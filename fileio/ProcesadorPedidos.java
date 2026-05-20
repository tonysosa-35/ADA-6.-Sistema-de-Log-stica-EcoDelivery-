package ecodelivery.fileio;

import ecodelivery.model.Pedido;
import ecodelivery.model.ResultadoPedido;
import ecodelivery.model.Transporte;
import ecodelivery.sucursal.Sucursal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Procesa los pedidos leídos del archivo, buscando los transportes
 * en todas las sucursales registradas.
 *
 * Maneja errores por pedido de forma ordenada (sobrepeso, transporte no encontrado, etc.)
 * sin detener el procesamiento de los pedidos restantes.
 */
public class ProcesadorPedidos {

    private final Map<String, Sucursal> sucursales;

    public ProcesadorPedidos(Map<String, Sucursal> sucursales) {
        this.sucursales = sucursales;
    }

    /**
     * Procesa una lista de pedidos y devuelve los resultados.
     *
     * @param pedidos lista de pedidos a procesar
     * @return lista de ResultadoPedido (éxito o error por cada pedido)
     */
    public List<ResultadoPedido> procesar(List<Pedido> pedidos) {
        List<ResultadoPedido> resultados = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            ResultadoPedido resultado = procesarPedido(pedido);
            resultados.add(resultado);

            // Imprimir en consola en tiempo real
            System.out.println("  " + resultado);
        }

        return resultados;
    }

    private ResultadoPedido procesarPedido(Pedido pedido) {
        // Buscar el transporte en todas las sucursales
        Transporte transporte = buscarTransporteEnFlota(pedido.getNumeroSerieTransporte());

        if (transporte == null) {
            return new ResultadoPedido(
                pedido.getIdPedido(),
                "Transporte no encontrado: " + pedido.getNumeroSerieTransporte()
            );
        }

        if (!transporte.isDisponible()) {
            return new ResultadoPedido(
                pedido.getIdPedido(),
                "Transporte " + pedido.getNumeroSerieTransporte() + " no está disponible."
            );
        }

        // Intentar cargar el paquete (puede lanzar excepción de sobrepeso)
        try {
            transporte.cargarPaquete(pedido.getPesoKg());
        } catch (IllegalStateException e) {
            return new ResultadoPedido(
                pedido.getIdPedido(),
                "SOBREPESO — " + e.getMessage()
            );
        } catch (IllegalArgumentException e) {
            return new ResultadoPedido(
                pedido.getIdPedido(),
                "Peso inválido — " + e.getMessage()
            );
        }

        // Calcular tarifa
        double tarifa;
        try {
            tarifa = transporte.calcularTarifaBase(pedido.getParametroTarifa());
        } catch (Exception e) {
            // En caso de error en cálculo, deshacer la carga
            transporte.descargar();
            return new ResultadoPedido(
                pedido.getIdPedido(),
                "Error al calcular tarifa: " + e.getMessage()
            );
        }

        String mensaje = "Transporte: " + transporte.getNumeroSerie() +
                         " [" + transporte.getTipoTransporte() + "]" +
                         " | Peso: " + pedido.getPesoKg() + " kg";

        return new ResultadoPedido(pedido.getIdPedido(), tarifa, mensaje);
    }

    /**
     * Busca un transporte por número de serie en todas las sucursales registradas.
     */
    private Transporte buscarTransporteEnFlota(String numeroSerie) {
        for (Sucursal sucursal : sucursales.values()) {
            Transporte t = sucursal.buscarTransporte(numeroSerie);
            if (t != null) return t;
        }
        return null;
    }
}
