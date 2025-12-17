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
        Solution solution = new Solution();

        // Obtener información de la instancia
        List<Map<String, String>> nodes = instance.getNodes();
        List<Map<String, String>> vehicles = instance.getVehicles();

        // Filtrar nodos (excluir el depósito 'P')
        List<String> customerNodes = new ArrayList<>();
        for (Map<String, String> node : nodes) {
            String nodeName = node.get("Node");
            if (!nodeName.equals("P")) {
                customerNodes.add(nodeName);
            }
        }

        // Barajar el orden de visita de las granjas para que la asignación sea completamente aleatoria
        Collections.shuffle(customerNodes, random);

        // Obtener lista de vehículos disponibles y capacidades por unidad
        List<String> availableVehicles = new ArrayList<>();
        Map<String, Double> vehicleCapacityByUnit = new java.util.HashMap<>();
        for (Map<String, String> vehicle : vehicles) {
            String vehicleName = vehicle.get("Vehicle");
            int numVehicles = Integer.parseInt(vehicle.get("Num_v"));
            double capacity = Double.parseDouble(vehicle.get("Load"));

            for (int i = 0; i < numVehicles; i++) {
                String unitName = vehicleName + (i > 0 ? "_" + i : "");
                availableVehicles.add(unitName);
                vehicleCapacityByUnit.put(unitName, capacity);
            }
        }

        // Cargar actual por vehículo unidad
        Map<String, Double> routeLoadByVehicle = new java.util.HashMap<>();

        // Asignar aleatoriamente nodos a vehículos
        for (String node : customerNodes) {
            if (availableVehicles.isEmpty()) {
                throw new IllegalStateException("No hay vehículos disponibles para asignar todos los nodos");
            }

            // Peso (producción) del nodo
            double nodeProd = 0.0;
            for (Map<String, String> n : nodes) {
                if (node.equals(n.get("Node"))) {
                    String prodStr = n.get("prod");
                    nodeProd = (prodStr == null || prodStr.isEmpty()) ? 0.0 : Double.parseDouble(prodStr);
                    break;
                }
            }

            // Intentar asignar el nodo a un vehículo que no exceda la capacidad
            boolean assigned = false;
            
            // Primero: intentar aleatoriamente para mantener la aleatoriedad
            int attempts = 0;
            int maxRandomAttempts = availableVehicles.size() * 2;
            while (!assigned && attempts < maxRandomAttempts) {
                int randomVehicleIndex = random.nextInt(availableVehicles.size());
                String vehicleUnit = availableVehicles.get(randomVehicleIndex);
                double capacity = vehicleCapacityByUnit.get(vehicleUnit);
                double currentLoad = routeLoadByVehicle.getOrDefault(vehicleUnit, 0.0);

                if (currentLoad + nodeProd <= capacity + 1e-9) { // permitir pequeña tolerancia
                    Map<String, List<String>> routes = solution.getVehicleRoutes();
                    List<String> route = routes.getOrDefault(vehicleUnit, new ArrayList<>());
                    route.add(node);
                    solution.addRoute(vehicleUnit, route);
                    routeLoadByVehicle.put(vehicleUnit, currentLoad + nodeProd);
                    assigned = true;
                } else {
                    attempts++;
                }
            }
            
            // Si no se asignó aleatoriamente, buscar sistemáticamente el primer vehículo con espacio
            if (!assigned) {
                for (String vehicleUnit : availableVehicles) {
                    double capacity = vehicleCapacityByUnit.get(vehicleUnit);
                    double currentLoad = routeLoadByVehicle.getOrDefault(vehicleUnit, 0.0);
                    
                    if (currentLoad + nodeProd <= capacity + 1e-9) {
                        Map<String, List<String>> routes = solution.getVehicleRoutes();
                        List<String> route = routes.getOrDefault(vehicleUnit, new ArrayList<>());
                        route.add(node);
                        solution.addRoute(vehicleUnit, route);
                        routeLoadByVehicle.put(vehicleUnit, currentLoad + nodeProd);
                        assigned = true;
                        break;
                    }
                }
            }

            if (!assigned) {
                throw new IllegalStateException("Capacidad insuficiente: no se puede asignar el nodo " + node);
            }
        }

        // Evaluar la solución
        solution.evaluate();

        return solution;
    }
}
