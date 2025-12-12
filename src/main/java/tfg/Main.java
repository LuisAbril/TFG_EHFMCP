package tfg;

import java.io.IOException;

/**
 * Clase principal para demostrar el uso de Instance y Solution.
 */
public class Main {
    public static void main(String[] args) {
        try {
            // Crear una instancia leyendo un archivo
            Instance instance = new Instance("instance.txt");
            
            // Mostrar información de la instancia
            System.out.println("Instancia cargada:");
            System.out.println(instance);
            System.out.println("Número de líneas: " + instance.getNumberOfLines());
            System.out.println();
            
            // Establecer la instancia en Solution
            Solution.setInstance(instance);
            
            // Crear una solución
            Solution solution = new Solution("Solución de prueba", 42.5);
            
            // Mostrar información de la solución
            System.out.println("Solución creada:");
            System.out.println(solution);
            System.out.println();
            
            // Verificar que la instancia está disponible desde Solution
            System.out.println("Instancia desde Solution:");
            System.out.println(Solution.getInstance());
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
