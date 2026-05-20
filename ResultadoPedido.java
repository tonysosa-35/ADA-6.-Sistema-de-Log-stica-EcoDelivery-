package ecodelivery.model;

/**
 * Resultado del procesamiento de un pedido.
 * Puede ser exitoso o contener un error registrado.
 */
public class ResultadoPedido {

    public enum Estado { EXITOSO, ERROR }

    private final String idPedido;
    private final Estado estado;
    private final String mensaje;
    private final double tarifaCalculada;

    // Constructor para pedidos exitosos
    public ResultadoPedido(String idPedido, double tarifaCalculada, String mensaje) {
        this.idPedido = idPedido;
        this.estado = Estado.EXITOSO;
        this.tarifaCalculada = tarifaCalculada;
        this.mensaje = mensaje;
    }

    // Constructor para pedidos con error
    public ResultadoPedido(String idPedido, String mensajeError) {
        this.idPedido = idPedido;
        this.estado = Estado.ERROR;
        this.tarifaCalculada = 0.0;
        this.mensaje = mensajeError;
    }

    public String getIdPedido() { return idPedido; }
    public Estado getEstado() { return estado; }
    public String getMensaje() { return mensaje; }
    public double getTarifaCalculada() { return tarifaCalculada; }

    @Override
    public String toString() {
        if (estado == Estado.EXITOSO) {
            return "[OK]    Pedido " + idPedido + " | Tarifa: $" +
                   String.format("%.2f", tarifaCalculada) + " | " + mensaje;
        } else {
            return "[ERROR] Pedido " + idPedido + " | " + mensaje;
        }
    }
}
