package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    private Connection enlace;

    public Conexion() {
        String ruta = "jdbc:postgresql://localhost:5432/db_libreria";
        try {
            Class.forName("org.postgresql.Driver");
            enlace = DriverManager.getConnection(ruta, "postgres", "admin");
        } catch (ClassNotFoundException | SQLException error) {
            
            
            JOptionPane.showMessageDialog(null, "Error al conectar con PostgreSQL:\n" + error.getMessage());
        }
    }

    public Connection getEnlace() {
        return enlace;
    }
}