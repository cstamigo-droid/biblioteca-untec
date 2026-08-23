package model;

/**
 * Entidad Libro: representa un libro del catálogo de la biblioteca.
 * Sus atributos corresponden a las columnas de la tabla LIBROS.
 */
public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private boolean disponible;

    public Libro(int id, String titulo, String autor, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.disponible = disponible;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public boolean isDisponible() { return disponible; }
}
