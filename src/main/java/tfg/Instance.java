package tfg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa una instancia del problema.
 * Lee un archivo, lo procesa y guarda la información en diferentes atributos.
 */
public class Instance {
    private String fileName;
    private List<String> data;
    private int numberOfLines;
    private String content;

    /**
     * Constructor que lee un archivo, lo procesa y guarda la información.
     * 
     * @param filePath Ruta del archivo a leer
     * @throws IOException Si hay un error al leer el archivo
     */
    public Instance(String filePath) throws IOException {
        this.fileName = filePath;
        this.data = new ArrayList<>();
        this.content = "";
        readFile(filePath);
    }

    /**
     * Método privado que lee el archivo y procesa su contenido.
     * 
     * @param filePath Ruta del archivo a leer
     * @throws IOException Si hay un error al leer el archivo
     */
    private void readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
                contentBuilder.append(line).append("\n");
            }
        }
        
        this.content = contentBuilder.toString();
        this.numberOfLines = data.size();
    }

    /**
     * Obtiene el nombre del archivo.
     * 
     * @return Nombre del archivo
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Obtiene las líneas de datos leídas del archivo.
     * 
     * @return Lista de líneas
     */
    public List<String> getData() {
        return new ArrayList<>(data);
    }

    /**
     * Obtiene el número de líneas leídas.
     * 
     * @return Número de líneas
     */
    public int getNumberOfLines() {
        return numberOfLines;
    }

    /**
     * Obtiene el contenido completo del archivo.
     * 
     * @return Contenido del archivo
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "fileName='" + fileName + '\'' +
                ", numberOfLines=" + numberOfLines +
                '}';
    }
}
