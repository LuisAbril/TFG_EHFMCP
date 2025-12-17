package tfg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa una instancia del problema VRP.
 * Lee un archivo CSV con información de vehículos y nodos, y lo procesa.
 */
public class Instance {
    private String fileName;
    private List<Map<String, String>> vehicles;
    private List<Map<String, String>> nodes;
    private String content;

    /**
     * Constructor que lee un archivo CSV, lo procesa y guarda la información.
     * 
     * @param filePath Ruta del archivo a leer
     * @throws IOException Si hay un error al leer el archivo
     */
    public Instance(String filePath) throws IOException {
        this.fileName = filePath;
        this.vehicles = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.content = "";
        readFile(filePath);
    }

    /**
     * Método privado que lee el archivo CSV y procesa su contenido.
     * Separa la información en dos secciones: vehículos y nodos.
     * 
     * @param filePath Ruta del archivo a leer
     * @throws IOException Si hay un error al leer el archivo
     */
    private void readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                    contentBuilder.append(line).append("\n");
                }
            }
        }
        
        this.content = contentBuilder.toString();
        parseCSV(lines);
    }

    /**
     * Parsea el CSV separando vehículos y nodos.
     * 
     * @param lines Lista de líneas del archivo
     */
    private void parseCSV(List<String> lines) {
        String[] vehicleHeaders = null;
        String[] nodeHeaders = null;
        boolean parsingVehicles = false;
        boolean parsingNodes = false;

        for (String line : lines) {
            String[] parts = line.split(",");
            
            // Saltar líneas vacías
            if (parts.length == 0 || parts[0].trim().isEmpty()) {
                continue;
            }

            // Detectar sección de vehículos
            if (parts[0].trim().equals("Vehicle")) {
                vehicleHeaders = parts;
                parsingVehicles = true;
                parsingNodes = false;
                continue;
            }

            // Detectar sección de nodos
            if (parts[0].trim().equals("Node")) {
                nodeHeaders = parts;
                parsingVehicles = false;
                parsingNodes = true;
                continue;
            }

            // Procesar vehículos
            if (parsingVehicles && vehicleHeaders != null && !parts[0].trim().isEmpty()) {
                Map<String, String> vehicle = new HashMap<>();
                for (int i = 0; i < vehicleHeaders.length && i < parts.length; i++) {
                    vehicle.put(vehicleHeaders[i].trim(), parts[i].trim());
                }
                vehicles.add(vehicle);
            }

            // Procesar nodos
            if (parsingNodes && nodeHeaders != null && !parts[0].trim().isEmpty()) {
                Map<String, String> node = new HashMap<>();
                for (int i = 0; i < nodeHeaders.length && i < parts.length; i++) {
                    node.put(nodeHeaders[i].trim(), parts[i].trim());
                }
                nodes.add(node);
            }
        }
    }

    /**
     * Obtiene el nombre del archivo.
     * 
     * @return Nombre del archivo
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Obtiene la lista de vehículos parseados.
     * 
     * @return Lista de mapas con información de vehículos
     */
    public List<Map<String, String>> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    /**
     * Obtiene la lista de nodos parseados.
     * 
     * @return Lista de mapas con información de nodos
     */
    public List<Map<String, String>> getNodes() {
        return new ArrayList<>(nodes);
    }

    /**
     * Obtiene el número de vehículos.
     * 
     * @return Número de vehículos
     */
    public int getNumberOfVehicles() {
        return vehicles.size();
    }

    /**
     * Obtiene el número de nodos.
     * 
     * @return Número de nodos
     */
    public int getNumberOfNodes() {
        return nodes.size();
    }

    /**
     * Obtiene el contenido completo del archivo.
     * 
     * @return Contenido del archivo
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "fileName='" + fileName + '\'' +
                ", vehículos=" + vehicles.size() +
                ", nodos=" + nodes.size() +
                '}';
    }
}
