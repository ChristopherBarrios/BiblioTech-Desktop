package controlador;

import modelo.Usuario;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import modelo.Usuario;


public class Crud extends Conexion {
    

        public void insertar(Usuario obj) {
        String sql = "INSERT INTO public.usuario (nombre, tel_cel, f_nac, adeudos) VALUES (?, ?, ?, 0)";
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            ps.setString(1, obj.getNombre());
            ps.setLong(2, obj.getTelefono());
            ps.setDate(3, obj.getFechaNac());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Usuario registrado correctamente");
            }
            ps.close();
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, "Error al registrar: " + error.getMessage());
        }
    }

    public void modificar(Usuario obj) {
        String sql = "UPDATE public.usuario SET nombre=?, tel_cel=?, f_nac=? WHERE id_usuario=?";
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            ps.setString(1, obj.getNombre());
            ps.setLong(2, obj.getTelefono());
            ps.setDate(3, obj.getFechaNac());
            ps.setInt(4, obj.getId());
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Usuario actualizado correctamente");
            }
            ps.close();
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + error.getMessage());
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM public.usuario WHERE id_usuario=?";
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            ps.setInt(1, id);
            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Usuario eliminado correctamente");
            }
            ps.close();
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + error.getMessage());
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
