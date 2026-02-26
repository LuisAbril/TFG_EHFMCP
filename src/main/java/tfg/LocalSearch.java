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
     * Variable Neighborhood Descent (VND).
     * Vecindarios usados en este orden: Inserción -> 2-Opt.
     * Si un vecindario mejora, se reinicia desde el primero.
     */
    public Solution applyVND(Solution solution) {
        Solution current = solution;
        int neighborhoodIndex = 0;

        while (neighborhoodIndex < 2) {
            Solution candidate;
            if (neighborhoodIndex == 0) {
                candidate = applyInsertion(current);
            } else {
                candidate = apply2Opt(current);
            }

            if (isBetter(candidate, current)) {
                current = candidate;
                neighborhoodIndex = 0;
            } else {
                neighborhoodIndex++;
            }
        }

        return current;
    }

    /**
     * Ejecuta ambas búsquedas locales (2-Opt e Inserción) y devuelve la mejor solución.
     * El criterio de selección es menor CO2 total.
     */
    public Solution applyBothLocalSearch(Solution solution) {
        Solution opt2Solution = apply2Opt(solution);
        Solution insertionSolution = applyInsertion(solution);

        double insertionCO2 = insertionSolution.getTotalCO2();
        double opt2CO2 = opt2Solution.getTotalCO2();

        if (insertionCO2 < opt2CO2) {
            return insertionSolution;
        }
        if (insertionCO2 > opt2CO2) {
            return opt2Solution;
        }

        if (insertionSolution.getTotalDistance() < opt2Solution.getTotalDistance()) {
            return insertionSolution;
        }
        return opt2Solution;
    }

    private boolean isBetter(Solution candidate, Solution reference) {
        double epsilon = 1e-9;
        if (candidate.getTotalCO2() < reference.getTotalCO2() - epsilon) {
            return true;
        }
        if (Math.abs(candidate.getTotalCO2() - reference.getTotalCO2()) <= epsilon
                && candidate.getTotalDistance() < reference.getTotalDistance() - epsilon) {
            return true;
        }
        return false;
    }

    /**
     * Aplica búsqueda local por inserción a cada ruta de la solución y devuelve una solución mejorada.
     * El criterio de mejora es reducir la distancia de la ruta.
     */
    public Solution applyInsertion(Solution solution) {
        Instance inst = Solution.getInstance();
        if (inst == null) {
            throw new IllegalStateException("La instancia no ha sido establecida en Solution");
        }

        Map<String, double[]> coords = buildCoordinatesMap(inst.getNodes());
        String depot = getDepotNodeKey(coords);
        Map<String, List<String>> improvedRoutes = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : solution.getVehicleRoutes().entrySet()) {
            List<String> route = new ArrayList<>(entry.getValue());
            if (route.size() < 2) {
                improvedRoutes.put(entry.getKey(), route);
                continue;
            }

            boolean improved;
            do {
                improved = false;
                double baseDistance = routeDistance(route, coords, depot);
                double bestDelta = 0.0;
                int bestFrom = -1;
                int bestTo = -1;

                for (int from = 0; from < route.size(); from++) {
                    String movedNode = route.remove(from);

                    for (int to = 0; to <= route.size(); to++) {
                        route.add(to, movedNode);
                        double candidateDistance = routeDistance(route, coords, depot);
                        double delta = candidateDistance - baseDistance;

                        if (delta < bestDelta - 1e-9) {
                            bestDelta = delta;
                            bestFrom = from;
                            bestTo = to;
                        }

                        route.remove(to);
                    }

                    route.add(from, movedNode);
                }

                if (bestFrom != -1) {
                    String movedNode = route.remove(bestFrom);
                    route.add(bestTo, movedNode);
                    improved = true;
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

    private String getDepotNodeKey(Map<String, double[]> coords) {
        if (coords.containsKey("P")) {
            return "P";
        }
        if (coords.containsKey("0")) {
            return "0";
        }
        throw new IllegalStateException("No se encontró el depósito (P o 0) en la instancia");
    }

    private double routeDistance(List<String> route, Map<String, double[]> coords, String depot) {
        if (route.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        String previous = depot;

        for (String node : route) {
            total += distance(coords.get(previous), coords.get(node));
            previous = node;
        }

        total += distance(coords.get(previous), coords.get(depot));
        return total;
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
