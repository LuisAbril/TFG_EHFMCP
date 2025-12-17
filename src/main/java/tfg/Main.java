package tfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase principal para demostrar el uso de Instance y Solution.
 */
public class Main {
    private static final String INSTANCES_DIR = "instances";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Listar instancias disponibles
            List<String> availableInstances = listAvailableInstances();
            
            if (availableInstances.isEmpty()) {
                System.err.println("No hay instancias disponibles en la carpeta '" + INSTANCES_DIR + "'");
                scanner.close();
                return;
            }
            
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║    INSTANCIAS DISPONIBLES                  ║");
            System.out.println("╚════════════════════════════════════════════╝");
            for (int i = 0; i < availableInstances.size(); i++) {
                System.out.println((i + 1) + ". " + availableInstances.get(i));
            }
            System.out.println();
            
            // Solicitar nombre de la instancia
            String instanceName = "";
            boolean validInput = false;
            
            while (!validInput) {
                System.out.print("Ingresa el nombre de la instancia (sin extensión): ");
                instanceName = scanner.nextLine().trim();
                
                if (instanceName.isEmpty()) {
                    System.out.println("El nombre no puede estar vacío.");
                    continue;
                }
                
                if (!availableInstances.contains(instanceName)) {
                    System.out.println("Instancia no encontrada. Intenta de nuevo.");
                    continue;
                }
                
                validInput = true;
            }
            
            // Cargar la instancia
            String filePath = INSTANCES_DIR + File.separator + instanceName + ".csv";
            Instance instance = new Instance(filePath);
            
            // Mostrar información de la instancia
            System.out.println();
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║       INFORMACIÓN DE LA INSTANCIA          ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("Archivo: " + instance.getFileName());
            System.out.println("Vehículos: " + instance.getNumberOfVehicles());
            System.out.println("Nodos: " + instance.getNumberOfNodes());
            System.out.println();
            
            // Mostrar información de vehículos
            System.out.println("┌─ VEHÍCULOS ─────────────────────────────────┐");
            List<Map<String, String>> vehicles = instance.getVehicles();
            for (Map<String, String> vehicle : vehicles) {
                System.out.println("├─ " + vehicle.get("Vehicle"));
                System.out.println("│  ├─ Capacidad: " + vehicle.get("Load") + " kg");
                System.out.println("│  ├─ Unidades: " + vehicle.get("Num_v"));
                System.out.println("│  ├─ Emisiones lleno (Ef): " + vehicle.get("Ef"));
                System.out.println("│  └─ Emisiones vacío (Eo): " + vehicle.get("Eo"));
            }
            System.out.println("└─────────────────────────────────────────────┘");
            System.out.println();
            
            // Mostrar información de nodos
            System.out.println("┌─ NODOS ─────────────────────────────────────┐");
            List<Map<String, String>> nodes = instance.getNodes();
            for (Map<String, String> node : nodes) {
                String nodeName = node.get("Node");
                String coordX = node.get("coord_x");
                String coordY = node.get("coord_y");
                String prod = node.get("prod");
                
                System.out.println("├─ " + nodeName);
                System.out.println("│  ├─ Coordenadas: (" + coordX + ", " + coordY + ")");
                System.out.println("│  └─ Producción/Demanda: " + prod);
            }
            System.out.println("└─────────────────────────────────────────────┘");
            System.out.println();
            
            // Establecer la instancia en Solution
            Solution.setInstance(instance);
            
            // Crear el algoritmo de construcción aleatoria en este caso
            RandomConstructive constructive = new RandomConstructive(instance);
            
            // Generar solución aleatoria y evaluarla
            Solution solution = constructive.run();
            
            // Mostrar información de la solución
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║         INFORMACIÓN DE LA SOLUCIÓN         ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println(solution);
            System.out.println();
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Lista las instancias disponibles en la carpeta de instancias.
     * 
     * @return Lista con los nombres de las instancias (sin extensión)
     */
    private static List<String> listAvailableInstances() {
        List<String> instances = new ArrayList<>();
        File instancesDir = new File(INSTANCES_DIR);
        
        if (!instancesDir.exists() || !instancesDir.isDirectory()) {
            return instances;
        }
        
        File[] files = instancesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        
        if (files != null) {
            for (File file : files) {
                // Remover la extensión .csv
                String nameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");
                instances.add(nameWithoutExtension);
            }
        }
        
        return instances;
    }
}
