package ecodelivery.sucursal;

import ecodelivery.model.Transporte;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sucursal de EcoDelivery.
 *
 * AGREGACIÓN con Transporte:
 * - Una sucursal administra transportes, pero si cierra, los transportes
 *   no desaparecen: se reasignan. El transporte existe independientemente.
 */
public class Sucursal {

    private final String id;
    private String nombre;
    private String ciudad;
    private final List<Transporte> flota;

    public Sucursal(String id, String nombre, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.flota = new ArrayList<>();
    }

    // ─── Gestión de flota ─────────────────────────────────────────────────────

    public void agregarTransporte(Transporte transporte) {
        if (transporte == null) {
            throw new IllegalArgumentException("El transporte no puede ser nulo.");
        }
        flota.add(transporte);
        System.out.println("[Sucursal " + nombre + "] Transporte agregado: " +
                           transporte.getNumeroSerie());
    }

    /**
     * Remueve un transporte de la flota (para reasignación).
     * El transporte sigue existiendo, solo cambia de sucursal.
     *
     * @param numeroSerie número de serie del transporte a remover
     * @return el transporte removido, o null si no se encontró
     */
    public Transporte removerTransporte(String numeroSerie) {
        for (int i = 0; i < flota.size(); i++) {
            if (flota.get(i).getNumeroSerie().equals(numeroSerie)) {
                Transporte t = flota.remove(i);
                System.out.println("[Sucursal " + nombre + "] Transporte " +
                                   numeroSerie + " removido para reasignación.");
                return t;
            }
        }
        System.out.println("[Sucursal " + nombre + "] Advertencia: No se encontró transporte " +
                           numeroSerie);
        return null;
    }

    public Transporte buscarTransporte(String numeroSerie) {
        for (Transporte t : flota) {
            if (t.getNumeroSerie().equals(numeroSerie)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Cierra la sucursal y devuelve todos sus transportes para reasignación.
     */
    public List<Transporte> cerrarSucursal() {
        System.out.println("[Sucursal " + nombre + "] Cerrando sucursal. " +
                           flota.size() + " transportes disponibles para reasignación.");
        List<Transporte> transportesReasignables = new ArrayList<>(flota);
        flota.clear();
        return transportesReasignables;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCiudad() { return ciudad; }

    public List<Transporte> getFlota() {
        return Collections.unmodifiableList(flota);
    }

    public int getTotalTransportes() {
        return flota.size();
    }

    @Override
    public String toString() {
        return "Sucursal{id='" + id + "', nombre='" + nombre +
               "', ciudad='" + ciudad + "', transportes=" + flota.size() + "}";
    }
}
