package modelo;

import java.sql.Date;

public class Usuario {
    private int id;
    private String nombre;
    private long telefono;
    private Date fechaNac;
    private int adeudos;

    public Usuario(String nombre, long telefono, Date fechaNac) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNac = fechaNac;
    }

    public Usuario(int id, String nombre, long telefono, Date fechaNac) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNac = fechaNac;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public Date getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(Date fechaNac) {
        this.fechaNac = fechaNac;
    }

    public int getAdeudos() {
        return adeudos;
    }

    public void setAdeudos(int adeudos) {
        this.adeudos = adeudos;
    }


}
