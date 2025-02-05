package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.*;

import java.util.*;

/**
 * Implementazione di una strategia di movimento difensiva basata su Dijkstra.
 * Cerca di mantenere una distanza di sicurezza dagli altri veicoli.
 */
public class DefensiveDijkstraMovementStrategy implements MovementStrategy {
    private final DefaultMoveValidator moveValidator;
    private MovementWeights weights;
    private IPlayer currentPlayer;
    private List<IPlayer> allPlayers;
    private static final double SAFETY_DISTANCE = 2.0;
    private static final double OPTIMAL_SPEED = 3.0;
    private int stationaryTurns = 0;
    private int consecutiveInvalidMoves = 0;
    private IPosition lastTarget;
    private List<IPosition> currentPath;

    public DefensiveDijkstraMovementStrategy(DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.weights = new MovementWeights();
        this.currentPath = new ArrayList<>();
    }

    @Override
    public void configureWeights(MovementWeights weights) {
        this.weights = weights;
    }

    public void setGameContext(IPlayer player, List<IPlayer> players) {
        this.currentPlayer = player;
        this.allPlayers = players;
    }

    @Override
    public IAcceleration calculateMove(IPosition currentPosition, IVelocity currentVelocity,
            List<IPosition> opponentPositions, ICircuit circuit, IPosition nextCheckpoint) {
        
        // Aggiorna lo stato di stallo
        if (currentVelocity.getCurrentVelocity().equals(
                new it.unicam.cs.mdp2024.formula1game.model.util.Vector(0, 0))) {
            stationaryTurns++;
        } else {
            stationaryTurns = 0;
        }

        // Ridotta la soglia di stallo da 2 a 1
        boolean needsRecalculation = stationaryTurns > 1 || 
                                   consecutiveInvalidMoves > 3 || 
                                   !nextCheckpoint.equals(lastTarget) ||
                                   isPathInvalid(currentPosition, nextCheckpoint);

        if (needsRecalculation) {
            lastTarget = nextCheckpoint;
            recalculatePath(currentPosition, nextCheckpoint, circuit);
            stationaryTurns = 0;
            consecutiveInvalidMoves = 0;
        }

        // Lista delle mosse valide, escludendo (0,0)
        List<IAcceleration> validMoves = getValidMovesExcludingStall(currentPosition, currentVelocity, circuit);
        
        if (validMoves.isEmpty()) {
            consecutiveInvalidMoves++;
            // Forza un movimento in una direzione qualsiasi evitando (0,0)
            return findEmergencyEscape(currentPosition, currentVelocity, circuit, nextCheckpoint);
        }

        consecutiveInvalidMoves = 0;
        return findBestAcceleration(validMoves, currentPosition, currentVelocity,
                opponentPositions, circuit, nextCheckpoint);
    }

    private List<IAcceleration> getValidMovesExcludingStall(IPosition currentPosition,
            IVelocity currentVelocity, ICircuit circuit) {
        List<IAcceleration> validMoves = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip stall move
                IAcceleration acc = new Acceleration(
                    new it.unicam.cs.mdp2024.formula1game.model.util.Vector(dx, dy));
                if (isValidMove(currentPosition, currentVelocity, acc, circuit)) {
                    validMoves.add(acc);
                }
            }
        }

        return validMoves;
    }

    private boolean isPathInvalid(IPosition currentPosition, IPosition target) {
        if (currentPath.isEmpty()) return true;

        double minDistance = Double.POSITIVE_INFINITY;
        for (IPosition waypoint : currentPath) {
            double distance = currentPosition.distanceTo(waypoint);
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance > 4.0;
    }

    private void recalculatePath(IPosition start, IPosition target, ICircuit circuit) {
        currentPath = new ArrayList<>();
        currentPath.add(target);
    }

    private boolean isValidMove(IPosition currentPosition, IVelocity currentVelocity,
            IAcceleration acceleration, ICircuit circuit) {
        if (currentPlayer == null || allPlayers == null) {
            return false;
        }

        // Non considerare valida l'accelerazione (0,0)
        if (acceleration.getAccelerationVector().equals(
                new it.unicam.cs.mdp2024.formula1game.model.util.Vector(0, 0))) {
            return false;
        }

        if (!moveValidator.isValidMove(currentPlayer, currentPosition,
                acceleration, circuit, allPlayers)) {
            return false;
        }

        IVelocity newVelocity = currentVelocity.addAcceleration(acceleration);
        IVector velocityVector = newVelocity.getCurrentVelocity();
        double speed = Math.sqrt(velocityVector.getX() * velocityVector.getX() +
                velocityVector.getY() * velocityVector.getY());
        
        return speed <= 5.0;
    }

    private IAcceleration findEmergencyEscape(IPosition currentPosition, IVelocity currentVelocity,
            ICircuit circuit, IPosition target) {
        int dx = Integer.compare(target.getRow(), currentPosition.getRow());
        int dy = Integer.compare(target.getColumn(), currentPosition.getColumn());

        // Prova prima le direzioni verso il target con magnitudini diverse
        for (int magnitude = 1; magnitude >= -1; magnitude--) {
            if (magnitude == 0) continue; // Skip zero magnitude
            IAcceleration acc = new Acceleration(
                new it.unicam.cs.mdp2024.formula1game.model.util.Vector(dx * magnitude, dy * magnitude));
            if (isValidMove(currentPosition, currentVelocity, acc, circuit)) {
                return acc;
            }
        }

        // Se non funziona, prova tutte le altre direzioni evitando (0,0)
        int[][] directions = {
            {1,0}, {-1,0}, {0,1}, {0,-1},
            {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };

        for (int[] dir : directions) {
            IAcceleration acc = new Acceleration(
                new it.unicam.cs.mdp2024.formula1game.model.util.Vector(dir[0], dir[1]));
            if (isValidMove(currentPosition, currentVelocity, acc, circuit)) {
                return acc;
            }
        }

        // Se proprio non troviamo alternative, ritorna una mossa casuale ma non (0,0)
        return new Acceleration(
            new it.unicam.cs.mdp2024.formula1game.model.util.Vector(
                (Math.random() < 0.5 ? -1 : 1),
                (Math.random() < 0.5 ? -1 : 1)
            ));
    }

    private IAcceleration findBestAcceleration(List<IAcceleration> validMoves,
            IPosition currentPosition, IVelocity currentVelocity,
            List<IPosition> opponentPositions, ICircuit circuit,
            IPosition target) {
        
        IAcceleration bestMove = validMoves.get(0);
        double bestScore = Double.NEGATIVE_INFINITY;

        for (IAcceleration acc : validMoves) {
            double score = evaluateMove(currentPosition, currentVelocity, acc,
                    opponentPositions, target);

            // Bonus per uscire dallo stallo
            if (stationaryTurns > 0) {
                score *= (1.0 + (0.5 * stationaryTurns)); // Aumentato il bonus
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = acc;
            }
        }

        return bestMove;
    }

    private double evaluateMove(IPosition currentPosition, IVelocity currentVelocity,
            IAcceleration acceleration, List<IPosition> opponentPositions,
            IPosition target) {
        
        IVelocity newVelocity = currentVelocity.addAcceleration(acceleration);
        IPosition newPosition = currentPosition.nextPosition(newVelocity);

        double pathScore = evaluateTargetDistance(newPosition, target);
        double speedScore = evaluateSpeed(newVelocity);
        double safetyScore = evaluateSafetyDistance(newPosition, opponentPositions);
        double alignmentScore = evaluateTargetAlignment(newPosition, newVelocity, target);

        return (weights.getPathEfficiencyWeight() * pathScore) +
               (weights.getSpeedControlWeight() * speedScore) +
               (weights.getCollisionAvoidanceWeight() * safetyScore) +
               (weights.getCheckpointAlignmentWeight() * alignmentScore);
    }

    private double evaluateTargetDistance(IPosition position, IPosition target) {
        return 1.0 / (1.0 + position.distanceTo(target));
    }

    private double evaluateSpeed(IVelocity velocity) {
        double speed = Math.sqrt(velocity.getCurrentVelocity().getX() * velocity.getCurrentVelocity().getX() +
                velocity.getCurrentVelocity().getY() * velocity.getCurrentVelocity().getY());

        if (speed > 5.0) return 0.0;

        if (speed < 1.0) {
            return 0.3 + (speed * 0.4);
        }

        if (speed > OPTIMAL_SPEED) {
            return 1.0 - ((speed - OPTIMAL_SPEED) / (5.0 - OPTIMAL_SPEED));
        }

        return 1.0 - Math.abs(speed - OPTIMAL_SPEED) / OPTIMAL_SPEED;
    }

    private double evaluateSafetyDistance(IPosition position, List<IPosition> opponentPositions) {
        if (opponentPositions.isEmpty()) {
            return 1.0;
        }

        double minDistance = opponentPositions.stream()
                .mapToDouble(pos -> position.distanceTo(pos))
                .min()
                .orElse(Double.POSITIVE_INFINITY);

        return Math.exp(-minDistance / SAFETY_DISTANCE);
    }

    private double evaluateTargetAlignment(IPosition position, IVelocity velocity,
            IPosition target) {
        IVector velocityVector = velocity.getCurrentVelocity();
        IVector directionVector = position.vectorTo(target);

        double vx = velocityVector.getX();
        double vy = velocityVector.getY();
        double dx = directionVector.getX();
        double dy = directionVector.getY();

        double magnitude = Math.sqrt((vx * vx + vy * vy) * (dx * dx + dy * dy));
        if (magnitude < 0.0001) return 0;

        double dotProduct = (vx * dx + vy * dy) / magnitude;
        return (dotProduct + 1.0) / 2.0;
    }
}
