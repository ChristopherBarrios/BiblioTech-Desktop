/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import controlador.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.ClaseMulta;

/**
 * Acceso a db_libreria para el módulo de multas.
 * Mantiene los mismos métodos de antes: insertar, revisar, modificar y eliminar.
 */
public class Crud_Multas extends Conexion {

    /** Pesos de multa por cada día de retraso (misma regla de tu versión original). */
    public static final int MULTA_POR_DIA = 100;

    /** Elemento para el JComboBox: guarda el id y muestra un texto. */
    public static class Opcion {
        public final int id;
        public final String texto;

        public Opcion(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    // ------------------------------------------------------------------
    // Apoyo para el formulario
    // ------------------------------------------------------------------

    /** Lista de libros para el combo (la tabla multa necesita un id_libro). */
    public List<Opcion> listarLibros() throws SQLException {
        String sql = "SELECT id_libro, titulo FROM libro ORDER BY titulo";
        List<Opcion> lista = new ArrayList<>();
        Connection cn = conectar();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Opcion(rs.getInt(1), rs.getInt(1) + " - " + rs.getString(2)));
            }
        }
        return lista;
    }

    /** Busca al usuario por nombre y teléfono. Devuelve su id, o null si no existe. */
    public Integer buscarUsuario(String nombre, String tel) throws SQLException {
        String sql = "SELECT id_usuario FROM usuario WHERE LOWER(nombre) = LOWER(?) AND tel_cel = ? LIMIT 1";
        Connection cn = conectar();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setLong(2, Long.parseLong(tel));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        }
    }

    // ------------------------------------------------------------------
    // INSERTAR: guarda el préstamo en "pide" y su multa en "multa".
    // Devuelve el monto de la multa (0 si todavía no hay retraso).
    // ------------------------------------------------------------------
    public int insertar(ClaseMulta m) throws SQLException {
        // pide.id_pide NO es autonumérico en tu base, por eso se calcula MAX + 1.
        String sql = "INSERT INTO pide (id_pide, fecha_prestamo, fecha_devolucion, id_libro, id_usuario) "
                + "VALUES ((SELECT COALESCE(MAX(id_pide), 0) + 1 FROM pide), ?, ?, ?, ?) "
                + "RETURNING id_pide";

        return enTransaccion(cn -> {
            int idPide;
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(m.getFecha_prestamo()));
                ps.setDate(2, Date.valueOf(m.getFecha_devolucion()));
                ps.setInt(3, m.getId_libro());
                ps.setInt(4, m.getId_usuario());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idPide = rs.getInt(1);
                }
            }
            sincronizarMultas(cn);
            return montoDe(cn, idPide);
        });
    }

    // ------------------------------------------------------------------
    // REVISAR: todos los registros con su usuario, libro y multa.
    // ------------------------------------------------------------------
    public List<ClaseMulta> revisar() throws SQLException {
        String sql = "SELECT p.id_pide, p.id_usuario, u.nombre, u.tel_cel, "
                + "       p.id_libro, l.titulo, p.fecha_prestamo, p.fecha_devolucion, "
                + "       COALESCE(m.monto, 0) AS monto "
                + "FROM pide p "
                + "JOIN usuario u ON u.id_usuario = p.id_usuario "
                + "LEFT JOIN libro l ON l.id_libro = p.id_libro "
                + "LEFT JOIN multa m ON m.id_pide = p.id_pide "
                + "ORDER BY p.id_pide";

        List<ClaseMulta> lista = new ArrayList<>();
        Connection cn = conectar();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ClaseMulta m = new ClaseMulta();
                m.setId_pide(rs.getInt("id_pide"));
                m.setId_usuario(rs.getInt("id_usuario"));
                m.setNombre(rs.getString("nombre"));
                m.setTel_cel(String.valueOf(rs.getLong("tel_cel")));
                m.setId_libro(rs.getInt("id_libro"));
                m.setTitulo(rs.getString("titulo"));
                m.setFecha_prestamo(rs.getDate("fecha_prestamo").toLocalDate().toString());
                m.setFecha_devolucion(rs.getDate("fecha_devolucion").toLocalDate().toString());
                m.setMonto(rs.getInt("monto"));
                lista.add(m);
            }
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // MODIFICAR: igual que antes, cambia Nombre y Teléfono de la persona
    // (ahora en la tabla usuario, usando el id del préstamo seleccionado).
    // ------------------------------------------------------------------
    public void modificar(ClaseMulta m) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, tel_cel = ? "
                + "WHERE id_usuario = (SELECT id_usuario FROM pide WHERE id_pide = ?)";

        enTransaccion(cn -> {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, m.getNombre());
                ps.setLong(2, Long.parseLong(m.getTel_cel()));
                ps.setInt(3, m.getId_pide());
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se encontró el registro " + m.getId_pide());
                }
            }
            return null;
        });
    }

    // ------------------------------------------------------------------
    // ELIMINAR: borra el registro (préstamo); su multa se borra sola
    // por el ON DELETE CASCADE de la base.
    // ------------------------------------------------------------------
    public void eliminar(int idPide) throws SQLException {
        String sql = "DELETE FROM pide WHERE id_pide = ?";

        enTransaccion(cn -> {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idPide);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se encontró el registro " + idPide);
                }
            }
            return null;
        });
    }

    // ------------------------------------------------------------------
    // MULTAS: misma regla de antes (días de retraso x $100).
    // Deja cada préstamo con su fila en "multa"; si no hay retraso, monto = 0.
    // ------------------------------------------------------------------
    public void actualizarMultas() throws SQLException {
        enTransaccion(cn -> {
            sincronizarMultas(cn);
            return null;
        });
    }

    private void sincronizarMultas(Connection cn) throws SQLException {
        String sql = "INSERT INTO multa (id_pide, id_usuario, id_libro, monto) "
                + "SELECT id_pide, id_usuario, id_libro, GREATEST(CURRENT_DATE - fecha_devolucion, 0) * ? "
                + "FROM pide "
                + "WHERE id_usuario IS NOT NULL AND id_libro IS NOT NULL "
                + "ON CONFLICT (id_pide) DO UPDATE SET monto = EXCLUDED.monto";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, MULTA_POR_DIA);
            ps.executeUpdate();
        }
    }

    private int montoDe(Connection cn, int idPide) throws SQLException {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM multa WHERE id_pide = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPide);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // ------------------------------------------------------------------
    // Utilidades internas
    // ------------------------------------------------------------------
    @FunctionalInterface
    private interface Accion<T> {
        T ejecutar(Connection cn) throws SQLException;
    }

    /** Ejecuta varias sentencias como una sola operación: o se guardan todas o ninguna. */
    private <T> T enTransaccion(Accion<T> accion) throws SQLException {
        Connection cn = conectar();
        boolean autoCommit = cn.getAutoCommit();
        try {
            cn.setAutoCommit(false);
            T resultado = accion.ejecutar(cn);
            cn.commit();
            return resultado;
        } catch (SQLException | RuntimeException e) {
            cn.rollback();
            throw e;
        } finally {
            cn.setAutoCommit(autoCommit);
        }
    }

    /** Usa el mismo getEnlace() de tu clase Conexion. */
    private Connection conectar() throws SQLException {
        Connection cn;
        try {
            cn = super.getEnlace();
        } catch (Exception e) {
            throw new SQLException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
        if (cn == null) {
            throw new SQLException("No se pudo conectar a la base de datos. Revisa la clase Conexion.");
        }
        return cn;
    }
}
