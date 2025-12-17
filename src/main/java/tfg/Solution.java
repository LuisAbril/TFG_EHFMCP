package tfg;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa una solución al problema VRP.
 * Contiene rutas asignadas a vehículos y métricas de desempeño.
 */
public class Solution {
    private static Instance instance;
    private Map<String, List<String>> vehicleRoutes; // Vehículo -> Lista de nodos
    private double totalDistance;
    private double totalCO2;

    /**
     * Constructor por defecto.
     */
    public Solution() {
        this.vehicleRoutes = new HashMap<>();
        this.totalDistance = 0.0;
        this.totalCO2 = 0.0;
    }

    /**
     * Constructor con datos de solución y métricas.
     * 
     * @param vehicleRoutes Mapa de vehículos con sus rutas
     * @param totalDistance Distancia total
     * @param totalCO2 Emisiones totales de CO2
     */
    public Solution(Map<String, List<String>> vehicleRoutes, double totalDistance, double totalCO2) {
        this.vehicleRoutes = new HashMap<>(vehicleRoutes);
        this.totalDistance = totalDistance;
        this.totalCO2 = totalCO2;
    }

    /**
     * Establece la instancia estática.
     * 
     * @param inst Instancia a establecer
     */
    public static void setInstance(Instance inst) {
        instance = inst;
    }

    /**
     * Obtiene la instancia estática.
     * 
     * @return Instancia
     */
    public static Instance getInstance() {
        return instance;
    }

    /**
     * Añade una ruta a un vehículo.
     * 
     * @param vehicle Nombre del vehículo
     * @param route Lista de nodos en la ruta
     */
    public void addRoute(String vehicle, List<String> route) {
        vehicleRoutes.put(vehicle, new ArrayList<>(route));
    }

    /**
     * Obtiene las rutas de vehículos.
     * 
     * @return Mapa de vehículos con sus rutas
     */
    public Map<String, List<String>> getVehicleRoutes() {
        return new HashMap<>(vehicleRoutes);
    }

    /**
     * Establece la distancia total.
     * 
     * @param totalDistance Distancia total
     */
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    /**
     * Obtiene la distancia total.
     * 
     * @return Distancia total
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Establece el CO2 total.
     * 
     * @param totalCO2 Emisiones de CO2 total
     */
    public void setTotalCO2(double totalCO2) {
        this.totalCO2 = totalCO2;
    }

    /**
     * Obtiene el CO2 total.
     * 
     * @return Emisiones de CO2 total
     */
    public double getTotalCO2() {
        return totalCO2;
    }

    /**
     * Evalúa la solución calculando distancia y CO2 basándose en la instancia.
     * Calcula la distancia euclidiana entre nodos y las emisiones según los vehículos utilizados.
     */
    public void evaluate() {
        if (instance == null) {
            throw new IllegalStateException("La instancia no ha sido establecida");
        }

        double totalDist = 0.0;
        double totalEmissions = 0.0;

        List<Map<String, String>> nodes = instance.getNodes();
        List<Map<String, String>> vehicles = instance.getVehicles();
        
        // Crear mapa de coordenadas para acceso rápido
        Map<String, double[]> coordinates = new HashMap<>();
        for (Map<String, String> node : nodes) {
            String nodeName = node.get("Node");
            double x = Double.parseDouble(node.get("coord_x"));
            double y = Double.parseDouble(node.get("coord_y"));
            coordinates.put(nodeName, new double[]{x, y});
        }

        // Para cada vehículo y su ruta, calcular distancia y emisiones
        for (Map.Entry<String, List<String>> routeEntry : vehicleRoutes.entrySet()) {
            String vehicleName = routeEntry.getKey();
            List<String> route = routeEntry.getValue();

            // Encontrar el vehículo en la instancia
            Map<String, String> vehicleInfo = null;
            for (Map<String, String> v : vehicles) {
                if (v.get("Vehicle").equals(vehicleName)) {
                    vehicleInfo = v;
                    break;
                }
            }

            if (vehicleInfo != null) {
                double Ef = Double.parseDouble(vehicleInfo.get("Ef"));
                double Eo = Double.parseDouble(vehicleInfo.get("Eo"));
                double capacity = Double.parseDouble(vehicleInfo.get("Load"));

                // Calcular distancia de la ruta (desde P a primer nodo, entre nodos, y volver a P)
                if (!route.isEmpty()) {
                    double load = 0.0;  // Carga actual del vehículo
                    String prev = "P";  // Nodo anterior (empieza en planta)
                    
                    double[] depotCoords = coordinates.get("P");
                    
                    // Recorrer todas las granjas de la ruta
                    for (String node : route) {
                        // Calcular distancia desde nodo anterior al actual
                        double[] prevCoords = prev.equals("P") ? depotCoords : coordinates.get(prev);
                        double[] currentCoords = coordinates.get(node);
                        double distance = calculateDistance(prevCoords, currentCoords);
                        
                        totalDist += distance;
                        
                        // Calcular emisiones del tramo con la carga ACTUAL (antes de recoger)
                        // Fórmula: (Eo + ((Ef - Eo) / capacity) * load) * distance
                        totalEmissions += ((((Ef - Eo) / capacity) * load) + Eo ) * distance;
                        
                        // DESPUÉS de viajar, recoger la producción del nodo
                        load += getNodeProd(node, nodes);
                        prev = node;
                    }
                    
                    // Volver a P con la carga completa
                    double[] lastCoords = coordinates.get(prev);
                    double distanceToDepot = calculateDistance(lastCoords, depotCoords);
                    totalDist += distanceToDepot;
                    
                    // Emisiones del tramo de vuelta con toda la carga recogida
                    totalEmissions += ((((Ef - Eo) / capacity) * load) + Eo ) * distanceToDepot;
                }
            }
        }

        this.totalDistance = totalDist;
        this.totalCO2 = totalEmissions;
    }

    /**
     * Calcula la distancia euclidiana entre dos puntos.
     * 
     * @param point1 Array con coordenadas [x, y]
     * @param point2 Array con coordenadas [x, y]
     * @return Distancia euclidiana
     */
    private double calculateDistance(double[] point1, double[] point2) {
        double dx = point1[0] - point2[0];
        double dy = point1[1] - point2[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Obtiene la producción/demanda del nodo por nombre.
     */
    private double getNodeProd(String nodeName, List<Map<String, String>> nodes) {
        for (Map<String, String> n : nodes) {
            if (nodeName.equals(n.get("Node"))) {
                String prodStr = n.get("prod");
                if (prodStr == null || prodStr.isEmpty()) {
                    return 0.0;
                }
                try {
                    return Double.parseDouble(prodStr);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }

    /**
     * Guarda la solución en un archivo de texto.
     * 
     * @param instanceName Nombre de la instancia (sin extensión)
     * @throws IOException Si hay un error al escribir el archivo
     */
    public void saveToFile(String instanceName) throws IOException {
        String fileName = instanceName + "_sol.txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Escribir rutas de vehículos
            for (Map.Entry<String, List<String>> entry : vehicleRoutes.entrySet()) {
                String vehicle = entry.getKey();
                List<String> route = entry.getValue();
                writer.println(vehicle + "\t\t" + route);
            }
            
            // Escribir métricas
            writer.println();
            writer.println("CO2\t\t" + String.format("%.7f", totalCO2));
            writer.println("Distance\t" + String.format("%.6f", totalDistance));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Mostrar rutas de vehículos
        for (Map.Entry<String, List<String>> entry : vehicleRoutes.entrySet()) {
            String vehicle = entry.getKey();
            List<String> route = entry.getValue();
            sb.append(vehicle).append("\t\t").append(route).append("\n");
        }
        
        // Mostrar métricas
        sb.append("\n");
        sb.append("CO2\t\t").append(String.format("%.7f", totalCO2)).append("\n");
        sb.append("Distance\t").append(String.format("%.6f", totalDistance));
        
        return sb.toString();
    }
}
