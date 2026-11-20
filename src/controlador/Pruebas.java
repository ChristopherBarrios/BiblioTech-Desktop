package controlador;

import java.sql.Date;
import modelo.Usuario;

public class Pruebas {
    public static void main(String[] args) {
        Conexion objConexion = new Conexion();
        Crud proceso = new Crud();
        proceso.insertar(new Usuario("Prueba", 5512345678L, Date.valueOf("2005-03-14")));
    }
}