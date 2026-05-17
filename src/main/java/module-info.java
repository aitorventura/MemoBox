module org.example.memobox {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;              // API JDBC estándar
    requires org.postgresql.jdbc;   // Driver PostgreSQL (necesario para que DriverManager lo encuentre en JPMS)

    // Abre paquetes al módulo javafx.fxml para reflexión en controladores
    opens org.example.memobox to javafx.fxml;
    opens org.example.memobox.controller to javafx.fxml;

    // Exporta paquetes públicos
    exports org.example.memobox;
    exports org.example.memobox.model;
    exports org.example.memobox.controller;
    exports org.example.memobox.service;
    exports org.example.memobox.repository;
    exports org.example.memobox.util;
}