package ecodelivery.model;

import ecodelivery.interfaces.Recargable;

/**
 * Bicicleta Eléctrica.
 * Implementa Recargable porque necesita conectarse a la red eléctrica.
 * Tarifa = distanciaKm * factorDesgasteLlantas
 */
public class BicicletaElectrica extends Transporte implements Recargable {

    private final double factorDesgasteLlantas;
    private double nivelBateria; // 0.0 a 100.0

    public BicicletaElectrica(String numeroSerie, double capacidadMaximaKg,
                               double factorDesgasteLlantas) {
        super(numeroSerie, capacidadMaximaKg);
        this.factorDesgasteLlantas = factorDesgasteLlantas;
        this.nivelBateria = 100.0;
    }

    /**
     * @param distanciaKm kilómetros del recorrido
     * @return tarifa = distancia * factorDesgaste
     */
    @Override
    public double calcularTarifaBase(double distanciaKm) {
        return distanciaKm * factorDesgasteLlantas;
    }

    @Override
    public void iniciarCargaRapida() {
        System.out.println("[BicicletaElectrica " + getNumeroSerie() +
                           "] Conectando a la red eléctrica... Carga rápida iniciada.");
        nivelBateria = 100.0;
        getDiario().registrarEvento("Ciclo de carga rápida completado. Batería al 100%.");
        System.out.println("[BicicletaElectrica " + getNumeroSerie() +
                           "] Carga completa. Batería: " + nivelBateria + "%");
    }

    @Override
    public String getTipoTransporte() {
        return "BicicletaElectrica";
    }

    public double getFactorDesgasteLlantas() { return factorDesgasteLlantas; }
    public double getNivelBateria() { return nivelBateria; }

    public void setNivelBateria(double nivelBateria) {
        this.nivelBateria = Math.max(0.0, Math.min(100.0, nivelBateria));
    }
}
