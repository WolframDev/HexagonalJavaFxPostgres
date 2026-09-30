package co.edu.poli.examen2_Soto.dominio.modelo;

/**
 * Entidad de dominio abstracta — Tarjeta bancaria.
 * Contiene la lógica de negocio de activación y bloqueo.
 * No depende de ninguna capa externa.
 */
public abstract class Tarjeta {

    private String numero;
    private String fechaExp;
    private boolean estado;
    private Titular titular;

    protected Tarjeta(String numero, String fechaExp, boolean estado, Titular titular) {
        this.numero = numero;
        this.fechaExp = fechaExp;
        this.estado = estado;
        this.titular = titular;
    }

    // ── Lógica de negocio del dominio ──────────────────────────────────────

    public String bloquear() {
        this.estado = false;
        return "Tarjeta " + numero + " BLOQUEADA.";
    }

    public String activar() {
        this.estado = true;
        return "Tarjeta " + numero + " ACTIVADA.";
    }

    // ── Getters / Setters ──────────────────────────────────────────────────

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFechaExp() {
        return fechaExp;
    }

    public void setFechaExp(String fechaExp) {
        this.fechaExp = fechaExp;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Titular getTitular() {
        return titular;
    }

    public void setTitular(Titular titular) {
        this.titular = titular;
    }

    @Override
    public String toString() {
        return "numero=" + numero
                + ", fechaExp=" + fechaExp
                + ", estado=" + estado
                + ", titular=" + titular;
    }
}
