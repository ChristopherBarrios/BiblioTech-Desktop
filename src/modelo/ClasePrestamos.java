package modelo;

public class ClasePrestamos {
    private int idPide;               // id_pide de la tabla pide
    private String usuario;           // nombre del usuario (tabla usuario)
    private String libro;             // título del libro (tabla libro)
    private String fechaPrestamo;     // texto en formato yy/MM/dd
    private String fechaDevolucion;   // texto en formato yy/MM/dd

    public ClasePrestamos() {
    }

    public ClasePrestamos(int idPide, String usuario, String libro,
                          String fechaPrestamo, String fechaDevolucion) {
        this.idPide = idPide;
        this.usuario = usuario;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getIdPide() {
        return idPide;
    }

    public void setIdPide(int idPide) {
        this.idPide = idPide;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getLibro() {
        return libro;
    }

    public void setLibro(String libro) {
        this.libro = libro;
    }

    public String getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(String fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public String getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(String fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
}