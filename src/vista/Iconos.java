package vista;

import java.awt.Window;
import java.net.URL;
import javax.swing.ImageIcon;

public class Iconos {
    public static void poner(Window ventana) {
        URL ruta = Iconos.class.getResource("/img/icono.png");
        if (ruta != null) {
            ventana.setIconImage(new ImageIcon(ruta).getImage());
        }
    }
}