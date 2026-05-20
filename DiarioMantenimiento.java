package ecodelivery.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diario de Mantenimiento interno e inseparable de cada transporte.
 * Relación de COMPOSICIÓN: si el transporte es destruido, el diario también.
 */
public class DiarioMantenimiento {

    private final String numeroSerieTransporte;
    private final List<String> registros;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DiarioMantenimiento(String numeroSerieTransporte) {
        this.numeroSerieTransporte = numeroSerieTransporte;
        this.registros = new ArrayList<>();
        registrarEvento("Diario de mantenimiento creado para transporte: " + numeroSerieTransporte);
    }

    public void registrarEvento(String descripcion) {
        String entrada = "[" + LocalDateTime.now().format(FORMATTER) + "] " + descripcion;
        registros.add(entrada);
    }

    public List<String> getRegistros() {
        return new ArrayList<>(registros);
    }

    public String getNumeroSerieTransporte() {
        return numeroSerieTransporte;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DIARIO DE MANTENIMIENTO ===\n");
        sb.append("Transporte: ").append(numeroSerieTransporte).append("\n");
        for (String r : registros) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }
}
