package vista;
import controlador.Crud_Multas;
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
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import modelo.ClaseMulta;
public class Registro_Multas extends javax.swing.JFrame {
    // ===================== MODULO DE MULTAS (pegar dentro de la clase Registro_Multas) =====================
 
    /** Formato de fecha que se escribe en pantalla (dd/MM/yyyy). */
    static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
 
    Consulta_Multas con;
    private final Crud_Multas crud = new Crud_Multas();
 
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(20);
    private final JComboBox<Crud_Multas.Opcion> cmbLibro = new JComboBox<>();
    private final JTextField txtFechaPrestamo = new JTextField(20);
    private final JTextField txtFechaDevolucion = new JTextField(20);
    private final JButton btnRegistrar = new JButton("Registrar");
    private final JMenuItem mniConsulta = new JMenuItem("Consulta");
    private final JMenuItem mniActualizar = new JMenuItem("Actualizar libros");
 
    /** Se llama desde el constructor, justo despues de initComponents(). */
    private void iniciar() {
        setTitle("Registro De Multas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        construirPantalla();
        cargarLibros();
        pack();
        setLocationRelativeTo(null);
    }
 
    private void construirPantalla() {
        JLabel titulo = new JLabel("Registro De Multas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(new Color(0, 51, 102));
        titulo.setBorder(new EmptyBorder(12, 12, 12, 12));
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 8, 20));
        agregarFila(form, 0, "Nombre", txtNombre);
        agregarFila(form, 1, "Telefono", txtTelefono);
        agregarFila(form, 2, "Libro", cmbLibro);
        agregarFila(form, 3, "Fecha de prestamo", txtFechaPrestamo);
        agregarFila(form, 4, "Fecha Devolucion", txtFechaDevolucion);
        txtFechaPrestamo.setToolTipText("dd/MM/yyyy, por ejemplo 15/11/2026");
        txtFechaDevolucion.setToolTipText("dd/MM/yyyy, por ejemplo 15/11/2026");
        cmbLibro.setPreferredSize(new Dimension(txtNombre.getPreferredSize().width, 26));
 
        JPanel abajo = new JPanel();
        abajo.setBorder(new EmptyBorder(4, 0, 14, 0));
        abajo.add(btnRegistrar);
        btnRegistrar.addActionListener(e -> registrar());
 
        JMenu menu = new JMenu("Opciones");
        menu.add(mniConsulta);
        menu.add(mniActualizar);
        JMenuBar barra = new JMenuBar();
        barra.add(menu);
        setJMenuBar(barra);
        mniConsulta.addActionListener(e -> abrirConsulta());
        mniActualizar.addActionListener(e -> cargarLibros());
 
        add(titulo, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
    }
 
    private void agregarFila(JPanel panel, int fila, String texto, java.awt.Component campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
 
        c.gridx = 0;
        panel.add(new JLabel(texto), c);
 
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        panel.add(campo, c);
    }
 
    /** Llena el combo con los libros de la tabla libro. */
    private void cargarLibros() {
        try {
            cmbLibro.setModel(new DefaultComboBoxModel<>(crud.listarLibros().toArray(new Crud_Multas.Opcion[0])));
        } catch (SQLException ex) {
            System.out.println("ERROR" + ex);
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los libros:\n" + ex.getMessage());
        }
    }
 
    private void abrirConsulta() {
        if (con == null) {
            con = new Consulta_Multas();
        }
        con.setVisible(true);
        con.mostrar();
    }
 
    /**
     * Validacion de siempre: nombre con texto y telefono solo con numeros.
     * Devuelve el mensaje de error, o null si todo esta bien.
     * (nombre es varchar(30) y tel_cel es bigint en la base)
     */
    static String validarPersona(String nombre, String tel) {
        if (nombre.isEmpty() || !tel.matches("[0-9]+")) {
            return "Escribe el nombre y solo números en el teléfono";
        }
        if (nombre.length() > 30) {
            return "El nombre admite máximo 30 caracteres";
        }
        if (tel.length() > 18) {
            return "El teléfono es demasiado largo";
        }
        return null;
    }
 
    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String tel = txtTelefono.getText().trim();
 
        String error = validarPersona(nombre, tel);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error);
            return;
        }
 
        LocalDate prestamo;
        LocalDate devolucion;
        try {
            prestamo = LocalDate.parse(txtFechaPrestamo.getText().trim(), FORMATO);
            devolucion = LocalDate.parse(txtFechaDevolucion.getText().trim(), FORMATO);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Usa el formato dd/MM/yyyy, por ejemplo 15/11/2026");
            return;
        }
        if (devolucion.isBefore(prestamo)) {
            JOptionPane.showMessageDialog(this, "La devolución no puede ser antes del préstamo");
            return;
        }
 
        Crud_Multas.Opcion libro = (Crud_Multas.Opcion) cmbLibro.getSelectedItem();
        if (libro == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un libro.\nSi la lista está vacía, primero registra libros en la base.");
            return;
        }
 
        try {
            // La base exige que la persona ya exista en la tabla usuario
            Integer idUsuario = crud.buscarUsuario(nombre, tel);
            if (idUsuario == null) {
                JOptionPane.showMessageDialog(this,
                        "No existe un usuario con ese nombre y teléfono.\nRegístralo primero como usuario.");
                return;
            }
 
            ClaseMulta m = new ClaseMulta();
            m.setId_usuario(idUsuario);
            m.setId_libro(libro.id);
            m.setFecha_prestamo(prestamo.toString());
            m.setFecha_devolucion(devolucion.toString());
 
            int monto = crud.insertar(m);
            System.out.println("REGISTRADO");
            if (monto > 0) {
                JOptionPane.showMessageDialog(this, "Multa registrada correctamente: $" + monto);
            } else {
                JOptionPane.showMessageDialog(this, "Multa registrada correctamente (sin retraso por ahora)");
            }
 
            txtNombre.setText("");
            txtTelefono.setText("");
            txtFechaPrestamo.setText("");
            txtFechaDevolucion.setText("");
            if (con != null && con.isVisible()) {
                con.mostrar();
            }
        } catch (SQLException ex) {
            System.out.println("ERROR NO REGISTRADO " + ex);
            JOptionPane.showMessageDialog(this, "No se pudo registrar. Revisa la consola.\n" + ex.getMessage());
        }
    }
 
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Registro_Multas.class.getName());

   
    public Registro_Multas() {
        initComponents();
        iniciar();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuBar1 = new javax.swing.JMenuBar();

        jMenuItem2.setText("jMenuItem2");

        jMenuItem3.setText("jMenuItem3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
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
        java.awt.EventQueue.invokeLater(() -> new Registro_Multas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    // End of variables declaration//GEN-END:variables
}
