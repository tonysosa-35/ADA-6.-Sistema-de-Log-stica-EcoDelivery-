package ecodelivery.model;

import ecodelivery.interfaces.Recargable;

/**
 * Drone de reparto.
 * Implementa Recargable porque necesita conectarse a la red eléctrica.
 * Tarifa = (pesoKg * altitudMetros) / factorAerodinamico
 */
public class Drone extends Transporte implements Recargable {

    private final double factorAerodinamico;
    private double nivelBateria; // 0.0 a 100.0

    public Drone(String numeroSerie, double capacidadMaximaKg, double factorAerodinamico) {
        super(numeroSerie, capacidadMaximaKg);
        this.factorAerodinamico = factorAerodinamico;
        this.nivelBateria = 100.0;
    }

    /**
     * Fórmula compleja basada en peso y altitud.
     * Se pasa la altitud como parámetro; el peso se toma de la carga actual.
     *
     * @param altitudMetros altitud de vuelo requerida en metros
     * @return tarifa = (cargaActual * altitud) / factorAerodinamico
     */
    @Override
    public double calcularTarifaBase(double altitudMetros) {
        double pesoEfectivo = getCargaActual() > 0 ? getCargaActual() : 1.0;
        return (pesoEfectivo * altitudMetros) / factorAerodinamico;
    }

    @Override
    public void iniciarCargaRapida() {
        System.out.println("[Drone " + getNumeroSerie() +
                           "] Conectando a estación de carga... Carga rápida iniciada.");
        nivelBateria = 100.0;
        getDiario().registrarEvento("Ciclo de carga rápida completado. Batería al 100%.");
        System.out.println("[Drone " + getNumeroSerie() +
                           "] Carga completa. Batería: " + nivelBateria + "%");
    }

    @Override
    public String getTipoTransporte() {
        return "Drone";
    }

    public double getFactorAerodinamico() { return factorAerodinamico; }
    public double getNivelBateria() { return nivelBateria; }

    public void setNivelBateria(double nivelBateria) {
        this.nivelBateria = Math.max(0.0, Math.min(100.0, nivelBateria));
    }
}
