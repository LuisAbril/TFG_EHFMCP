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
            
            // Solicitar número de iteraciones
            int iterations = 100;
            boolean validIterations = false;
            while (!validIterations) {
                System.out.print("Ingresa el número de iteraciones (default 1): ");
                String iterInput = scanner.nextLine().trim();
                if (iterInput.isEmpty()) {
                    iterations = 1;
                    validIterations = true;
                } else {
                    try {
                        iterations = Integer.parseInt(iterInput);
                        if (iterations < 1) {
                            System.out.println("Las iteraciones deben ser al menos 1.");
                            continue;
                        }
                        validIterations = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Ingresa un número válido.");
                    }
                }
            }
            System.out.println();
            
            // Medir tiempo de CPU
            long startTime = System.currentTimeMillis();
            long startNanoTime = System.nanoTime();
            
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║  EJECUTANDO " + String.format("%d", iterations) + " ITERACIONES DEL ALGORITMO  ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println();
            
            // Variables para rastrear la mejor solución global
            Solution bestSolution = null;
            double bestCO2 = Double.MAX_VALUE;
            double bestDistance = 0.0;
            LocalSearch localSearch = new LocalSearch();
            
            for (int iter = 1; iter <= iterations; iter++) {
                System.out.println("--- Iteración " + iter + " / " + iterations + " ---");
                
                // Fase 1: Construcción Aleatoria
                RandomConstructive constructive = new RandomConstructive(instance);
                Solution initialSolution = constructive.run();
                double initialCO2 = initialSolution.getTotalCO2();
                
                // Fase 2: Búsqueda Local (2-Opt)
                Solution improvedSolution = localSearch.apply2Opt(initialSolution);
                double improvedCO2 = improvedSolution.getTotalCO2();
                double improvedDistance = improvedSolution.getTotalDistance();
                
                // Mostrar resultado de esta iteración
                double co2Improvement = ((initialCO2 - improvedCO2) / initialCO2) * 100;
                System.out.println("  CO2 inicial:   " + String.format("%.7f", initialCO2));
                System.out.println("  CO2 mejorado:  " + String.format("%.7f", improvedCO2));
                System.out.println("  Mejora: " + String.format("%.2f%%", co2Improvement));
                
                // Verificar si es la mejor solución encontrada hasta ahora
                if (improvedCO2 < bestCO2) {
                    bestCO2 = improvedCO2;
                    bestDistance = improvedDistance;
                    bestSolution = improvedSolution;
                    System.out.println("  ★ NUEVA MEJOR SOLUCIÓN");
                }
                System.out.println();
            }
            
            // Calcular tiempo transcurrido
            long endTime = System.currentTimeMillis();
            long endNanoTime = System.nanoTime();
            long elapsedMillis = endTime - startTime;
            long elapsedNanos = endNanoTime - startNanoTime;
            double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
            
            System.out.println("═════════════════════════════════════════════");
            System.out.println("Mejor solución encontrada en " + iterations + " iteraciones:");
            System.out.println("CO2: " + String.format("%.7f", bestCO2));
            System.out.println("Distancia: " + String.format("%.6f", bestDistance));
            System.out.println("═════════════════════════════════════════════");
            System.out.println("⏱️  TIEMPO DE EJECUCIÓN:");
            System.out.println("    • Milisegundos: " + elapsedMillis + " ms");
            System.out.println("    • Segundos: " + String.format("%.3f", elapsedSeconds) + " s");
            System.out.println("═════════════════════════════════════════════");
            System.out.println();
            
            // Guardar la mejor solución en un archivo
            try {
                bestSolution.saveToFile(instanceName);
                System.out.println("Mejor solución guardada en: " + instanceName + "_sol.txt");
            } catch (IOException e) {
                System.err.println("Error al guardar la solución: " + e.getMessage());
            }
            System.out.println();
            
            // Mostrar información de la mejor solución
            System.out.println("╔════════════════════════════════════════════╗");
            System.out.println("║    INFORMACIÓN DE LA MEJOR SOLUCIÓN        ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println(bestSolution);
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
