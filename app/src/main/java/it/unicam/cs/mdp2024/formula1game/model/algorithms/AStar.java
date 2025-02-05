package it.unicam.cs.mdp2024.formula1game.model.algorithms;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.*;

import java.util.*;

/**
 * Implementazione dell'algoritmo A* per il pathfinding nel circuito.
 * Trova il percorso migliore tra due punti evitando ostacoli, rispettando
 * le regole del gioco di Formula 1.
 */
public class AStar {

    private static class Node implements Comparable<Node> {
        IPosition position;
        Node parent;
        double gCost; // Costo dal punto di partenza
        double hCost; // Euristica (distanza stimata al target)
        double fCost; // Costo totale (g + h)
        IVelocity velocity; // Velocità corrente del nodo

        Node(IPosition position, Node parent, double gCost, double hCost, IVelocity velocity) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
            this.velocity = velocity;
        }

        void updateCosts(Node parent, double gCost, double hCost) {
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost, other.fCost);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return position.equals(node.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }

    /**
     * Trova il percorso più efficiente tra start e target evitando ostacoli.
     * 
     * @param start        posizione di partenza
     * @param target       posizione di arrivo
     * @param circuit      circuito con informazioni sugli ostacoli
     * @param lastVelocity velocità precedente per calcolare il punto principale
     * @return lista di posizioni che formano il percorso, vuota se non trovato
     */
    public List<IPosition> findPath(IPosition start, IPosition target, ICircuit circuit, IVelocity lastVelocity) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<IPosition, Node> openMap = new HashMap<>();
        Set<IPosition> closedSet = new HashSet<>();

        Node startNode = new Node(start, null, 0, heuristic(start, target), lastVelocity);
        openSet.add(startNode);
        openMap.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            Node validNode = openMap.get(current.position);
            if (validNode == null || validNode.gCost != current.gCost) {
                continue;
            }
            openMap.remove(current.position);

            if (isAtTarget(current.position, target)) {
                return reconstructPath(current);
            }

            closedSet.add(current.position);

            for (IPosition neighbor : getNeighbors(current.position, circuit, current.velocity)) {
                if (closedSet.contains(neighbor)) continue;

                IVelocity newVelocity = calculateNewVelocity(current.position, neighbor);
                double newGCost = current.gCost + getMoveCost(current.position, neighbor);
                Node neighborNode = openMap.get(neighbor);

                if (neighborNode == null) {
                    neighborNode = new Node(
                            neighbor,
                            current,
                            newGCost,
                            heuristic(neighbor, target),
                            newVelocity);
                    openSet.add(neighborNode);
                    openMap.put(neighbor, neighborNode);
                } else if (newGCost < neighborNode.gCost) {
                    neighborNode.updateCosts(current, newGCost, neighborNode.hCost);
                    neighborNode.velocity = newVelocity;
                }
            }
        }

        return new ArrayList<>(); // Nessun percorso trovato
    }

    /**
     * Overload del metodo findPath per compatibilità all'indietro
     */
    public List<IPosition> findPath(IPosition start, IPosition target, ICircuit circuit) {
        return findPath(start, target, circuit, 
            new Velocity(new it.unicam.cs.mdp2024.formula1game.model.util.Vector(0, 0)));
    }

    private double heuristic(IPosition pos, IPosition target) {
        int dx = Math.abs(pos.getColumn() - target.getColumn());
        int dy = Math.abs(pos.getRow() - target.getRow());
        double directDistance = Math.sqrt(dx * dx + dy * dy);
        
        // Penalizza leggermente i percorsi più lunghi ma favorisce
        // quelli che rispettano le regole di movimento
        return directDistance * 1.2;
    }

    private List<IPosition> getNeighbors(IPosition pos, ICircuit circuit, IVelocity currentVelocity) {
        List<IPosition> neighbors = new ArrayList<>();
        
        // Calcola il punto principale basato sull'ultima velocità
        IPosition mainPoint = calculateMainPoint(pos, currentVelocity);
        
        // Le otto posizioni possibili intorno al punto principale
        int[][] offsets = {
            {-1, 1},  {0, 1},  {1, 1},   // Alto
            {-1, 0},  {0, 0},  {1, 0},   // Centro
            {-1, -1}, {0, -1}, {1, -1}   // Basso
        };
        
        for (int[] offset : offsets) {
            int newX = mainPoint.getColumn() + offset[0];
            int newY = mainPoint.getRow() + offset[1];
            IPosition newPos = new Position(newY, newX);

            if (isValidPosition(newPos, circuit)) {
                // Verifica che il movimento rispetti i limiti di accelerazione
                IVelocity newVelocity = calculateNewVelocity(pos, newPos);
                if (isValidVelocity(newVelocity)) {
                    neighbors.add(newPos);
                }
            }
        }

        return neighbors;
    }

    private IPosition calculateMainPoint(IPosition currentPos, IVelocity currentVelocity) {
        int newX = currentPos.getColumn() + currentVelocity.getCurrentVelocity().getX();
        int newY = currentPos.getRow() + currentVelocity.getCurrentVelocity().getY();
        return new Position(newY, newX);
    }

    private IVelocity calculateNewVelocity(IPosition from, IPosition to) {
        int dx = to.getColumn() - from.getColumn();
        int dy = to.getRow() - from.getRow();
        return new Velocity(new it.unicam.cs.mdp2024.formula1game.model.util.Vector(dx, dy));
    }

    private boolean isValidVelocity(IVelocity velocity) {
        // Verifica che la velocità non superi i limiti del gioco
        int vx = Math.abs(velocity.getCurrentVelocity().getX());
        int vy = Math.abs(velocity.getCurrentVelocity().getY());
        return vx <= 5 && vy <= 5;
    }

    private boolean isValidPosition(IPosition pos, ICircuit circuit) {
        if (!circuit.isValidPosition(pos) || circuit.isWall(pos.getRow(), pos.getColumn())) {
            return false;
        }

        // Verifica extra per evitare posizioni troppo vicine ai muri
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                IPosition adjacent = new Position(pos.getRow() + dy, pos.getColumn() + dx);
                if (circuit.isValidPosition(adjacent) && 
                    circuit.isWall(adjacent.getRow(), adjacent.getColumn())) {
                    return false;
                }
            }
        }

        return true;
    }

    private double getMoveCost(IPosition from, IPosition to) {
        // Costo del movimento considerando anche le diagonali
        boolean isDiagonal = from.getColumn() != to.getColumn() && 
                           from.getRow() != to.getRow();
        return isDiagonal ? Math.sqrt(2) : 1;
    }

    private boolean isAtTarget(IPosition pos, IPosition target) {
        return pos.equals(target);
    }

    private List<IPosition> reconstructPath(Node endNode) {
        List<IPosition> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(0, current.position);
            current = current.parent;
        }

        return path;
    }
}
