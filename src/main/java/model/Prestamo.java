package model;

/**
 * Entidad Prestamo: registra qué libro fue prestado, a nombre de quién
 * y en qué fecha. Incluye el título del libro y el nombre del usuario
 * (obtenidos con JOIN) para simplificar la vista.
 */
public class Prestamo {

    private int id;
    private String tituloLibro;
    private String nombreUsuario;
    private String fechaPrestamo;
    private boolean devuelto;

    public Prestamo(int id, String tituloLibro, String nombreUsuario,
                    String fechaPrestamo, boolean devuelto) {
        this.id = id;
        this.tituloLibro = tituloLibro;
        this.nombreUsuario = nombreUsuario;
        this.fechaPrestamo = fechaPrestamo;
        this.devuelto = devuelto;
    }

    public int getId() { return id; }
    public String getTituloLibro() { return tituloLibro; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getFechaPrestamo() { return fechaPrestamo; }
    public boolean isDevuelto() { return devuelto; }
}
