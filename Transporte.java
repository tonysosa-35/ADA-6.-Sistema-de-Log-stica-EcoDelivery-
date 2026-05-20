package ecodelivery.model;

/**
 * Clase abstracta base para todos los tipos de transporte de EcoDelivery.
 *
 * ENCAPSULAMIENTO:
 * - numeroSerie: private final → no puede ser alterado por nadie externo.
 * - cargaActual: private → modificada solo a través de cargarPaquete() / descargar().
 *
 * ABSTRACCIÓN:
 * - calcularTarifaBase() es abstracto → cada subclase define su propia fórmula.
 *
 * COMPOSICIÓN con DiarioMantenimiento:
 * - El diario nace y muere con el transporte.
 */
public abstract class Transporte {

    private final String numeroSerie;
    private final double capacidadMaximaKg;
    private double cargaActual;
    private boolean disponible;

    // Composición: el diario es parte inseparable del transporte
    private final DiarioMantenimiento diario;

    public Transporte(String numeroSerie, double capacidadMaximaKg) {
        this.numeroSerie = numeroSerie;
        this.capacidadMaximaKg = capacidadMaximaKg;
        this.cargaActual = 0.0;
        this.disponible = true;
        this.diario = new DiarioMantenimiento(numeroSerie);
    }

    // ─── Método de negocio principal ──────────────────────────────────────────

    /**
     * Intenta cargar un paquete. Lanza excepción si excede el límite de peso.
     *
     * @param pesoKg peso del paquete en kilogramos
     * @throws IllegalArgumentException si la carga supera la capacidad máxima
     */
    public void cargarPaquete(double pesoKg) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException(
                "El peso del paquete debe ser positivo. Recibido: " + pesoKg);
        }
        if (cargaActual + pesoKg > capacidadMaximaKg) {
            throw new IllegalStateException(
                "Sobrepeso: El transporte " + numeroSerie +
                " tiene capacidad de " + capacidadMaximaKg + " kg." +
                " Carga actual: " + cargaActual + " kg." +
                " Intentando agregar: " + pesoKg + " kg.");
        }
        cargaActual += pesoKg;
        diario.registrarEvento("Paquete cargado. Peso: " + pesoKg +
                               " kg. Carga total: " + cargaActual + " kg.");
    }

    /**
     * Descarga el transporte completamente.
     */
    public void descargar() {
        double cargaAnterior = cargaActual;
        cargaActual = 0.0;
        diario.registrarEvento("Transporte descargado. Carga liberada: " + cargaAnterior + " kg.");
    }

    /**
     * Calcula la tarifa base de envío. Cada tipo de transporte define su propia fórmula.
     *
     * @param parametro parámetro principal de cálculo (km, altitud, etc.)
     * @return tarifa base en pesos/moneda
     */
    public abstract double calcularTarifaBase(double parametro);

    /**
     * Descripción del tipo de transporte para reportes.
     */
    public abstract String getTipoTransporte();

    // ─── Getters (acceso controlado, sin setters para campos críticos) ────────

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public double getCapacidadMaximaKg() {
        return capacidadMaximaKg;
    }

    public double getCargaActual() {
        return cargaActual;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        disponible = disponible;
        diario.registrarEvento("Estado de disponibilidad cambiado a: " + disponible);
        this.disponible = disponible;
    }

    public DiarioMantenimiento getDiario() {
        return diario;
    }

    @Override
    public String toString() {
        return "[" + getTipoTransporte() + "] Serie: " + numeroSerie +
               " | Cap: " + capacidadMaximaKg + " kg" +
               " | Carga: " + cargaActual + " kg" +
               " | Disponible: " + disponible;
    }
}
