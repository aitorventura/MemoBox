package org.example.memobox.repository;

import org.example.memobox.model.Deck;
import org.example.memobox.util.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckRepository {

    public static List<Deck> getAll() throws SQLException {
        PreparedStatement pstmt = null;
        String query = "SELECT id, name, description, created_at FROM decks " +
                       "WHERE user_id = ? ORDER BY created_at ASC";
        List<Deck> lista = new ArrayList<>();
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

    public static Deck insertar(String nombre, String descripcion) throws SQLException {
        PreparedStatement pstmt = null;
        String insertStr = "INSERT INTO decks (user_id, name, description) " +
                           "VALUES (?, ?, ?) RETURNING id, name, description, created_at";
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(insertStr);
            pstmt.setObject(1, UserSession.getCurrentUser().getId());
            pstmt.setString(2, nombre);
            pstmt.setString(3, descripcion != null ? descripcion : "");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
        return null;
    }

    public static void actualizar(UUID id, String nombre, String descripcion) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "UPDATE decks SET name = ?, description = ? WHERE id = ? AND user_id = ?");
            pstmt.setString(1, nombre);
            pstmt.setString(2, descripcion != null ? descripcion : "");
            pstmt.setObject(3, id);
            pstmt.setObject(4, UserSession.getCurrentUser().getId());
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
                    "DELETE FROM decks WHERE id = ? AND user_id = ?");
            pstmt.setObject(1, id);
            pstmt.setObject(2, UserSession.getCurrentUser().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            ConexionBD.printSQLException(e);
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    public static int contarTarjetas(UUID deckId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "SELECT COUNT(*) FROM cards WHERE deck_id = ?");
            pstmt.setObject(1, deckId);
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

    public static int contarPendientes(UUID deckId) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = ConexionBD.getConexion().prepareStatement(
                    "SELECT COUNT(*) FROM cards " +
                    "WHERE deck_id = ? AND next_review_at <= NOW() AND status != 'mastered'");
            pstmt.setObject(1, deckId);
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

    private static Deck mapRow(ResultSet rs) throws SQLException {
        Deck d = new Deck();
        d.setId((UUID) rs.getObject("id"));
        d.setName(rs.getString("name"));
        d.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) d.setCreatedAt(ts.toLocalDateTime());
        return d;
    }
}
