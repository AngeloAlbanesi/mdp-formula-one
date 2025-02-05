package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;

import java.util.List;

/**
 * Classe che implementa il pattern Context per le strategie di movimento.
 * Gestisce la strategia corrente e coordina le operazioni di movimento.
 */
public class MovementContext {
    private MovementStrategy currentStrategy;
    private final MovementWeights weights;
    private final DefaultMoveValidator moveValidator;
    private IPlayer currentPlayer;
    private List<IPlayer> allPlayers;

    public MovementContext(DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.weights = new MovementWeights();
        // Default a strategia A* (codice 1) con pesi bilanciati
        this.currentStrategy = MovementStrategyFactory.createStrategy(1, moveValidator);
        configureWeights(2.0, 1.0, 0.5, 1.0); // A* priorità al percorso efficiente
        this.currentStrategy.configureWeights(weights);
    }

    public MovementContext(int strategyCode, DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.weights = new MovementWeights();
        this.currentStrategy = MovementStrategyFactory.createStrategy(strategyCode, moveValidator);
        
        // Configura i pesi in base alla strategia
        if (strategyCode == 1) {
            // A* priorità al percorso efficiente
            configureWeights(2.0, 1.0, 0.5, 1.0);
        } else {
            // Dijkstra priorità alla sicurezza
            configureWeights(1.0, 1.5, 2.0, 1.0);
        }
        
        this.currentStrategy.configureWeights(weights);
    }

    public void setGameContext(IPlayer player, List<IPlayer> players) {
        this.currentPlayer = player;
        this.allPlayers = players;
        if (currentStrategy instanceof DefensiveDijkstraMovementStrategy) {
            ((DefensiveDijkstraMovementStrategy) currentStrategy).setGameContext(player, players);
        }
    }

    public void setStrategy(MovementStrategy newStrategy) {
        this.currentStrategy = newStrategy;
        this.currentStrategy.configureWeights(weights);
        if (newStrategy instanceof DefensiveDijkstraMovementStrategy) {
            ((DefensiveDijkstraMovementStrategy) newStrategy).setGameContext(currentPlayer, allPlayers);
        }
    }

    public void activateDefensiveStrategy() {
        MovementStrategy defensiveStrategy = MovementStrategyFactory.createStrategy(2, moveValidator);
        setStrategy(defensiveStrategy);
        
        // Configura i pesi per una guida più difensiva ma mantenendo velocità accettabile
        configureWeights(1.0, 1.5, 2.0, 1.0);
    }

    public IAcceleration calculateNextMove(IPosition currentPosition,
            IVelocity currentVelocity,
            List<IPosition> opponentPositions,
            ICircuit circuit,
            IPosition nextCheckpoint) {
        return currentStrategy.calculateMove(currentPosition,
                currentVelocity,
                opponentPositions,
                circuit,
                nextCheckpoint);
    }

    public void configureWeights(double pathEfficiency, double speedControl,
            double collisionAvoidance, double checkpointAlign) {
        weights.setPathEfficiencyWeight(pathEfficiency);
        weights.setSpeedControlWeight(speedControl);
        weights.setCollisionAvoidanceWeight(collisionAvoidance);
        weights.setCheckpointAlignmentWeight(checkpointAlign);
        currentStrategy.configureWeights(weights);
    }

    public MovementStrategy getCurrentStrategy() {
        return currentStrategy;
    }
}
