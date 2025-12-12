package tfg;

/**
 * Clase que representa una posible solución al problema.
 * La instancia se incluye como atributo estático.
 */
public class Solution {
    private static Instance instance;
    private String solutionData;
    private double objectiveValue;

    /**
     * Constructor por defecto.
     */
    public Solution() {
        this.solutionData = "";
        this.objectiveValue = 0.0;
    }

    /**
     * Constructor con datos de solución.
     * 
     * @param solutionData Datos de la solución
     */
    public Solution(String solutionData) {
        this.solutionData = solutionData;
        this.objectiveValue = 0.0;
    }

    /**
     * Constructor con datos de solución y valor objetivo.
     * 
     * @param solutionData Datos de la solución
     * @param objectiveValue Valor objetivo de la solución
     */
    public Solution(String solutionData, double objectiveValue) {
        this.solutionData = solutionData;
        this.objectiveValue = objectiveValue;
    }

    /**
     * Establece la instancia estática.
     * 
     * @param inst Instancia a establecer
     */
    public static void setInstance(Instance inst) {
        instance = inst;
    }

    /**
     * Obtiene la instancia estática.
     * 
     * @return Instancia actual
     */
    public static Instance getInstance() {
        return instance;
    }

    /**
     * Obtiene los datos de la solución.
     * 
     * @return Datos de la solución
     */
    public String getSolutionData() {
        return solutionData;
    }

    /**
     * Establece los datos de la solución.
     * 
     * @param solutionData Datos de la solución
     */
    public void setSolutionData(String solutionData) {
        this.solutionData = solutionData;
    }

    /**
     * Obtiene el valor objetivo de la solución.
     * 
     * @return Valor objetivo
     */
    public double getObjectiveValue() {
        return objectiveValue;
    }

    /**
     * Establece el valor objetivo de la solución.
     * 
     * @param objectiveValue Valor objetivo
     */
    public void setObjectiveValue(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "solutionData='" + solutionData + '\'' +
                ", objectiveValue=" + objectiveValue +
                ", instance=" + (instance != null ? instance.toString() : "null") +
                '}';
    }
}
