package ecodelivery;

import ecodelivery.fileio.GestorArchivos;
import ecodelivery.fileio.GestorArchivos.ResultadoLectura;
import ecodelivery.fileio.ProcesadorPedidos;
import ecodelivery.interfaces.Recargable;
import ecodelivery.model.*;
import ecodelivery.sucursal.Sucursal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Punto de entrada principal del sistema EcoDelivery.
 *
 * Conceptos OOP aplicados:
 * - HERENCIA: Drone, BicicletaElectrica, Furgoneta extienden Transporte
 * - POLIMORFISMO: calcularTarifaBase() varía por tipo de transporte
 * - ENCAPSULAMIENTO: numeroSerie y cargaActual son privados y protegidos
 * - ABSTRACCIÓN: Transporte es clase abstracta
 * - INTERFAZ: Recargable implementada por Drone y BicicletaElectrica
 * - COMPOSICIÓN: DiarioMantenimiento vive y muere con Transporte
 * - AGREGACIÓN: Sucursal agrupa Transportes que sobreviven a su cierre
 */
public class EcoDeliveryApp {

    public static void main(String[] args) {

        System.out.println("=== SISTEMA ECODELIVERY - INICIANDO ===\n");

        // ─── 1. Crear transportes ──────────────────────────────────────────────
        Drone drone1        = new Drone("DRONE-01", 5.0, 8.0);
        Drone drone2        = new Drone("DRONE-02", 3.0, 6.0);
        BicicletaElectrica  bici1 = new BicicletaElectrica("BICI-01", 20.0, 2.5);
        BicicletaElectrica  bici2 = new BicicletaElectrica("BICI-02", 25.0, 3.0);
        Furgoneta furgo1    = new Furgoneta("FURG-01", 500.0, 50.0, 0.08, 22.0);
        Furgoneta furgo2    = new Furgoneta("FURG-02", 800.0, 60.0, 0.10, 22.0);

        // ─── 2. Crear sucursales y asignar flota ──────────────────────────────
        Sucursal sucNorte  = new Sucursal("SUC-001", "Norte", "Mérida");
        Sucursal sucSur    = new Sucursal("SUC-002", "Sur",   "Mérida");

        sucNorte.agregarTransporte(drone1);
        sucNorte.agregarTransporte(drone2);
        sucNorte.agregarTransporte(bici1);
        sucNorte.agregarTransporte(furgo1);

        sucSur.agregarTransporte(bici2);
        sucSur.agregarTransporte(furgo2);

        System.out.println("\n--- Flota registrada ---");
        System.out.println(sucNorte);
        System.out.println(sucSur);

        // ─── 3. Demo polimorfismo: carga rápida en equipos eléctricos ─────────
        System.out.println("\n--- Demo: Carga rápida (interfaz Recargable) ---");
        Transporte[] todosLosTransportes = {drone1, drone2, bici1, bici2, furgo1, furgo2};
        for (Transporte t : todosLosTransportes) {
            if (t instanceof Recargable) {
                ((Recargable) t).iniciarCargaRapida();
            }
        }

        // ─── 4. Demo reasignación de transporte al cerrar sucursal ────────────
        System.out.println("\n--- Demo: Cierre y reasignación de sucursal ---");
        List<Transporte> reasignados = sucNorte.cerrarSucursal();
        for (Transporte t : reasignados) {
            sucSur.agregarTransporte(t);
        }
        System.out.println("Sucursal Sur después de reasignación: " + sucSur);

        // ─── 5. Procesar pedidos del día ──────────────────────────────────────
        System.out.println("\n--- Procesando pedidos del día ---");

        // Registrar todas las sucursales activas
        Map<String, Sucursal> sucursalesActivas = new LinkedHashMap<>();
        sucursalesActivas.put(sucSur.getId(), sucSur);

        GestorArchivos gestorArchivos   = new GestorArchivos();
        ProcesadorPedidos procesador    = new ProcesadorPedidos(sucursalesActivas);

        // Leer pedidos del archivo (maneja errores internamente)
        ResultadoLectura lectura = gestorArchivos.leerPedidos();

        // Procesar cada pedido (maneja errores por pedido, no se cuelga)
        List<ecodelivery.model.ResultadoPedido> resultados =
                procesador.procesar(lectura.getPedidos());

        // ─── 6. Generar reporte del día ───────────────────────────────────────
        System.out.println("\n--- Generando reporte diario ---");
        gestorArchivos.escribirReporte(resultados, lectura.getErrores());

        // ─── 7. Mostrar diario de un transporte ───────────────────────────────
        System.out.println("\n--- Diario de mantenimiento: DRONE-01 ---");
        System.out.println(drone1.getDiario());

        System.out.println("\n=== SISTEMA ECODELIVERY - CIERRE DEL DÍA ===");
    }
}
