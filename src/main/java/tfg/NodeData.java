package tfg;

/**
 * Datos tipados de un nodo.
 */
public class NodeData {
    private final String name;
    private final double x;
    private final double y;
    private final double prod;

    public NodeData(String name, double x, double y, double prod) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.prod = prod;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getProd() {
        return prod;
    }
}
