package org.example.memobox.repository;

import org.example.memobox.model.User;

import java.sql.*;
import java.util.UUID;

public class UserRepository {

    public static User login(String username, String password) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "SELECT id, username, email FROM users " +
                    "WHERE username = ? AND password_hash = crypt(?, password_hash)");
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
            return null; // credenciales incorrectas
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    public static User register(String username, String email, String password) throws SQLException {
        PreparedStatement pstmtUser = null;
        PreparedStatement pstmtStats = null;
        try {
            // Insertar usuario con hash bcrypt
            pstmtUser = ConexionBD.getConexion().prepareStatement(
                    "INSERT INTO users(username, email, password_hash) " +
                    "VALUES(?, ?, crypt(?, gen_salt('bf'))) " +
                    "RETURNING id, username, email");
            pstmtUser.setString(1, username);
            pstmtUser.setString(2, email);
            pstmtUser.setString(3, password);
            ResultSet rs = pstmtUser.executeQuery();

            if (rs.next()) {
                User user = mapRow(rs);

                // Crear fila inicial en user_stats
                pstmtStats = ConexionBD.getConexion().prepareStatement(
                        "INSERT INTO user_stats(user_id) VALUES(?) ON CONFLICT (user_id) DO NOTHING");
                pstmtStats.setObject(1, user.getId());
                pstmtStats.executeUpdate();

                return user;
            }
            return null;
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmtUser  != null) pstmtUser.close();
            if (pstmtStats != null) pstmtStats.close();
        }
    }

    public static boolean existeUsername(String username) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "SELECT 1 FROM users WHERE username = ?");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        return new User(
                (UUID) rs.getObject("id"),
                rs.getString("username"),
                rs.getString("email")
        );
    }
}
