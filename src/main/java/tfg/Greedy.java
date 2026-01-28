package tfg;

import java.util.*;

/**
 * Algoritmo Greedy con GRASP para construir soluciones al problema VRP.
 * Utiliza una función de calidad basada en distancia y producción del nodo.
 */
public class Greedy {
    // PARÁMETRO GRASP (MODIFICAR AQUÍ)
    // alpha = 0.0 -> completamente aleatorio
    // alpha = 1.0 -> completamente greedy (siempre elige el mejor)
    private static final double ALPHA =0.99;
    
    private Instance instance;
    
    /**
     * Constructor del algoritmo Greedy.
     * 
     * @param instance La instancia del problema
     */
    public Greedy(Instance instance) {
        this.instance = instance;
    }
    
    /**
     * Ejecuta el algoritmo Greedy con GRASP.
     * 
     * @return Solución construida
     */
    public Solution run() {
        Map<String, List<String>> routes = new HashMap<>();
        Map<String, Double> vehicleLoads = new HashMap<>();
        List<NodeData> unassignedNodes = new ArrayList<>(instance.getNodes());
        
        // Remover el depósito de los nodos no asignados
        unassignedNodes.removeIf(node -> node.getName().equals("0"));
        
        // Inicializar capacidades de vehículos
        Map<String, Integer> vehicleUnitsAvailable = new HashMap<>();
        for (Vehicle vehicle : instance.getVehicles()) {
            vehicleUnitsAvailable.put(vehicle.getName(), vehicle.getNumUnits());
        }
        
        // Construir solución
        String currentPosition = "0"; // Comenzar en el depósito
        
        while (!unassignedNodes.isEmpty()) {
            // Calcular calidad de cada nodo no asignado
            Map<NodeData, Double> nodeQualities = new HashMap<>();
            
            for (NodeData node : unassignedNodes) {
                double quality = calculateQuality(currentPosition, node);
                nodeQualities.put(node, quality);
            }
            
            // Encontrar calidad mínima y máxima
            double minQuality = Collections.min(nodeQualities.values());
            double maxQuality = Collections.max(nodeQualities.values());
            
            // Calcular threshold usando GRASP (invertido: alpha=0 aleatorio, alpha=1 greedy)
            double threshold = maxQuality - ALPHA * (maxQuality - minQuality);
            
            // Construir RCL (Restricted Candidate List) con nodos que cumplan threshold
            List<NodeData> rcl = new ArrayList<>();
            for (Map.Entry<NodeData, Double> entry : nodeQualities.entrySet()) {
                if (entry.getValue() <= threshold) {
                    rcl.add(entry.getKey());
                }
            }
            
            // Seleccionar nodo aleatorio de la RCL
            NodeData selectedNode = rcl.get(new Random().nextInt(rcl.size()));
            
            // Encontrar vehículos con capacidad suficiente
            List<String> feasibleVehicles = new ArrayList<>();
            for (Vehicle vehicle : instance.getVehicles()) {
                if (vehicleUnitsAvailable.get(vehicle.getName()) > 0 && 
                    vehicle.getLoad() >= selectedNode.getProd()) {
                    feasibleVehicles.add(vehicle.getName());
                }
            }
            
            if (feasibleVehicles.isEmpty()) {
                // No hay vehículo disponible, reintentar
                break;
            }
            
            // Seleccionar vehículo aleatorio de los factibles
            String selectedVehicle = feasibleVehicles.get(new Random().nextInt(feasibleVehicles.size()));
            
            // Asignar nodo al vehículo
            routes.computeIfAbsent(selectedVehicle, k -> new ArrayList<>()).add(selectedNode.getName());
            
            // Actualizar carga del vehículo
            vehicleLoads.merge(selectedVehicle, selectedNode.getProd(), Double::sum);
            
            // Actualizar unidades disponibles si se alcanza la capacidad
            Vehicle vehicleData = instance.getVehicles().stream()
                .filter(v -> v.getName().equals(selectedVehicle))
                .findFirst()
                .orElse(null);
            
            if (vehicleData != null && vehicleLoads.get(selectedVehicle) >= vehicleData.getLoad()) {
                vehicleUnitsAvailable.put(selectedVehicle, vehicleUnitsAvailable.get(selectedVehicle) - 1);
                vehicleLoads.put(selectedVehicle, 0.0);
                currentPosition = "0"; // Volver al depósito
            } else {
                currentPosition = selectedNode.getName(); // Continuar desde este nodo
            }
            
            // Remover nodo de los no asignados
            unassignedNodes.remove(selectedNode);
        }
        
        // Crear y evaluar la solución
        Solution solution = new Solution();
        for (Map.Entry<String, List<String>> entry : routes.entrySet()) {
            solution.addRoute(entry.getKey(), entry.getValue());
        }
        solution.evaluate();
        
        return solution;
    }
    
    /**
     * Calcula la calidad de un nodo.
     * Menor calidad = mejor (más cercano y menor producción).
     * 
     * @param currentPos Posición actual
     * @param node Nodo a evaluar
     * @return Valor de calidad
     */
    private double calculateQuality(String currentPos, NodeData node) {
        // Obtener coordenadas de la posición actual
        NodeData currentNode = instance.getNodes().stream()
            .filter(n -> n.getName().equals(currentPos))
            .findFirst()
            .orElse(null);
        
        if (currentNode == null) {
            return Double.MAX_VALUE;
        }
        
        // Calcular distancia euclidiana
        double dx = node.getX() - currentNode.getX();
        double dy = node.getY() - currentNode.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        // Calidad = distancia + producción (normalizado)
        // Menor distancia y menor producción = mejor calidad (menor valor)
        double quality = distance + node.getProd() ; // Normalizar producción
        
        return quality;
    }
}
