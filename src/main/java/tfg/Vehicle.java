package tfg;

/**
 * Datos tipados de un vehículo de la instancia.
 */
public class Vehicle {
    private final String name;
    private final double load;
    private final int numUnits;
    private final double ef;
    private final double eo;

    public Vehicle(String name, double load, int numUnits, double ef, double eo) {
        this.name = name;
        this.load = load;
        this.numUnits = numUnits;
        this.ef = ef;
        this.eo = eo;
    }

    public String getName() {
        return name;
    }

    public double getLoad() {
        return load;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public double getEf() {
        return ef;
    }

    public double getEo() {
        return eo;
    }
}
