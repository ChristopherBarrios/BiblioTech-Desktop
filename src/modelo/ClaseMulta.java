package modelo;

/**
 * Una multa (tabla multa) con los datos de su préstamo (pide),
 * su usuario y su libro, para mostrarla en la tabla.
 * Las fechas se guardan como texto yyyy-MM-dd.
 */
public class ClaseMulta {

    // multa
    private int Id_multa;
    private int Id_pide;
    private int Monto;

    // usuario
    private String Nombre;
    private String Tel_cel;

    // libro
    private String Titulo;

    // pide (préstamo)
    private String Fecha_prestamo;
    private String Fecha_devolucion;

    public ClaseMulta() {
    }

    public int getId_multa() {
        return Id_multa;
    }

    public void setId_multa(int Id_multa) {
        this.Id_multa = Id_multa;
    }

    public int getId_pide() {
        return Id_pide;
    }

    public void setId_pide(int Id_pide) {
        this.Id_pide = Id_pide;
    }

    public int getMonto() {
        return Monto;
    }

    public void setMonto(int Monto) {
        this.Monto = Monto;
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
}
