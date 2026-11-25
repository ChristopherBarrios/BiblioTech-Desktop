package modelo;

public class ClaseLibro {

    private int idLibro;
    private String isbn;
    private String titulo;
    private String editorial;
    private String categoria;

    public ClaseLibro() {
    }

    public ClaseLibro(int idLibro, String isbn, String titulo,
                      String editorial, String categoria) {
        this.idLibro = idLibro;
        this.isbn = isbn;
        this.titulo = titulo;
        this.editorial = editorial;
        this.categoria = categoria;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}