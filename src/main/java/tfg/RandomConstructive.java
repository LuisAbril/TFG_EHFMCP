package tfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Clase que implementa el algoritmo de construcción aleatoria.
 * Genera soluciones asignando aleatoriamente nodos a vehículos.
 */
public class RandomConstructive {
    private Instance instance;
    private Random random;

    /**
     * Constructor del algoritmo RandomConstructive.
     * 
     * @param instance Instancia del problema
     */
    public RandomConstructive(Instance instance) {
        this.instance = instance;
        this.random = new Random();
    }

    /**
     * Constructor del algoritmo RandomConstructive con semilla para reproducibilidad.
     * 
     * @param instance Instancia del problema
     * @param seed Semilla para el generador de números aleatorios
     */
    public RandomConstructive(Instance instance, long seed) {
        this.instance = instance;
        this.random = new Random(seed);
    }

    /**
     * Ejecuta el algoritmo y devuelve una solución generada aleatoriamente.
     * 
     * @return Solución evaluada generada aleatoriamente
     */
    public Solution run() {
        // Obtener información de la instancia
        List<NodeData> nodes = instance.getNodes();
        List<Vehicle> vehicles = instance.getVehicles();

        // Filtrar nodos (excluir el depósito 'P')
        List<String> customerNodes = new ArrayList<>();
        for (NodeData node : nodes) {
            String nodeName = node.getName();
            if (!"P".equals(nodeName)) {
                customerNodes.add(nodeName);
            }
        }

        // Unidades de vehículos disponibles y capacidades por unidad
        List<String> availableVehicles = new ArrayList<>();
        Map<String, Double> vehicleCapacityByUnit = new java.util.HashMap<>();
        Map<String, Double> prodByNode = new java.util.HashMap<>();
        
        for (Vehicle vehicle : vehicles) {
            String vehicleName = vehicle.getName();
            int numVehicles = vehicle.getNumUnits();
            double capacity = vehicle.getLoad();
            for (int i = 0; i < numVehicles; i++) {
                String unitName = vehicleName + (i > 0 ? "_" + i : "");
                availableVehicles.add(unitName);
                vehicleCapacityByUnit.put(unitName, capacity);
            }
        }
        
        for (NodeData n : nodes) {
            prodByNode.put(n.getName(), Math.abs(n.getProd()));
        }

        // Reintentar hasta encontrar una asignación factible
        int maxAttempts = 1000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Solution solution = new Solution();
            Map<String, Double> loadByVehicle = new java.util.HashMap<>();
            
            Collections.shuffle(customerNodes, random);
            boolean feasible = true;
            
            for (String nodeName : customerNodes) {
                double nodeProd = prodByNode.getOrDefault(nodeName, 0.0);
                
                // Encontrar vehículos que puedan llevar este nodo
                List<String> feasibleVehicles = new ArrayList<>();
                for (String vehicle : availableVehicles) {
                    double capacity = vehicleCapacityByUnit.get(vehicle);
                    double currentLoad = loadByVehicle.getOrDefault(vehicle, 0.0);
                    if (currentLoad + nodeProd <= capacity + 1e-9) {
                        feasibleVehicles.add(vehicle);
                    }
                }
                
                // Si no hay vehículos factibles, descartar esta solución y reintentar
                if (feasibleVehicles.isEmpty()) {
                    feasible = false;
                    break;
                }
                
                // Asignar aleatoriamente a uno de los vehículos factibles
                String selectedVehicle = feasibleVehicles.get(random.nextInt(feasibleVehicles.size()));
                Map<String, List<String>> routes = solution.getVehicleRoutes();
                List<String> route = routes.getOrDefault(selectedVehicle, new ArrayList<>());
                route.add(nodeName);
                solution.addRoute(selectedVehicle, route);
                loadByVehicle.put(selectedVehicle, loadByVehicle.getOrDefault(selectedVehicle, 0.0) + nodeProd);
            }
            
            if (feasible) {
                solution.evaluate();
                return solution;
            }
        }

        throw new IllegalStateException("No se encontró una asignación factible tras " + maxAttempts + " intentos. La instancia podría ser infactible.");
    }
}
