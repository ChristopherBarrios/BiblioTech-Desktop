package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.ClaseMulta;

/**
 * Acceso a db_libreria para el módulo de multas.
 * Las multas NO se registran a mano: se generan solas a partir de la tabla
 * pide (préstamos) cuando pasa la fecha de devolución.
 */
public class Crud_Multas extends Conexion {

    /** Pesos de multa por cada día de retraso. */
    public static final int MULTA_POR_DIA = 100;

    // ------------------------------------------------------------------
    // GENERAR (automático), a partir de la tabla pide (préstamos):
    //  1) Quita la multa de los préstamos que ya no están vencidos
    //     (por ejemplo, si desde Consulta_Prestamo se amplió la fecha de devolución).
    //  2) Por cada préstamo vencido que todavía no tiene multa, inserta una
    //     en la tabla multa con monto = días de retraso x $100.
    // No toca las multas que ya existen y siguen vencidas (así se respetan las ediciones).
    // Devuelve cuántas multas nuevas se crearon.
    // ------------------------------------------------------------------
    public int generarMultasAutomaticas() throws SQLException {
        String quitar = "DELETE FROM multa WHERE id_pide IN "
                + "(SELECT id_pide FROM pide WHERE fecha_devolucion >= CURRENT_DATE)";

        String generar = "INSERT INTO multa (id_pide, id_usuario, id_libro, monto) "
                + "SELECT p.id_pide, p.id_usuario, p.id_libro, (CURRENT_DATE - p.fecha_devolucion) * ? "
                + "FROM pide p "
                + "WHERE p.fecha_devolucion < CURRENT_DATE "
                + "  AND p.id_usuario IS NOT NULL AND p.id_libro IS NOT NULL "
                + "  AND NOT EXISTS (SELECT 1 FROM multa m WHERE m.id_pide = p.id_pide) "
                + "ON CONFLICT (id_pide) DO NOTHING";

        return enTransaccion(cn -> {
            try (PreparedStatement ps = cn.prepareStatement(quitar)) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = cn.prepareStatement(generar)) {
                ps.setInt(1, MULTA_POR_DIA);
                return ps.executeUpdate();
            }
        });
    }

    // ------------------------------------------------------------------
    // REVISAR: todas las multas con su usuario, libro y fechas del préstamo.
    // ------------------------------------------------------------------
    public List<ClaseMulta> revisar() throws SQLException {
        String sql = "SELECT m.id_multa, m.id_pide, u.nombre, u.tel_cel, l.titulo, "
                + "       p.fecha_prestamo, p.fecha_devolucion, m.monto "
                + "FROM multa m "
                + "JOIN pide p ON p.id_pide = m.id_pide "
                + "JOIN usuario u ON u.id_usuario = m.id_usuario "
                + "JOIN libro l ON l.id_libro = m.id_libro "
                + "ORDER BY m.id_multa";

        List<ClaseMulta> lista = new ArrayList<>();
        Connection cn = conectar();
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ClaseMulta m = new ClaseMulta();
                m.setId_multa(rs.getInt("id_multa"));
                m.setId_pide(rs.getInt("id_pide"));
                m.setNombre(rs.getString("nombre"));
                m.setTel_cel(String.valueOf(rs.getLong("tel_cel")));
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
    // MODIFICAR: cambia el monto de una multa.
    // ------------------------------------------------------------------
    public void modificar(ClaseMulta m) throws SQLException {
        String sql = "UPDATE multa SET monto = ? WHERE id_multa = ?";

        enTransaccion(cn -> {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, m.getMonto());
                ps.setInt(2, m.getId_multa());
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se encontró la multa " + m.getId_multa());
                }
            }
            return null;
        });
    }

    // ------------------------------------------------------------------
    // ELIMINAR: borra la multa (no borra el préstamo).
    // ------------------------------------------------------------------
    public void eliminar(int idMulta) throws SQLException {
        String sql = "DELETE FROM multa WHERE id_multa = ?";

        enTransaccion(cn -> {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, idMulta);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se encontró la multa " + idMulta);
                }
            }
            return null;
        });
    }

    // ------------------------------------------------------------------
    // Utilidades internas
    // ------------------------------------------------------------------
    @FunctionalInterface
    private interface Accion<T> {
        T ejecutar(Connection cn) throws SQLException;
    }

    /** Ejecuta la operación y la confirma (commit); si algo falla, la deshace. */
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
