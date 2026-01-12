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
    private static final String SOLUTIONS_DIR = "instanceSolutions";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Crear la carpeta de soluciones si no existe
            File solutionsFolder = new File(SOLUTIONS_DIR);
            if (!solutionsFolder.exists()) {
                solutionsFolder.mkdirs();
            }
            
            // Listar instancias disponibles
            List<String> availableInstances = listAvailableInstances();
            
            if (availableInstances.isEmpty()) {
                System.err.println("No hay instancias disponibles en la carpeta '" + INSTANCES_DIR + "'");
                scanner.close();
                return;
            }
            
            // Tiempo límite de ejecución: 1 segundo
            final long TIME_LIMIT_NANOS = 1_000_000_000L; // 1 segundo en nanosegundos
            
            // Listas para almacenar resultados
            List<String> instanceNames = new ArrayList<>();
            List<Double> co2Results = new ArrayList<>();
            List<Double> cpuTimeResults = new ArrayList<>();
            
            // Procesar todas las instancias
            for (String instanceName : availableInstances) {
                System.out.println("Procesando instancia: " + instanceName);
                
                try {
                    // Cargar la instancia
                    String filePath = INSTANCES_DIR + File.separator + instanceName + ".csv";
                    Instance instance = new Instance(filePath);
                    Solution.setInstance(instance);
                    
                    // Medir tiempo de CPU
                    long startNanoTime = System.nanoTime();
                    
                    // Variables para rastrear la mejor solución global
                    Solution bestSolution = null;
                    double bestCO2 = Double.MAX_VALUE;
                    double bestDistance = 0.0;
                    LocalSearch localSearch = new LocalSearch();
                    int iterationsCompleted = 0;
                    
                    // Ejecutar mientras no se supere 1 segundo
                    while ((System.nanoTime() - startNanoTime) < TIME_LIMIT_NANOS) {
                        iterationsCompleted++;
                        
                        // Fase 1: Construcción Greedy con GRASP
                        Greedy greedy = new Greedy(instance);
                        Solution initialSolution = greedy.run();
                        
                        // Fase 2: Búsqueda Local (2-Opt)
                        Solution improvedSolution = localSearch.apply2Opt(initialSolution);
                        double improvedCO2 = improvedSolution.getTotalCO2();
                        double improvedDistance = improvedSolution.getTotalDistance();
                        
                        // Verificar si es la mejor solución encontrada hasta ahora
                        if (improvedCO2 < bestCO2) {
                            bestCO2 = improvedCO2;
                            bestDistance = improvedDistance;
                            bestSolution = improvedSolution;
                        }
                        
                    }
                    
                    // Calcular tiempo transcurrido
                    long endNanoTime = System.nanoTime();
                    double elapsedSeconds = (endNanoTime - startNanoTime) / 1_000_000_000.0;
                    
                    // Guardar resultados
                    instanceNames.add(instanceName);
                    co2Results.add(bestCO2);
                    cpuTimeResults.add(elapsedSeconds);
                    
                    // Guardar la mejor solución en un archivo
                    try {
                        String solutionPath = SOLUTIONS_DIR + File.separator + instanceName + "_sol.txt";
                        bestSolution.saveToFile(solutionPath);
                    } catch (IOException e) {
                        System.err.println("Error al guardar la solución: " + e.getMessage());
                    }
                    
                } catch (IOException e) {
                    System.err.println("Error al procesar instancia " + instanceName + ": " + e.getMessage());
                }
                System.out.println();
            }
            
            // Mostrar resumen final
            System.out.println();
            System.out.println("CPU");
            for (int i = 0; i < cpuTimeResults.size(); i++) {
                System.out.println(String.format("%.3f", cpuTimeResults.get(i)));
            }
            
            System.out.println();
            System.out.println("co2");
            for (int i = 0; i < co2Results.size(); i++) {
                System.out.println(String.format("%.7f", co2Results.get(i)));
            }
            System.out.println();
            
        
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
