package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class Menu_Principal extends JFrame {

    private static final Color AZUL = new Color(0, 0, 102);
    private static final Color CREMA = new Color(255, 255, 204);

    public Menu_Principal() {
        Iconos.poner(this);
        setTitle("Libreria - Menu principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE); // cerrar el menu cierra todo el programa
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(AZUL);


        JLabel titulo = new JLabel("Sistema de Libreria", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // ---------- Modulos ----------
        JPanel centro = new JPanel(new GridLayout(1, 0, 15, 15));
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(10, 20, 10, 20));

        centro.add(modulo("Usuarios",
                boton("Registrar usuario", () -> abrir(new Registro_Usuarios())),
                boton("Consultar usuarios", () -> abrir(new Consulta_Usuarios()))));


        centro.add(modulo("Libros",
                boton("Registrar libro", () -> abrir(new Registro_Libros())),
                boton("Consultar libros", () -> abrir(new Consulta_Libros()))));

        centro.add(modulo("Prestamos",
                boton("Registrar / devolver", () -> abrir(new Gestion_Prestamos())),
                boton("Consultar prestamos", () -> abrir(new Consulta_Prestamo()))));

        centro.add(modulo("Multas",
                boton("Consultar multas", () -> {
                    Consulta_Multas m = new Consulta_Multas();
                    m.mostrar();          // genera las multas y llena la tabla
                    m.setVisible(true);
                })));

        add(centro, BorderLayout.CENTER);


        JButton salir = boton("Salir", () -> System.exit(0));
        salir.setBackground(new Color(255, 102, 102));
        JPanel sur = new JPanel();
        sur.setOpaque(false);
        sur.setBorder(new EmptyBorder(5, 0, 15, 0));
        sur.add(salir);
        add(sur, BorderLayout.SOUTH);

        setSize(950, 330);
        setLocationRelativeTo(null);
    }



    private void abrir(JFrame ventana) {
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    private void enConstruccion(String nombre) {
        JOptionPane.showMessageDialog(this, "El modulo de " + nombre + " aun esta en construccion.");
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(new Color(204, 255, 204));
        b.setForeground(new Color(0, 102, 0));
        b.setFocusPainted(false);
        b.addActionListener(e -> accion.run());
        return b;
    }

    private JPanel modulo(String nombre, JButton... botones) {
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.setBackground(CREMA);
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lbl = new JLabel(nombre, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 20));
        lbl.setForeground(AZUL);
        p.add(lbl);

        for (JButton b : botones) {
            p.add(b);
        }
        return p;
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Menu_Principal().setVisible(true));
    }
}