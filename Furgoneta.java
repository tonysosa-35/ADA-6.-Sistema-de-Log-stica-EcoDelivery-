package ecodelivery.model;

/**
 * Furgoneta a combustión.
 * Tarifa = tarifaBase + (consumoCombustiblePorKm * km * precioCombustible)
 */
public class Furgoneta extends Transporte {

    private final double tarifaBaseFija;
    private final double consumoCombustiblePorKm; // litros/km
    private final double precioCombustible;        // precio por litro

    public Furgoneta(String numeroSerie, double capacidadMaximaKg,
                     double tarifaBaseFija, double consumoCombustiblePorKm,
                     double precioCombustible) {
        super(numeroSerie, capacidadMaximaKg);
        this.tarifaBaseFija = tarifaBaseFija;
        this.consumoCombustiblePorKm = consumoCombustiblePorKm;
        this.precioCombustible = precioCombustible;
    }

    /**
     * @param km kilómetros del recorrido
     * @return tarifa = base + consumo_por_km * km * precio_combustible
     */
    @Override
    public double calcularTarifaBase(double km) {
        return tarifaBaseFija + (consumoCombustiblePorKm * km * precioCombustible);
    }

    @Override
    public String getTipoTransporte() {
        return "Furgoneta";
    }

    public double getTarifaBaseFija() { return tarifaBaseFija; }
    public double getConsumoCombustiblePorKm() { return consumoCombustiblePorKm; }
    public double getPrecioCombustible() { return precioCombustible; }
}
