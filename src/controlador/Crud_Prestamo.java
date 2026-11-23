package controlador;

import controlador.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import modelo.ClasePrestamos;

public class Crud_Prestamo extends Conexion {

    // El usuario se busca por ID (devuelve -1 si no existe o no es número)
    private int buscarUsuario(String idTexto) {
        try {
            int id = Integer.parseInt(idTexto.trim());
            PreparedStatement ps = super.getEnlace().prepareStatement(
                    "SELECT id_usuario FROM usuario WHERE id_usuario = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (NumberFormatException e) {
            return -1;
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
        }
        return -1;
    }

    // El libro se busca por título
    private int buscarLibro(String titulo) {
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(
                    "SELECT id_libro FROM libro WHERE LOWER(titulo) = LOWER(?) LIMIT 1");
            ps.setString(1, titulo.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
        }
        return -1;
    }

    // Acepta yyyy/MM/dd y dd/MM/yy (null si es inválida)
   // Acepta yyyy-MM-dd y yyyy/MM/dd (null si es inválida)
public java.sql.Date procesoFecha(String texto) {
    if (texto == null) {
        return null;
    }
    String[] formatos = {"yyyy-MM-dd", "yyyy/MM/dd"};
    for (String f : formatos) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(f);
            sdf.setLenient(false);
            java.util.Date fecha = sdf.parse(texto.trim());
            return new java.sql.Date(fecha.getTime());
        } catch (Exception error) {
            // prueba con el siguiente formato
        }
    }
    System.out.println("Error al leer fecha");
    return null;
}
    public boolean insertar(ClasePrestamos objClase) {
        int idUsuario = buscarUsuario(objClase.getUsuario());
        if (idUsuario == -1) {
            JOptionPane.showMessageDialog(null, "El usuario no está registrado");
            return false;
        }
        int idLibro = buscarLibro(objClase.getLibro());
        if (idLibro == -1) {
            JOptionPane.showMessageDialog(null, "El libro no existe");
            return false;
        }
        java.sql.Date fPrestamo = procesoFecha(objClase.getFechaPrestamo());
        java.sql.Date fDevolucion = procesoFecha(objClase.getFechaDevolucion());
        if (fPrestamo == null || fDevolucion == null) {
            JOptionPane.showMessageDialog(null, "Fecha inválida. Usa el formato dd/MM/yyyy");
            return false;
        }

        String sqlPrestado = "SELECT COUNT(*) FROM pide WHERE id_libro = ?";
        // pide.id_pide no es autoincremental: se calcula el siguiente
        String sql = "INSERT INTO pide(id_pide, fecha_prestamo, fecha_devolucion, id_libro, id_usuario) "
                + "VALUES((SELECT COALESCE(MAX(id_pide), 0) + 1 FROM pide), ?, ?, ?, ?)";
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(sqlPrestado);
            ps.setInt(1, idLibro);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null, "El libro ya está prestado");
                return false;
            }

            ps = super.getEnlace().prepareStatement(sql);
            ps.setDate(1, fPrestamo);
            ps.setDate(2, fDevolucion);
            ps.setInt(3, idLibro);
            ps.setInt(4, idUsuario);
            int res = ps.executeUpdate();
            if (res == 1) {
                JOptionPane.showMessageDialog(null, "REGISTRADO");
                return true;
            }
            JOptionPane.showMessageDialog(null, "ERROR: NO REGISTRADO");
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            JOptionPane.showMessageDialog(null, "ERROR: " + error.getMessage());
        }
        return false;
    }

    // Préstamos vigentes: id_pide, titulo, fecha_prestamo, fecha_devolucion
    public ResultSet revisar() {
        String sql = "SELECT p.id_pide, COALESCE(l.titulo, '') AS titulo, "
                + "p.fecha_prestamo, p.fecha_devolucion "
                + "FROM pide p LEFT JOIN libro l ON l.id_libro = p.id_libro "
                + "ORDER BY p.id_pide";
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            return ps.executeQuery();
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            return null;
        }
    }

    public boolean modificar(ClasePrestamos objClase) {
        int idLibro = buscarLibro(objClase.getLibro());
        if (idLibro == -1) {
            JOptionPane.showMessageDialog(null, "El libro no existe");
            return false;
        }
        java.sql.Date fPrestamo = procesoFecha(objClase.getFechaPrestamo());
        java.sql.Date fDevolucion = procesoFecha(objClase.getFechaDevolucion());
        if (fPrestamo == null || fDevolucion == null) {
            JOptionPane.showMessageDialog(null, "Fecha inválida. Usa el formato yyyy/MM/dd");
            return false;
        }

        String sql = "UPDATE pide SET id_libro=?, fecha_prestamo=?, fecha_devolucion=? WHERE id_pide=?";
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement(sql);
            ps.setInt(1, idLibro);
            ps.setDate(2, fPrestamo);
            ps.setDate(3, fDevolucion);
            ps.setInt(4, objClase.getIdPide());
            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "REGISTRO MODIFICADO");
                return true;
            }
            JOptionPane.showMessageDialog(null, "ERROR: NO MODIFICADO");
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            
        }
        return false;
    }

    public void eliminar(int idPide) {
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement("DELETE FROM pide WHERE id_pide=?");
            ps.setInt(1, idPide);
            if (ps.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "REGISTRO ELIMINADO");
            } else {
                JOptionPane.showMessageDialog(null, "ERROR: NO ELIMINADO");
            }
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            
        }
    }

    // Devolver = quitar el préstamo vigente de ese libro
    public void devolver(String libro) {
        int idLibro = buscarLibro(libro);
        if (idLibro == -1) {
            JOptionPane.showMessageDialog(null, "El libro no existe");
            return;
        }
        try {
            PreparedStatement ps = super.getEnlace().prepareStatement("DELETE FROM pide WHERE id_libro=?");
            ps.setInt(1, idLibro);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "LIBRO DEVUELTO");
            } else {
                JOptionPane.showMessageDialog(null, "NO HAY UN PRÉSTAMO PENDIENTE CON ESE LIBRO");
            }
        } catch (Exception error) {
            System.out.println("ERROR: " + error.toString());
            
        }
    }
}