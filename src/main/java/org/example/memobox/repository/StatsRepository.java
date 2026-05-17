package org.example.memobox.repository;

import org.example.memobox.model.StudySession;
import org.example.memobox.model.UserStats;
import org.example.memobox.util.UserSession;

import java.sql.*;
import java.util.UUID;

public class StatsRepository {

    public static UserStats getStats() throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "SELECT * FROM user_stats WHERE user_id = ?");
            pstmt.setObject(1, UserSession.getCurrentUser().getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowStats(rs);
            }
            return new UserStats(); // no debería ocurrir (se crea al registrar)
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    public static void actualizarStats(UserStats stats) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "UPDATE user_stats SET " +
                    "current_streak = ?, best_streak = ?, last_study_date = ?, " +
                    "studied_today = ?, total_reviews = ?, total_correct = ?, total_wrong = ? " +
                    "WHERE user_id = ?");
            pstmt.setInt(1, stats.getCurrentStreak());
            pstmt.setInt(2, stats.getBestStreak());
            pstmt.setDate(3, stats.getLastStudyDate() != null
                    ? Date.valueOf(stats.getLastStudyDate()) : null);
            pstmt.setInt(4, stats.getStudiedToday());
            pstmt.setInt(5, stats.getTotalReviews());
            pstmt.setInt(6, stats.getTotalCorrect());
            pstmt.setInt(7, stats.getTotalWrong());
            pstmt.setObject(8, UserSession.getCurrentUser().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    public static void guardarSesion(StudySession sesion) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "INSERT INTO study_sessions " +
                    "(user_id, deck_id, session_date, reviewed_count, correct_count, wrong_count, duration_seconds) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");

            pstmt.setObject(1, UserSession.getCurrentUser().getId());

            if (sesion.getDeckId() != null) {
                pstmt.setObject(2, sesion.getDeckId());
            } else {
                pstmt.setNull(2, Types.OTHER);
            }
            pstmt.setDate(3, Date.valueOf(sesion.getSessionDate()));
            pstmt.setInt(4, sesion.getReviewedCount());
            pstmt.setInt(5, sesion.getCorrectCount());
            pstmt.setInt(6, sesion.getWrongCount());
            pstmt.setInt(7, sesion.getDurationSeconds());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    private static UserStats mapRowStats(ResultSet rs) throws SQLException {
        UserStats s = new UserStats();
        s.setUserId((UUID) rs.getObject("user_id"));
        s.setCurrentStreak(rs.getInt("current_streak"));
        s.setBestStreak(   rs.getInt("best_streak"));
        s.setStudiedToday( rs.getInt("studied_today"));
        s.setTotalReviews( rs.getInt("total_reviews"));
        s.setTotalCorrect( rs.getInt("total_correct"));
        s.setTotalWrong(   rs.getInt("total_wrong"));

        Date lastStudy = rs.getDate("last_study_date");
        if (lastStudy != null) s.setLastStudyDate(lastStudy.toLocalDate());

        return s;
    }
}
