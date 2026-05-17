package org.example.memobox.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexión JDBC con la base de datos PostgreSQL de Supabase.
 *
 * Patrón: conexión única reutilizable (singleton de conexión).
 * Si la conexión se cierra o pierde, se vuelve a abrir automáticamente.
 *
 * Los repositorios llaman a ConexionBD.getConexion() para obtener la conexión
 * y solo cierran el Statement/PreparedStatement en el bloque finally.
 */
public class ConexionBD {

    private static final String CONFIG_FILE = "/org/example/memobox/supabase.properties";

    private static final String url;
    private static final String user;
    private static final String password;

    private static Connection conexion = null;

    static {
        // Necesario para Supabase en proyectos nuevos (IPv6)
        System.setProperty("java.net.preferIPv6Addresses", "true");

        Properties props = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream(CONFIG_FILE)) {
            if (in == null) throw new RuntimeException("Falta archivo configuracion: " + CONFIG_FILE);
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando properties", e);
        }
        url = props.getProperty("DB_URL", "").trim();
        user = props.getProperty("DB_USER", "postgres").trim();
        password = props.getProperty("DB_PASSWORD", "").trim();
    }

    public static synchronized Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            System.out.println("Conectando a: " + url);
            conexion = DriverManager.getConnection(url, user, password);
        }
        return conexion;
    }

    public static void printSQLException(SQLException ex) {
        ex.printStackTrace();
        System.err.println("SQLState: " + ex.getSQLState());
        System.err.println("Error: " + ex.getMessage());
    }

    public static void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) conexion.close();
        } catch (SQLException ignored) {}
    }

    private ConexionBD() {}
}
