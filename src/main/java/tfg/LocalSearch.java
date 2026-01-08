package tfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Búsqueda local con 2-Opt aplicada sobre las rutas de una solución.
 */
public class LocalSearch {

    /**
     * Aplica 2-Opt a cada ruta de la solución y devuelve una solución mejorada.
     * Si no hay mejora, retorna la misma estructura de rutas.
     */
    public Solution apply2Opt(Solution solution) {
        Instance inst = Solution.getInstance();
        if (inst == null) {
            throw new IllegalStateException("La instancia no ha sido establecida en Solution");
        }

        Map<String, double[]> coords = buildCoordinatesMap(inst.getNodes());
        Map<String, List<String>> improvedRoutes = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : solution.getVehicleRoutes().entrySet()) {
            List<String> route = new ArrayList<>(entry.getValue());
            if (route.size() < 4) {
                improvedRoutes.put(entry.getKey(), route);
                continue;
            }

            boolean improved;
            do {
                improved = false;
                for (int i = 0; i < route.size() - 2; i++) {
                    for (int k = i + 2; k < route.size(); k++) {
                        double delta = twoOptDelta(route, i, k, coords);
                        if (delta < -1e-9) { // mejora estricta
                            reverseSegment(route, i, k);
                            improved = true;
                        }
                    }
                }
            } while (improved);

            improvedRoutes.put(entry.getKey(), route);
        }

        Solution improvedSolution = new Solution();
        for (Map.Entry<String, List<String>> e : improvedRoutes.entrySet()) {
            improvedSolution.addRoute(e.getKey(), e.getValue());
        }
        improvedSolution.evaluate();
        return improvedSolution;
    }

    private Map<String, double[]> buildCoordinatesMap(List<NodeData> nodes) {
        Map<String, double[]> coordinates = new HashMap<>();
        for (NodeData node : nodes) {
            coordinates.put(node.getName(), new double[]{node.getX(), node.getY()});
        }
        return coordinates;
    }

    private double twoOptDelta(List<String> route, int i, int k, Map<String, double[]> coords) {
        String a = (i == 0) ? "P" : route.get(i - 1);
        String b = route.get(i);
        String c = route.get(k);
        String d = (k == route.size() - 1) ? "P" : route.get(k + 1);

        double[] ca = coords.get(a);
        double[] cb = coords.get(b);
        double[] cc = coords.get(c);
        double[] cd = coords.get(d);

        double current = distance(ca, cb) + distance(cc, cd);
        double proposed = distance(ca, cc) + distance(cb, cd);
        return proposed - current;
    }

    private double distance(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void reverseSegment(List<String> route, int i, int k) {
        while (i < k) {
            String tmp = route.get(i);
            route.set(i, route.get(k));
            route.set(k, tmp);
            i++;
            k--;
        }
    }
}
