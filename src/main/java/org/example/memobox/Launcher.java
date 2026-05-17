package org.example.memobox;

/**
 * Clase Launcher sin módulo para permitir ejecutar el JAR sin --module-path.
 * Delega en MemoBoxApp que extiende Application.
 */
public class Launcher {
    public static void main(String[] args) {
        MemoBoxApp.main(args);
    }
}
