package org.example.memobox.repository;

import org.example.memobox.model.Card;
import org.example.memobox.util.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardRepository {


    public static List<Card> getByDeck(UUID deckId) throws SQLException {
        PreparedStatement pstmt = null;
        String query = "SELECT * FROM cards WHERE deck_id = ? ORDER BY created_at ASC";
        List<Card> lista = new ArrayList<>();
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(query);
            pstmt.setObject(1, deckId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return lista;
    }


    public static List<Card> getDueToday(UUID deckId) throws SQLException {
        PreparedStatement pstmt = null;
        String query = "SELECT * FROM cards " +
                       "WHERE deck_id = ? AND next_review_at <= NOW() AND status != 'mastered' " +
                       "ORDER BY next_review_at ASC";
        List<Card> lista = new ArrayList<>();
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(query);
            pstmt.setObject(1, deckId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return lista;
    }

    public static List<Card> getAllDueToday() throws SQLException {
        PreparedStatement pstmt = null;
        String query = "SELECT c.* FROM cards c " +
                       "JOIN decks d ON d.id = c.deck_id " +
                       "WHERE d.user_id = ? AND c.next_review_at <= NOW() AND c.status != 'mastered' " +
                       "ORDER BY c.next_review_at ASC";
        List<Card> lista = new ArrayList<>();
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(query);
            pstmt.setObject(1, UserSession.getCurrentUser().getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return lista;
    }

    public static int countMastered() throws SQLException {
        PreparedStatement pstmt = null;
        String query = "SELECT COUNT(*) FROM cards c " +
                       "JOIN decks d ON d.id = c.deck_id " +
                       "WHERE d.user_id = ? AND c.status = 'mastered'";
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(query);
            pstmt.setObject(1, UserSession.getCurrentUser().getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return 0;
    }


    public static Card insertar(Card card) throws SQLException {
        PreparedStatement pstmt = null;
        String insertStr =
            "INSERT INTO cards (deck_id, question, answer, example, tag, " +
            "repetitions, interval_days, ease_factor, status, mastery_level, " +
            "times_correct, times_wrong, next_review_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING *";
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(insertStr);
            pstmt.setObject(1,    card.getDeckId());
            pstmt.setString(2,    card.getQuestion());
            pstmt.setString(3,    card.getAnswer());
            pstmt.setString(4,    card.getExample() != null  ? card.getExample()  : "");
            pstmt.setString(5,    card.getTag()     != null  ? card.getTag()      : "");
            pstmt.setInt(6,       card.getRepetitions());
            pstmt.setInt(7,       card.getIntervalDays());
            pstmt.setDouble(8,    card.getEaseFactor());
            pstmt.setString(9,    card.getStatus());
            pstmt.setInt(10,      card.getMasteryLevel());
            pstmt.setInt(11,      card.getTimesCorrect());
            pstmt.setInt(12,      card.getTimesWrong());
            pstmt.setTimestamp(13, Timestamp.valueOf(card.getNextReviewAt()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return null;
    }


    public static void actualizarContenido(UUID id, String question, String answer,
                                            String example, String tag) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "UPDATE cards SET question = ?, answer = ?, example = ?, tag = ? WHERE id = ?");
            pstmt.setString(1, question);
            pstmt.setString(2, answer);
            pstmt.setString(3, example != null ? example : "");
            pstmt.setString(4, tag     != null ? tag     : "");
            pstmt.setObject(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }


    public static void actualizarTrasRevision(Card card) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "UPDATE cards SET " +
                    "repetitions = ?, interval_days = ?, ease_factor = ?, " +
                    "status = ?, mastery_level = ?, times_correct = ?, times_wrong = ?, " +
                    "last_reviewed_at = ?, next_review_at = ? " +
                    "WHERE id = ?");
            pstmt.setInt(1,    card.getRepetitions());
            pstmt.setInt(2,    card.getIntervalDays());
            pstmt.setDouble(3, card.getEaseFactor());
            pstmt.setString(4, card.getStatus());
            pstmt.setInt(5,    card.getMasteryLevel());
            pstmt.setInt(6,    card.getTimesCorrect());
            pstmt.setInt(7,    card.getTimesWrong());
            pstmt.setTimestamp(8, card.getLastReviewedAt() != null
                    ? Timestamp.valueOf(card.getLastReviewedAt()) : null);
            pstmt.setTimestamp(9, card.getNextReviewAt() != null
                    ? Timestamp.valueOf(card.getNextReviewAt()) : null);
            pstmt.setObject(10, card.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }


    public static void borrar(UUID id) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "DELETE FROM cards WHERE id = ?");
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    private static Card mapRow(ResultSet rs) throws SQLException {
        Card c = new Card();
        c.setId(    (UUID) rs.getObject("id"));
        c.setDeckId((UUID) rs.getObject("deck_id"));
        c.setQuestion(rs.getString("question"));
        c.setAnswer(  rs.getString("answer"));
        c.setExample( rs.getString("example"));
        c.setTag(     rs.getString("tag"));
        c.setRepetitions( rs.getInt("repetitions"));
        c.setIntervalDays(rs.getInt("interval_days"));
        c.setEaseFactor(  rs.getDouble("ease_factor"));
        c.setStatus(      rs.getString("status"));
        c.setMasteryLevel(rs.getInt("mastery_level"));
        c.setTimesCorrect(rs.getInt("times_correct"));
        c.setTimesWrong(  rs.getInt("times_wrong"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp lastReview = rs.getTimestamp("last_reviewed_at");
        Timestamp nextReview = rs.getTimestamp("next_review_at");

        if (created    != null) c.setCreatedAt(created.toLocalDateTime());
        if (lastReview != null) c.setLastReviewedAt(lastReview.toLocalDateTime());
        if (nextReview != null) c.setNextReviewAt(nextReview.toLocalDateTime());

        return c;
    }
}
