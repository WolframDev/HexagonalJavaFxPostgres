package co.edu.poli.examen2_Soto.dominio.modelo;

/**
 * Entidad de dominio — Titular de una tarjeta.
 * No depende de ninguna capa externa.
 */
public class Titular {

    private String id;
    private String nombre;

    public Titular(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return id + ", " + nombre;
    }
}
