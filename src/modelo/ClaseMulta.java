/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 * Un registro del módulo de multas.
 * Junta datos de las tablas pide (préstamo), usuario, libro y multa.
 * Las fechas se guardan como texto yyyy-MM-dd, igual que en tu versión original.
 */
public class ClaseMulta {

    // pide / multa
    private int Id_pide;
    private int Id_usuario;
    private int Id_libro;
    private String Fecha_prestamo;
    private String Fecha_devolucion;
    private int Monto;

    // usuario
    private String Nombre;
    private String Tel_cel;

    // libro (solo para mostrar)
    private String Titulo;

    public ClaseMulta() {
    }

    public int getId_pide() {
        return Id_pide;
    }

    public void setId_pide(int Id_pide) {
        this.Id_pide = Id_pide;
    }

    public int getId_usuario() {
        return Id_usuario;
    }

    public void setId_usuario(int Id_usuario) {
        this.Id_usuario = Id_usuario;
    }

    public int getId_libro() {
        return Id_libro;
    }

    public void setId_libro(int Id_libro) {
        this.Id_libro = Id_libro;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getTel_cel() {
        return Tel_cel;
    }

    public void setTel_cel(String Tel_cel) {
        this.Tel_cel = Tel_cel;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String Titulo) {
        this.Titulo = Titulo;
    }

    public String getFecha_prestamo() {
        return Fecha_prestamo;
    }

    public void setFecha_prestamo(String Fecha_prestamo) {
        this.Fecha_prestamo = Fecha_prestamo;
    }

    public String getFecha_devolucion() {
        return Fecha_devolucion;
    }

    public void setFecha_devolucion(String Fecha_devolucion) {
        this.Fecha_devolucion = Fecha_devolucion;
    }

    public int getMonto() {
        return Monto;
    }

    public void setMonto(int Monto) {
        this.Monto = Monto;
    }
}
