package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.*;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.BotPlayer;
import java.util.*;

/**
 * Strategia di movimento difensiva basata sull'algoritmo di Dijkstra.
 * Implementa un approccio difensivo che prende in considerazione:
 * - Sicurezza nelle curve
 * - Evitamento degli avversari
 * - Controllo della velocità
 */
public class DefensiveDijkstraMovementStrategy implements MovementStrategy {

    private final DefaultMoveValidator moveValidator;
    private final SpeedController speedController;
    private final Map<IPosition, IPosition> cameFrom;
    private final Map<IPosition, Double> costSoFar;
    private SafetyWeights weights;
    private IPlayer currentPlayer;
    private List<IPlayer> allPlayers;

    public DefensiveDijkstraMovementStrategy(DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.speedController = new SpeedController();
        this.cameFrom = new HashMap<>();
        this.costSoFar = new HashMap<>();
        this.weights = new SafetyWeights(new Position[0]);
        this.allPlayers = new ArrayList<>();
    }

    @Override
    public IAcceleration calculateMove(
            IPosition currentPosition,
            IVelocity currentVelocity,
            List<IPosition> opponentPositions,
            ICircuit circuit,
            IPosition nextCheckpoint) {

        // Aggiorna i pesi di sicurezza con le nuove posizioni degli avversari
        this.weights = new SafetyWeights(opponentPositions.toArray(new Position[0]));

        // Implementa Dijkstra con pesi di sicurezza
        PriorityQueue<IPosition> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(costSoFar::get));

        frontier.add(currentPosition);
        costSoFar.clear();
        cameFrom.clear();
        costSoFar.put(currentPosition, 0.0);
        cameFrom.put(currentPosition, null);

        while (!frontier.isEmpty()) {
            IPosition current = frontier.poll();

            if (current.equals(nextCheckpoint)) {
                break;
            }

            // Esplora tutte le posizioni raggiungibili
            for (IPosition next : getValidNeighbors(current, currentVelocity, circuit)) {
                double newCost = costSoFar.get(current) +
                        weights.calculateSafetyWeight((Position) current, (Position) next);

                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    cameFrom.put(next, current);
                    frontier.add(next);
                }
            }
        }

        // Ricostruisci il percorso
        List<IPosition> path = reconstructPath(currentPosition, nextCheckpoint);
        if (path.size() < 2) {
            return new Acceleration(new Vector(0, 0));
        }

        // Calcola l'accelerazione necessaria per raggiungere la prossima posizione
        IPosition nextPos = path.get(1);
        IVelocity safeVelocity = speedController.calculateSafeVelocity(
                (Position) currentPosition,
                (Position) nextPos,
                currentVelocity);

        // Calcola l'accelerazione come differenza tra la velocità desiderata e quella
        // attuale
        return new Acceleration(
                safeVelocity.getCurrentVelocity().subtract(currentVelocity.getCurrentVelocity()));
    }

    @Override
    public void configureWeights(MovementWeights newWeights) {
        if (newWeights instanceof SafetyWeights) {
            this.weights = (SafetyWeights) newWeights;
        }
    }

    private List<IPosition> getValidNeighbors(IPosition pos, IVelocity velocity, ICircuit circuit) {
        List<IPosition> neighbors = new ArrayList<>();

        // Controlla le 8 direzioni possibili
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0)
                    continue;

                IPosition neighbor = new Position(
                        ((Position) pos).getRow() + dy,
                        ((Position) pos).getColumn() + dx);

                // Verifica che la posizione sia valida nel circuito
                if (circuit.isValidPosition(neighbor) && isValidPosition(pos, neighbor, circuit)) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    private boolean isValidPosition(IPosition current, IPosition next, ICircuit circuit) {
        if (currentPlayer == null) {
            currentPlayer = new BotPlayer("DefensiveDijkstra", "blue"); // Colore di default per test
        }

        // Crea un'accelerazione che porterebbe alla posizione desiderata
        IVector diff = next.vectorTo(current);
        IAcceleration acc = new Acceleration(diff);

        return moveValidator.isValidMove(currentPlayer, current, acc, circuit, allPlayers);
    }

    private List<IPosition> reconstructPath(IPosition start, IPosition target) {
        List<IPosition> path = new ArrayList<>();
        IPosition current = target;

        while (current != null) {
            path.add(0, current);
            current = cameFrom.get(current);
        }

        return path;
    }

    /**
     * Imposta il giocatore corrente e la lista di tutti i giocatori per la
     * validazione delle mosse.
     */
    public void setGameContext(IPlayer player, List<IPlayer> players) {
        this.currentPlayer = player;
        this.allPlayers = new ArrayList<>(players);
    }
}