package it.unicam.cs.mdp2024.formula1game.model.algorithms;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;

import java.util.*;

/**
 * Implementazione dell'algoritmo A* per il pathfinding nel circuito.
 * Trova il percorso migliore tra due punti evitando ostacoli.
 */
public class AStar {

    private static class Node implements Comparable<Node> {
        IPosition position;
        Node parent;
        double gCost; // Costo dal punto di partenza
        double hCost; // Euristica (distanza stimata al target)
        double fCost; // Costo totale (g + h)

        Node(IPosition position, Node parent, double gCost, double hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
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
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
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
     * @param start   posizione di partenza
     * @param target  posizione di arrivo
     * @param circuit circuito con informazioni sugli ostacoli
     * @return lista di posizioni che formano il percorso, vuota se non trovato
     */
    public List<IPosition> findPath(IPosition start, IPosition target, ICircuit circuit) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<IPosition, Node> openMap = new HashMap<>();
        Set<IPosition> closedSet = new HashSet<>();

        Node startNode = new Node(start, null, 0, heuristic(start, target));
        openSet.add(startNode);
        openMap.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            openMap.remove(current.position);

            if (isAtTarget(current.position, target)) {
                return reconstructPath(current);
            }

            closedSet.add(current.position);

            // Genera i vicini considerando le 8 direzioni
            for (IPosition neighbor : getNeighbors(current.position, circuit)) {
                if (closedSet.contains(neighbor))
                    continue;

                double newGCost = current.gCost + getMoveCost(current.position, neighbor);
                Node neighborNode = openMap.get(neighbor);

                if (neighborNode == null) {
                    // Nuovo nodo mai visto prima
                    neighborNode = new Node(
                            neighbor,
                            current,
                            newGCost,
                            heuristic(neighbor, target));
                    openSet.add(neighborNode);
                    openMap.put(neighbor, neighborNode);
                } else if (newGCost < neighborNode.gCost) {
                    // Percorso migliore verso un nodo esistente
                    openSet.remove(neighborNode);
                    neighborNode.updateCosts(current, newGCost, neighborNode.hCost);
                    openSet.add(neighborNode);
                }
            }
        }

        return new ArrayList<>(); // Nessun percorso trovato
    }

    private double heuristic(IPosition pos, IPosition target) {
        // Distanza Manhattan modificata per favorire movimenti diagonali
        int dx = Math.abs(pos.getColumn() - target.getColumn());
        int dy = Math.abs(pos.getRow() - target.getRow());
        return Math.max(dx, dy) + (Math.sqrt(2) - 1) * Math.min(dx, dy);
    }

    private List<IPosition> getNeighbors(IPosition pos, ICircuit circuit) {
        List<IPosition> neighbors = new ArrayList<>();
        int[][] directions = {
                { -1, -1 }, { -1, 0 }, { -1, 1 },
                { 0, -1 }, { 0, 1 },
                { 1, -1 }, { 1, 0 }, { 1, 1 }
        };

        for (int[] dir : directions) {
            int newX = pos.getColumn() + dir[0];
            int newY = pos.getRow() + dir[1];
            IPosition newPos = new Position(newX, newY);

            if (isValidMove(pos, newPos, circuit)) {
                neighbors.add(newPos);
            }
        }

        return neighbors;
    }

    private boolean isValidMove(IPosition from, IPosition to, ICircuit circuit) {
        // Verifica posizione finale
        if (!circuit.isValidPosition(to) || circuit.isWall(to.getColumn(), to.getRow())) {
            return false;
        }

        // Verifica celle intermedie per mosse diagonali
        if (from.getColumn() != to.getColumn() && from.getRow() != to.getRow()) {
            IPosition horizontalCheck = new Position(to.getColumn(), from.getRow());
            IPosition verticalCheck = new Position(from.getColumn(), to.getRow());

            return !circuit.isWall(horizontalCheck.getColumn(), horizontalCheck.getRow()) &&
                    !circuit.isWall(verticalCheck.getColumn(), verticalCheck.getRow());
        }

        return true;
    }

    private double getMoveCost(IPosition from, IPosition to) {
        // Costo diagonale = sqrt(2), costo ortogonale = 1
        return from.getColumn() != to.getColumn() &&
                from.getRow() != to.getRow() ? Math.sqrt(2) : 1;
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
