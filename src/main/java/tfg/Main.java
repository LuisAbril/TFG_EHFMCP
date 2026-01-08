package tfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
            List<Vehicle> vehicles = instance.getVehicles();
            for (Vehicle vehicle : vehicles) {
                System.out.println("├─ " + vehicle.getName());
                System.out.println("│  ├─ Capacidad: " + vehicle.getLoad() + " kg");
                System.out.println("│  ├─ Unidades: " + vehicle.getNumUnits());
                System.out.println("│  ├─ Emisiones lleno (Ef): " + vehicle.getEf());
                System.out.println("│  └─ Emisiones vacío (Eo): " + vehicle.getEo());
            }
            System.out.println("└─────────────────────────────────────────────┘");
            System.out.println();
            
            // Mostrar información de nodos
            System.out.println("┌─ NODOS ─────────────────────────────────────┐");
            List<NodeData> nodes = instance.getNodes();
            for (NodeData node : nodes) {
                System.out.println("├─ " + node.getName());
                System.out.println("│  ├─ Coordenadas: (" + node.getX() + ", " + node.getY() + ")");
                System.out.println("│  └─ Producción/Demanda: " + node.getProd());
            }
            System.out.println("└─────────────────────────────────────────────┘");
            System.out.println();
            
            // Establecer la instancia en Solution
            Solution.setInstance(instance);
            
            // Medir tiempo de CPU
            long startTime = System.currentTimeMillis();
            long startNanoTime = System.nanoTime();
            
            System.out.println();
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║     FASE 1: CONSTRUCCIÓN ALEATORIA         ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println();
            
            // Fase 1: Ejecutar Random Constructive
            RandomConstructive constructive = new RandomConstructive(instance);
            Solution initialSolution = constructive.run();
            double initialCO2 = initialSolution.getTotalCO2();
            double initialDistance = initialSolution.getTotalDistance();
            
            System.out.println("✓ Solución inicial generada");
            System.out.println("  CO2 inicial: " + String.format("%.7f", initialCO2));
            System.out.println("  Distancia inicial: " + String.format("%.6f", initialDistance));
            System.out.println();
            
            // Fase 2: Aplicar Local Search (2-Opt)
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║     FASE 2: BÚSQUEDA LOCAL (2-OPT)         ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println();
            
            LocalSearch localSearch = new LocalSearch();
            Solution improvedSolution = localSearch.apply2Opt(initialSolution);
            double improvedCO2 = improvedSolution.getTotalCO2();
            double improvedDistance = improvedSolution.getTotalDistance();
            
            double co2Improvement = ((initialCO2 - improvedCO2) / initialCO2) * 100;
            double distanceImprovement = ((initialDistance - improvedDistance) / initialDistance) * 100;
            
            System.out.println("✓ Búsqueda local completada");
            System.out.println("  CO2 mejorado: " + String.format("%.7f", improvedCO2));
            System.out.println("  Distancia mejorada: " + String.format("%.6f", improvedDistance));
            System.out.println();
            System.out.println("📊 MEJORAS CONSEGUIDAS:");
            System.out.println("  CO2: " + String.format("%.2f%%", co2Improvement) + " (" + String.format("%.7f", initialCO2 - improvedCO2) + ")");
            System.out.println("  Distancia: " + String.format("%.2f%%", distanceImprovement) + " (" + String.format("%.6f", initialDistance - improvedDistance) + ")");
            System.out.println();
            
            // Calcular tiempo transcurrido
            long endTime = System.currentTimeMillis();
            long endNanoTime = System.nanoTime();
            long elapsedMillis = endTime - startTime;
            long elapsedNanos = endNanoTime - startNanoTime;
            double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
            
            System.out.println("═════════════════════════════════════════════");
            System.out.println("Solución final después de Local Search:");
            System.out.println("CO2: " + String.format("%.7f", improvedCO2));
            System.out.println("═════════════════════════════════════════════");
            System.out.println("⏱️  TIEMPO DE EJECUCIÓN:");
            System.out.println("    • Milisegundos: " + elapsedMillis + " ms");
            System.out.println("    • Segundos: " + String.format("%.3f", elapsedSeconds) + " s");
            System.out.println("═════════════════════════════════════════════");
            System.out.println();
            
            // Guardar la mejor solución en un archivo
            try {
                improvedSolution.saveToFile(instanceName);
                System.out.println("Solución guardada en: " + instanceName + "_sol.txt");
            } catch (IOException e) {
                System.err.println("Error al guardar la solución: " + e.getMessage());
            }
            System.out.println();
            
            // Mostrar información de la mejor solución
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║         INFORMACIÓN DE LA SOLUCIÓN         ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println(improvedSolution);
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
