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
    private static final String SOLUTIONS_DIR = "greedy2solutions0.99";
    
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
            
            // Mostrar instancias disponibles
            System.out.println("Instancias disponibles:");
            for (int i = 0; i < availableInstances.size(); i++) {
                System.out.println((i + 1) + ". " + availableInstances.get(i));
            }
            
            // Seleccionar instancia
            System.out.print("\nSelecciona el número de la instancia a ejecutar: ");
            int instanceChoice = -1;
            try {
                instanceChoice = scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.err.println("Error: Por favor ingresa un número válido.");
                scanner.nextLine(); // Limpiar el buffer
                scanner.close();
                return;
            }
            
            if (instanceChoice < 1 || instanceChoice > availableInstances.size()) {
                System.err.println("Opción inválida.");
                scanner.close();
                return;
            }
            
            String selectedInstance = availableInstances.get(instanceChoice - 1);
            
            // Seleccionar número de iteraciones
            System.out.print("¿Cuántas iteraciones deseas realizar?: ");
            int iterations = -1;
            try {
                iterations = scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                System.err.println("Error: Por favor ingresa un número válido.");
                scanner.nextLine(); // Limpiar el buffer
                scanner.close();
                return;
            }
            
            if (iterations <= 0) {
                System.err.println("El número de iteraciones debe ser mayor a 0.");
                scanner.close();
                return;
            }
            
            System.out.println("\nProcesando instancia: " + selectedInstance + " con " + iterations + " iteraciones...\n");
            
            System.out.println("MejorCO2");
            
            try {
                // Cargar la instancia
                String filePath = INSTANCES_DIR + File.separator + selectedInstance + ".csv";
                Instance instance = new Instance(filePath);
                Solution.setInstance(instance);
                
                LocalSearch localSearch = new LocalSearch();
                
                // Variables para rastrear la mejor solución
                Solution bestSolution = null;
                double bestCO2 = Double.MAX_VALUE;
                double bestDistance = 0.0;
                
                // Ejecutar N iteraciones
                for (int i = 1; i <= iterations; i++) {
                    // Medir tiempo de CPU
                    long startNanoTime = System.nanoTime();
                    
                    // Fase 1: Construcción Greedy con GRASP
                    Greedy greedy = new Greedy(instance);
                    Solution initialSolution = greedy.run();
                    
                    // Fase 2: Búsqueda Local (2-Opt)
                    Solution improvedSolution = localSearch.apply2Opt(initialSolution);
                    
                    // Calcular tiempo transcurrido
                    long endNanoTime = System.nanoTime();
                    double elapsedSeconds = (endNanoTime - startNanoTime) / 1_000_000_000.0;
                    
                    double currentCO2 = improvedSolution.getTotalCO2();
                    double currentDistance = improvedSolution.getTotalDistance();
                    
                    // Verificar si es la mejor solución encontrada hasta ahora
                    if (currentCO2 < bestCO2) {
                        bestCO2 = currentCO2;
                        bestDistance = currentDistance;
                        bestSolution = improvedSolution;
                    }
                    
                    // Imprimir la mejor solución encontrada hasta el momento
                    System.out.println(String.format("%.0f", bestCO2));
                }
                
            } catch (IOException e) {
                System.err.println("Error al procesar instancia " + selectedInstance + ": " + e.getMessage());
            }
            
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
