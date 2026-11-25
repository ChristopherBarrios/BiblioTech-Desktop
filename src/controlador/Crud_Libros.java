package controlador;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import modelo.ClaseLibro;

public class Crud_Libros extends Conexion {

    private boolean isbnRepetido(String isbn, int idExcluir) throws Exception {
        String sql = "SELECT COUNT(*) FROM libro WHERE isbn = ? AND id_libro <> ?";
        try (PreparedStatement ps = super.getEnlace().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean insertar(ClaseLibro obj) {
        if (super.getEnlace() == null) {
            JOptionPane.showMessageDialog(null, "No hay conexión con la base de datos.");
            return false;
        }
        String sql = "INSERT INTO libro (id_libro, isbn, titulo, editorial, categoria) "
                + "VALUES ((SELECT COALESCE(MAX(id_libro), 0) + 1 FROM libro), ?, ?, ?, ?)";
        try {
            if (isbnRepetido(obj.getIsbn(), 0)) {
                JOptionPane.showMessageDialog(null, "Ya existe un libro con ese ISBN");
                return false;
            }
            try (PreparedStatement ps = super.getEnlace().prepareStatement(sql)) {
                ps.setString(1, obj.getIsbn());
                ps.setString(2, obj.getTitulo());
                ps.setString(3, obj.getEditorial());
                ps.setString(4, obj.getCategoria());
                if (ps.executeUpdate() == 1) {
                    JOptionPane.showMessageDialog(null, "Libro registrado correctamente");
                    return true;
                }
            }
            JOptionPane.showMessageDialog(null, "ERROR: NO REGISTRADO");
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            JOptionPane.showMessageDialog(null, "Error al registrar: " + error.getMessage());
        }
        return false;
    }

    public ResultSet revisar() {
        String sql = "SELECT id_libro, isbn, titulo, editorial, categoria "
                + "FROM libro ORDER BY id_libro";
        try {
            if (super.getEnlace() == null) {
                return null;
            }
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            return ps.executeQuery();
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            return null;
        }
    }

    // MODIFICAR
    public boolean modificar(ClaseLibro obj) {
        String sql = "UPDATE libro SET isbn=?, titulo=?, editorial=?, categoria=? WHERE id_libro=?";
        try {
            if (isbnRepetido(obj.getIsbn(), obj.getIdLibro())) {
                JOptionPane.showMessageDialog(null, "Ya existe otro libro con ese ISBN");
                return false;
            }
            try (PreparedStatement ps = super.getEnlace().prepareStatement(sql)) {
                ps.setString(1, obj.getIsbn());
                ps.setString(2, obj.getTitulo());
                ps.setString(3, obj.getEditorial());
                ps.setString(4, obj.getCategoria());
                ps.setInt(5, obj.getIdLibro());
                if (ps.executeUpdate() == 1) {
                    JOptionPane.showMessageDialog(null, "REGISTRO MODIFICADO");
                    return true;
                }
            }
            JOptionPane.showMessageDialog(null, "ERROR: NO MODIFICADO");
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            JOptionPane.showMessageDialog(null, "Error al modificar: " + error.getMessage());
        }
        return false;
    }

    // ELIMINAR
    public boolean eliminar(int idLibro) {
        try (PreparedStatement ps = super.getEnlace().prepareStatement(
                "DELETE FROM libro WHERE id_libro=?")) {
            ps.setInt(1, idLibro);
            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "REGISTRO ELIMINADO");
                return true;
            }
            JOptionPane.showMessageDialog(null, "ERROR: NO ELIMINADO");
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + error.getMessage());
        }
        return false;
    }
}