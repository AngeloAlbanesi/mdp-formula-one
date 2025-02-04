package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.*;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;

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
    private static final double SAFETY_DISTANCE = 3.0;
    private static final double OPTIMAL_SPEED = 2.0;

    public DefensiveDijkstraMovementStrategy(DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.weights = new MovementWeights();
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
        List<IAcceleration> validMoves = getValidMoves(currentPosition, currentVelocity, circuit);
        if (validMoves.isEmpty()) {
            return new Acceleration(new it.unicam.cs.mdp2024.formula1game.model.util.Vector(0, 0));
        }

        return findBestAcceleration(validMoves, currentPosition, currentVelocity,
                opponentPositions, circuit, nextCheckpoint);
    }

    private List<IAcceleration> getValidMoves(IPosition currentPosition,
            IVelocity currentVelocity, ICircuit circuit) {
        List<IAcceleration> validMoves = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                IAcceleration acc = new Acceleration(new it.unicam.cs.mdp2024.formula1game.model.util.Vector(dx, dy));
                if (isValidMove(currentPosition, currentVelocity, acc, circuit)) {
                    validMoves.add(acc);
                }
            }
        }

        return validMoves;
    }

    private boolean isValidMove(IPosition currentPosition, IVelocity currentVelocity,
            IAcceleration acceleration, ICircuit circuit) {
        if (currentPlayer == null || allPlayers == null) {
            return false;
        }

        return moveValidator.isValidMove(currentPlayer, currentPosition,
                acceleration, circuit, allPlayers);
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

        double score = 0.0;

        // Valuta distanza dal target
        score += weights.getPathEfficiencyWeight() *
                evaluateTargetDistance(newPosition, target);

        // Valuta velocità
        score += weights.getSpeedControlWeight() *
                evaluateSpeed(newVelocity);

        // Valuta distanza dagli avversari
        score += weights.getCollisionAvoidanceWeight() *
                evaluateSafetyDistance(newPosition, opponentPositions);

        // Valuta allineamento con il target
        score += weights.getCheckpointAlignmentWeight() *
                evaluateTargetAlignment(newPosition, newVelocity, target);

        return score;
    }

    private double evaluateTargetDistance(IPosition position, IPosition target) {
        double distance = position.distanceTo(target);
        return 1.0 / (1.0 + distance);
    }

    private double evaluateSpeed(IVelocity velocity) {
        double speed = Math.sqrt(velocity.getCurrentVelocity().getX() * velocity.getCurrentVelocity().getX() +
                velocity.getCurrentVelocity().getY() * velocity.getCurrentVelocity().getY());
        return 1.0 / (1.0 + Math.abs(speed - OPTIMAL_SPEED));
    }

    private double evaluateSafetyDistance(IPosition position, List<IPosition> opponentPositions) {
        if (opponentPositions.isEmpty()) {
            return 1.0;
        }

        double minDistance = opponentPositions.stream()
                .mapToDouble(pos -> position.distanceTo(pos))
                .min()
                .orElse(Double.POSITIVE_INFINITY);

        return Math.min(1.0, minDistance / SAFETY_DISTANCE);
    }

    private double evaluateTargetAlignment(IPosition position, IVelocity velocity,
            IPosition target) {
        IVector velocityVector = velocity.getCurrentVelocity();
        IVector directionVector = position.vectorTo(target);

        double dotProduct = velocityVector.getX() * directionVector.getX() +
                velocityVector.getY() * directionVector.getY();

        double magnitudeProduct = Math.sqrt(
                (velocityVector.getX() * velocityVector.getX() +
                        velocityVector.getY() * velocityVector.getY()) *
                (directionVector.getX() * directionVector.getX() +
                        directionVector.getY() * directionVector.getY()));

        if (magnitudeProduct < 0.0001) {
            return 0.0;
        }

        double cosAngle = dotProduct / magnitudeProduct;
        return (cosAngle + 1.0) / 2.0;
    }
}