package tfg;

import java.util.ArrayList;
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

        // Obtener lista de vehículos disponibles
        List<String> availableVehicles = new ArrayList<>();
        for (Map<String, String> vehicle : vehicles) {
            String vehicleName = vehicle.get("Vehicle");
            int numVehicles = Integer.parseInt(vehicle.get("Num_v"));
            
            // Añadir cada unidad del vehículo
            for (int i = 0; i < numVehicles; i++) {
                availableVehicles.add(vehicleName + (i > 0 ? "_" + i : ""));
            }
        }

        // Asignar aleatoriamente nodos a vehículos
        int vehicleIndex = 0;
        for (String node : customerNodes) {
            if (availableVehicles.isEmpty()) {
                break;
            }

            // Seleccionar un vehículo aleatorio
            int randomVehicleIndex = random.nextInt(availableVehicles.size());
            String vehicle = availableVehicles.get(randomVehicleIndex);

            // Obtener la ruta actual del vehículo o crear una nueva
            Map<String, List<String>> routes = solution.getVehicleRoutes();
            List<String> route;
            
            if (routes.containsKey(vehicle)) {
                route = routes.get(vehicle);
            } else {
                route = new ArrayList<>();
            }

            // Añadir el nodo a la ruta
            route.add(node);

            // Actualizar la ruta en la solución
            solution.addRoute(vehicle, route);
        }

        // Evaluar la solución
        solution.evaluate();

        return solution;
    }
}
