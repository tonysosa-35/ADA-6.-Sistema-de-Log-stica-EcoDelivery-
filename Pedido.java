package ecodelivery.model;

/**
 * Representa un pedido leído del archivo pedidos.txt.
 * Formato esperado de cada línea:
 * ID_PEDIDO,NUMERO_SERIE_TRANSPORTE,PESO_KG,PARAMETRO_TARIFA
 *
 * Ejemplo:
 * PED001,DRONE-01,2.5,150.0
 */
public class Pedido {

    private final String idPedido;
    private final String numeroSerieTransporte;
    private final double pesoKg;
    private final double parametroTarifa; // km, altitud, etc. según el tipo

    public Pedido(String idPedido, String numeroSerieTransporte,
                  double pesoKg, double parametroTarifa) {
        this.idPedido = idPedido;
        this.numeroSerieTransporte = numeroSerieTransporte;
        this.pesoKg = pesoKg;
        this.parametroTarifa = parametroTarifa;
    }

    public String getIdPedido() { return idPedido; }
    public String getNumeroSerieTransporte() { return numeroSerieTransporte; }
    public double getPesoKg() { return pesoKg; }
    public double getParametroTarifa() { return parametroTarifa; }

    @Override
    public String toString() {
        return "Pedido{id='" + idPedido + "', transporte='" + numeroSerieTransporte +
               "', peso=" + pesoKg + " kg, parametro=" + parametroTarifa + "}";
    }
}
