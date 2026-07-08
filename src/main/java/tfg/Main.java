package tfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

/**
 * Clase principal para demostrar el uso de Instance y Solution.
 */
public class Main {
    private static final String INSTANCES_DIR = "instances";
    private static final String SOLUTIONS_DIR = "greedy1solutions0.99VND180secs";
    private static final long EXECUTION_TIME_NS = 180_000_000_000L;
    
    public static void main(String[] args) {
        // Crear la carpeta de soluciones si no existe
        File solutionsFolder = new File(SOLUTIONS_DIR);
        if (!solutionsFolder.exists()) {
            solutionsFolder.mkdirs();
        }
        
        // Listar instancias disponibles
        List<String> availableInstances = listAvailableInstances();
        
        if (availableInstances.isEmpty()) {
            System.err.println("No hay instancias disponibles en la carpeta '" + INSTANCES_DIR + "'");
            return;
        }

        Collections.sort(availableInstances);
        
        StringBuilder co2Buffer = new StringBuilder();
        long totalIterations = 0;
        int processedInstances = 0;
        
        for (String selectedInstance : availableInstances) {
            try {
                // Cargar la instancia
                String filePath = INSTANCES_DIR + File.separator + selectedInstance + ".csv";
                Instance instance = new Instance(filePath);
                Solution.setInstance(instance);
                
                LocalSearch localSearch = new LocalSearch();
                
                // Variables para rastrear la mejor solución
                double bestCO2 = Double.MAX_VALUE;
                Solution bestSolution = null;
                int instanceIterations = 0;
                long totalStartNanoTime = System.nanoTime();
                long endTime = totalStartNanoTime + EXECUTION_TIME_NS;
                
                // Ejecutar durante 1 segundo
                while (System.nanoTime() < endTime) {
                    // Fase 1: Construcción Greedy con GRASP
                    GRASP greedy = new GRASP(instance);
                    Solution initialSolution = greedy.run();
                    
                    // Fase 2: Búsqueda Local VND (Inserción -> 2-Opt)
                    Solution improvedSolution = localSearch.applyVND(initialSolution);
                    instanceIterations++;
                    
                    double currentCO2 = improvedSolution.getTotalCO2();
                    
                    // Verificar si es la mejor solución encontrada hasta ahora
                    if (currentCO2 < bestCO2) {
                        bestCO2 = currentCO2;
                        bestSolution = improvedSolution;
                    }
                }

                if (bestSolution != null) {
                    String solutionPath = SOLUTIONS_DIR + File.separator + selectedInstance + "_sol.txt";
                    bestSolution.saveToFile(solutionPath);
                }

                totalIterations += instanceIterations;
                processedInstances++;

                String resultLine = String.format("%.7f", bestCO2);
                System.out.println(resultLine);
                co2Buffer.append(resultLine).append(System.lineSeparator());
            } catch (IOException e) {
                String errorLine = "Error al procesar instancia " + selectedInstance + ": " + e.getMessage();
                System.err.println(errorLine);
                co2Buffer.append(errorLine).append(System.lineSeparator());
            }
        }

        if (processedInstances > 0) {
            double averageIterations = (double) totalIterations / processedInstances;
            String averageLine = String.format("%.2f", averageIterations);
            System.out.println(averageLine);
            co2Buffer.append(averageLine).append(System.lineSeparator());
        }

        Toolkit.getDefaultToolkit().getSystemClipboard()
            .setContents(new StringSelection(co2Buffer.toString()), null);
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
