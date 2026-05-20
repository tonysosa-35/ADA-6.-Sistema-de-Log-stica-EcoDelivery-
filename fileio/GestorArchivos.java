package ecodelivery.fileio;

import ecodelivery.model.Pedido;
import ecodelivery.model.ResultadoPedido;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lectura de pedidos.txt y la escritura de reporte_diario.txt.
 *
 * Manejo robusto de errores:
 * - Archivo no encontrado → registra el incidente y no se cuelga.
 * - Línea con formato incorrecto → se registra y se continúa con la siguiente.
 * - El sistema nunca lanza excepciones no controladas al nivel principal.
 */
public class GestorArchivos {

    private static final String ARCHIVO_PEDIDOS = "pedidos.txt";
    private static final String ARCHIVO_REPORTE = "reporte_diario.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Lee y parsea el archivo pedidos.txt.
     * Las líneas con errores de formato se descartan y se registran.
     *
     * @return lista de objetos Pedido válidos, y lista de errores de parseo
     */
    public ResultadoLectura leerPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        List<String> erroresParseo = new ArrayList<>();

        File archivo = new File(ARCHIVO_PEDIDOS);
        if (!archivo.exists()) {
            String error = "ARCHIVO NO ENCONTRADO: " + ARCHIVO_PEDIDOS +
                           " — No se procesarán pedidos.";
            System.err.println("[GestorArchivos] " + error);
            erroresParseo.add(error);
            return new ResultadoLectura(pedidos, erroresParseo);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numLinea = 0;

            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();

                // Ignorar líneas vacías y comentarios
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                try {
                    Pedido p = parsearLinea(linea);
                    pedidos.add(p);
                } catch (Exception e) {
                    String error = "Línea " + numLinea + " inválida [\"" + linea + "\"]: " + e.getMessage();
                    System.err.println("[GestorArchivos] " + error);
                    erroresParseo.add(error);
                }
            }

        } catch (IOException e) {
            String error = "Error al leer " + ARCHIVO_PEDIDOS + ": " + e.getMessage();
            System.err.println("[GestorArchivos] " + error);
            erroresParseo.add(error);
        }

        System.out.println("[GestorArchivos] Lectura completada: " +
                           pedidos.size() + " pedidos válidos, " +
                           erroresParseo.size() + " errores.");
        return new ResultadoLectura(pedidos, erroresParseo);
    }

    /**
     * Parsea una línea del archivo pedidos.txt.
     * Formato: ID_PEDIDO,NUMERO_SERIE,PESO_KG,PARAMETRO_TARIFA
     */
    private Pedido parsearLinea(String linea) {
        String[] partes = linea.split(",");
        if (partes.length != 4) {
            throw new IllegalArgumentException(
                "Se esperaban 4 campos separados por coma, se encontraron: " + partes.length);
        }

        String idPedido = partes[0].trim();
        String numeroSerie = partes[1].trim();

        if (idPedido.isEmpty() || numeroSerie.isEmpty()) {
            throw new IllegalArgumentException("ID de pedido o número de serie vacío.");
        }

        double pesoKg;
        double parametroTarifa;

        try {
            pesoKg = Double.parseDouble(partes[2].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Peso inválido: '" + partes[2].trim() + "'");
        }

        try {
            parametroTarifa = Double.parseDouble(partes[3].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parámetro de tarifa inválido: '" + partes[3].trim() + "'");
        }

        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0. Valor: " + pesoKg);
        }

        return new Pedido(idPedido, numeroSerie, pesoKg, parametroTarifa);
    }

    /**
     * Escribe el reporte diario en reporte_diario.txt.
     *
     * @param resultados lista de resultados de procesamiento de pedidos
     * @param erroresParseo errores encontrados al leer el archivo
     */
    public void escribirReporte(List<ResultadoPedido> resultados, List<String> erroresParseo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_REPORTE))) {

            bw.write("==========================================");
            bw.newLine();
            bw.write("   REPORTE DIARIO - ECODELIVERY");
            bw.newLine();
            bw.write("   Generado: " + LocalDateTime.now().format(FORMATTER));
            bw.newLine();
            bw.write("==========================================");
            bw.newLine();
            bw.newLine();

            // Sección de errores de parseo
            if (!erroresParseo.isEmpty()) {
                bw.write("--- ERRORES DE LECTURA DEL ARCHIVO ---");
                bw.newLine();
                for (String err : erroresParseo) {
                    bw.write("  ! " + err);
                    bw.newLine();
                }
                bw.newLine();
            }

            // Sección de resultados
            bw.write("--- RESULTADO DE PEDIDOS ---");
            bw.newLine();

            long exitosos = resultados.stream()
                    .filter(r -> r.getEstado() == ResultadoPedido.Estado.EXITOSO)
                    .count();
            long errores = resultados.size() - exitosos;
            double ingresoTotal = resultados.stream()
                    .mapToDouble(ResultadoPedido::getTarifaCalculada)
                    .sum();

            for (ResultadoPedido resultado : resultados) {
                bw.write("  " + resultado.toString());
                bw.newLine();
            }

            bw.newLine();
            bw.write("--- RESUMEN DEL DÍA ---");
            bw.newLine();
            bw.write("  Total pedidos procesados : " + resultados.size());
            bw.newLine();
            bw.write("  Pedidos exitosos         : " + exitosos);
            bw.newLine();
            bw.write("  Pedidos con error        : " + errores);
            bw.newLine();
            bw.write(String.format("  Ingreso total del día    : $%.2f", ingresoTotal));
            bw.newLine();
            bw.newLine();
            bw.write("==========================================");
            bw.newLine();

            System.out.println("[GestorArchivos] Reporte escrito en: " + ARCHIVO_REPORTE);

        } catch (IOException e) {
            System.err.println("[GestorArchivos] ERROR al escribir reporte: " + e.getMessage());
        }
    }

    // ─── Clase interna para encapsular resultado de lectura ──────────────────

    public static class ResultadoLectura {
        private final List<Pedido> pedidos;
        private final List<String> errores;

        public ResultadoLectura(List<Pedido> pedidos, List<String> errores) {
            this.pedidos = pedidos;
            this.errores = errores;
        }

        public List<Pedido> getPedidos() { return pedidos; }
        public List<String> getErrores() { return errores; }
    }
}
