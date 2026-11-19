package controlador;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Crud extends Conexion {

    public void insertar(String nombre, long telefono, Date fechaNac) {
        String sql = "INSERT INTO public.usuario (nombre, tel_cel, f_nac, adeudos) VALUES (?, ?, ?, 0)";

        try {
            // Verifica que la conexión no haya llegado nula
            if (super.getEnlace() == null) {
                JOptionPane.showMessageDialog(null, "No hay conexión con la base de datos.");
                return;
            }

            PreparedStatement ps = super.getEnlace().prepareStatement(sql);

            ps.setString(1, nombre);
            ps.setLong(2, telefono);
            ps.setDate(3, fechaNac);

            int res = ps.executeUpdate();

            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Usuario registrado correctamente");
            }

            ps.close();

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + error.getMessage());
        }
    }

    public ResultSet consultar() {
        String sql = "SELECT * FROM public.usuario";

        try {
            if (super.getEnlace() == null) {
                return null;
            }
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            return ps.executeQuery();

        } catch (SQLException error) {
            System.out.println("Error al consultar: " + error.toString());
            return null;
        }
    }
}