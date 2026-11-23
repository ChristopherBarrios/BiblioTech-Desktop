package vista;
import controlador.Crud_Multas;
import modelo.ClaseMulta;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import vista.Registro_Multas;

public class Consulta_Multas extends javax.swing.JFrame {
        // ===================== MODULO DE MULTAS (pegar dentro de la clase Consulta_Multas) =====================
 
    // Posicion de cada columna en la tabla
    private static final int COL_ID = 0;
    private static final int COL_NOMBRE = 1;
    private static final int COL_TELEFONO = 2;
 
    private final Crud_Multas crud = new Crud_Multas();
    private final DefaultTableModel estructura = new DefaultTableModel(new Object[]{
        "ID", "Nombre", "Telefono", "Libro", "Fecha de registro", "Fecha devolucion", "Multa"
    }, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            // Como antes: se editan los datos de la persona y luego se pulsa Modificar
            return columna == COL_NOMBRE || columna == COL_TELEFONO;
        }
    };
    private final JTable tblConsulta = new JTable(estructura);
    private final JButton btnModificar = new JButton("Modificar");
    private final JButton btnEliminar = new JButton("Eliminar");
 
    /** Se llama desde el constructor, justo despues de initComponents(). */
    private void iniciar() {
        setTitle("Registro De Multas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
 
        JLabel titulo = new JLabel("Registro De Multas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(0, 51, 102));
        titulo.setBorder(new EmptyBorder(12, 12, 12, 12));
 
        JPanel botones = new JPanel();
        botones.setBorder(new EmptyBorder(8, 0, 8, 0));
        botones.add(btnEliminar);
        btnEliminar.addActionListener(e -> eliminar());
 
        add(titulo, BorderLayout.NORTH);
        add(new JScrollPane(tblConsulta), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
 
        setSize(900, 420);
        setLocationRelativeTo(null);
    }
 
    /** Actualiza las multas (dias de retraso x $100) y llena la tabla. */
    public void mostrar() {
        estructura.setRowCount(0);
        try {
            crud.actualizarMultas();
            java.util.List<ClaseMulta> lista = crud.revisar();
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "NO hay registros");
                return;
            }
            LocalDate hoy = LocalDate.now();
            for (ClaseMulta m : lista) {
                LocalDate prestamo = LocalDate.parse(m.getFecha_prestamo());
                LocalDate devolucion = LocalDate.parse(m.getFecha_devolucion());
 
                String multa = "Sin multa";
                if (hoy.isAfter(devolucion)) {
                    long dias = ChronoUnit.DAYS.between(devolucion, hoy);
                    multa = "$" + m.getMonto() + " (" + dias + " días)";
                }
                estructura.addRow(new Object[]{
                    m.getId_pide(),
                    m.getNombre(),
                    m.getTel_cel(),
                    m.getTitulo() == null ? "" : m.getTitulo(),
                    prestamo.format(Registro_Multas.FORMATO),
                    devolucion.format(Registro_Multas.FORMATO),
                    multa
                });
            }
        } catch (SQLException ex) {
            System.out.println("ERROR" + ex);
            JOptionPane.showMessageDialog(this, "No se pudo consultar:\n" + ex.getMessage());
        }
    }
    private void eliminar() {
        int fila = tblConsulta.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un registro");
            return;
        }
        int id = Integer.parseInt(String.valueOf(estructura.getValueAt(fila, COL_ID)));
        String nombre = String.valueOf(estructura.getValueAt(fila, COL_NOMBRE));
 
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el registro de " + nombre + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            crud.eliminar(id);
            JOptionPane.showMessageDialog(this, "REGISTRO ELIMINADO");
            mostrar();
        } catch (SQLException ex) {
            System.out.println("ERROR" + ex);
            JOptionPane.showMessageDialog(this, "ERROR\n" + ex.getMessage());
        }
    }
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Consulta_Multas.class.getName());

    /**
     * Creates new form Consulta
     */
    public Consulta_Multas() {
        initComponents();
        iniciar();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Consulta_Multas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
