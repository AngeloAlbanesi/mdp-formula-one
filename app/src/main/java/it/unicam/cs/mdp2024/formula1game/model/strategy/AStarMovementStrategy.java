package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.algorithms.AStar;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.player.BotPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.*;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;

import java.util.*;

/**
 * Implementazione della strategia di movimento basata sull'algoritmo A*.
 * Ottimizza il percorso considerando velocità, ostacoli e checkpoint.
 */
public class AStarMovementStrategy implements MovementStrategy {
    private final AStar pathFinder;
    private final DefaultMoveValidator moveValidator;
    private MovementWeights weights;
    private final IPlayer dummyPlayer;
    private List<IPosition> currentPath;
    private IPosition currentTarget;
    private static final double OPTIMAL_SPEED = 3.0; // Velocità ottimale bilanciata

    public AStarMovementStrategy() {
        this.pathFinder = new AStar();
        this.moveValidator = new DefaultMoveValidator();
        this.weights = new MovementWeights(0.6, 0.3, 0.4, 0.5); // Pesi bilanciati
        this.dummyPlayer = new BotPlayer("BOT", "black");
        this.currentPath = new ArrayList<>();
    }

    @Override
    public IAcceleration calculateMove(IPosition currentPosition, IVelocity currentVelocity,
            List<IPosition> opponentPositions, ICircuit circuit,
            IPosition nextCheckpoint) {
            
        // Aggiorna il percorso se il target è cambiato
        if (!nextCheckpoint.equals(currentTarget)) {
            currentTarget = nextCheckpoint;
            currentPath = pathFinder.findPath(currentPosition, nextCheckpoint, circuit);
        }

        // Se il percorso è vuoto, calcola una mossa basata sulla posizione attuale
        if (currentPath.isEmpty()) {
            return calculateDirectMove(currentPosition, currentVelocity, opponentPositions, 
                    circuit, nextCheckpoint);
        }

        // Trova il prossimo punto intermedio nel percorso
        IPosition nextWaypoint = findNextWaypoint(currentPosition);
        
        // Calcola tutte le possibili accelerazioni valide
        List<IAcceleration> validAccelerations = getValidAccelerations(currentPosition,
                currentVelocity, opponentPositions, circuit);

        if (validAccelerations.isEmpty()) {
            return new Acceleration(new Vector(0, 0));
        }

        // Valuta ogni accelerazione possibile considerando sia il waypoint che il target finale
        return findBestAcceleration(validAccelerations, currentPosition, currentVelocity,
                opponentPositions, circuit, nextWaypoint, nextCheckpoint);
    }

    private IPosition findNextWaypoint(IPosition currentPosition) {
        // Rimuovi i waypoint già raggiunti
        while (!currentPath.isEmpty() && currentPosition.distanceTo(currentPath.get(0)) < 2.0) {
            currentPath.remove(0);
        }
        
        // Se non ci sono più waypoint, usa il target finale
        return currentPath.isEmpty() ? currentTarget : currentPath.get(0);
    }

    private IAcceleration calculateDirectMove(IPosition currentPosition, IVelocity currentVelocity,
            List<IPosition> opponentPositions, ICircuit circuit, IPosition target) {
        List<IAcceleration> validAccelerations = getValidAccelerations(currentPosition,
                currentVelocity, opponentPositions, circuit);

        if (validAccelerations.isEmpty()) {
            return new Acceleration(new Vector(0, 0));
        }

        return findBestAcceleration(validAccelerations, currentPosition, currentVelocity,
                opponentPositions, circuit, target, target);
    }

    @Override
    public void configureWeights(MovementWeights weights) {
        this.weights = weights;
    }

    private List<IAcceleration> getValidAccelerations(IPosition currentPosition,
            IVelocity currentVelocity, List<IPosition> opponentPositions,
            ICircuit circuit) {
        List<IAcceleration> validMoves = new ArrayList<>();

        // Genera tutte le possibili accelerazioni (-1, 0, 1 per x e y)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                IAcceleration acc = new Acceleration(new Vector(dx, dy));
                IVelocity newVelocity = currentVelocity.addAcceleration(acc);
                IPosition newPosition = currentPosition.nextPosition(newVelocity);

                // Verifica se la mossa è valida considerando gli avversari
                if (moveValidator.isValidMove(dummyPlayer, currentPosition, acc, circuit, 
                        createDummyPlayers(opponentPositions))) {
                    validMoves.add(acc);
                }
            }
        }

        return validMoves;
    }

    private List<IPlayer> createDummyPlayers(List<IPosition> opponentPositions) {
        List<IPlayer> dummyPlayers = new ArrayList<>();
        for (IPosition pos : opponentPositions) {
            BotPlayer dummy = new BotPlayer("OPPONENT", "red");
            dummy.getCar().setPosition(pos);
            dummyPlayers.add(dummy);
        }
        return dummyPlayers;
    }

    private IAcceleration findBestAcceleration(List<IAcceleration> validAccelerations,
            IPosition currentPosition, IVelocity currentVelocity,
            List<IPosition> opponentPositions, ICircuit circuit,
            IPosition waypoint, IPosition finalTarget) {
        
        IAcceleration bestAcceleration = validAccelerations.get(0);
        double bestScore = Double.NEGATIVE_INFINITY;

        for (IAcceleration acc : validAccelerations) {
            IVelocity newVelocity = currentVelocity.addAcceleration(acc);
            IPosition newPosition = currentPosition.nextPosition(newVelocity);

            // Calcola il punteggio considerando sia il waypoint che il target finale
            double waypointScore = evaluateMove(newPosition, newVelocity, 
                    opponentPositions, circuit, waypoint);
            double targetScore = evaluateMove(newPosition, newVelocity, 
                    opponentPositions, circuit, finalTarget);
            
            // Combina i punteggi dando più peso al waypoint
            double score = (waypointScore * 0.7) + (targetScore * 0.3);

            if (score > bestScore) {
                bestScore = score;
                bestAcceleration = acc;
            }
        }

        return bestAcceleration;
    }

    private double evaluateMove(IPosition newPosition, IVelocity newVelocity,
            List<IPosition> opponentPositions, ICircuit circuit,
            IPosition target) {
        double score = 0.0;

        // Valuta efficienza del percorso
        double distanceToTarget = newPosition.distanceTo(target);
        score += weights.getPathEfficiencyWeight() * (1.0 / (distanceToTarget + 1));

        // Valuta controllo della velocità
        double speedScore = evaluateSpeed(newVelocity);
        score += weights.getSpeedControlWeight() * speedScore;

        // Valuta rischio collisioni
        double collisionRisk = evaluateCollisionRisk(newPosition, opponentPositions);
        score += weights.getCollisionAvoidanceWeight() * (1.0 - collisionRisk);

        // Valuta allineamento al target
        double alignmentScore = evaluateTargetAlignment(newPosition, newVelocity, target);
        score += weights.getCheckpointAlignmentWeight() * alignmentScore;

        return score;
    }

    private double evaluateSpeed(IVelocity velocity) {
        // Penalizza velocità troppo alte o troppo basse
        IVector velocityVector = velocity.getCurrentVelocity();
        double speed = Math.sqrt(
                velocityVector.getX() * velocityVector.getX() +
                        velocityVector.getY() * velocityVector.getY());
        
        return 1.0 / (1.0 + Math.abs(speed - OPTIMAL_SPEED));
    }

    private double evaluateCollisionRisk(IPosition position, List<IPosition> opponentPositions) {
        if (opponentPositions.isEmpty()) {
            return 0.0;
        }

        double minDistance = Double.POSITIVE_INFINITY;
        for (IPosition opponent : opponentPositions) {
            double distance = position.distanceTo(opponent);
            minDistance = Math.min(minDistance, distance);
        }

        // Normalizza il rischio di collisione con una funzione esponenziale
        return Math.exp(-minDistance / 3.0);
    }

    private double evaluateTargetAlignment(IPosition position, IVelocity velocity,
            IPosition target) {
        // Calcola l'angolo tra la velocità e la direzione verso il target
        IVector velocityVector = velocity.getCurrentVelocity();
        IVector directionVector = position.vectorTo(target);

        double vx = velocityVector.getX();
        double vy = velocityVector.getY();
        double dx = directionVector.getX();
        double dy = directionVector.getY();

        // Prodotto scalare normalizzato per ottenere il coseno dell'angolo
        double magnitude = Math.sqrt((vx * vx + vy * vy) * (dx * dx + dy * dy));
        if (magnitude < 0.0001) // Evita divisione per zero
            return 0;

        double dotProduct = (vx * dx + vy * dy) / magnitude;

        // Converte in un punteggio da 0 a 1, dove 1 è perfettamente allineato
        return (dotProduct + 1.0) / 2.0;
    }
}