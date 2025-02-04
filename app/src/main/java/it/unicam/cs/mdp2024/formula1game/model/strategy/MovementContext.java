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

    /**
     * Costruttore che inizializza il contesto con una strategia predefinita (A*).
     */
    public MovementContext(DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.weights = new MovementWeights();
        // Default a strategia A* (codice 1)
        this.currentStrategy = MovementStrategyFactory.createStrategy(1, moveValidator);
        this.currentStrategy.configureWeights(weights);
    }

    /**
     * Costruttore che permette di specificare il codice della strategia.
     *
     * @param strategyCode il codice della strategia (1=A*, 2=Dijkstra)
     * @param moveValidator il validatore di mosse
     */
    public MovementContext(int strategyCode, DefaultMoveValidator moveValidator) {
        this.moveValidator = moveValidator;
        this.weights = new MovementWeights();
        this.currentStrategy = MovementStrategyFactory.createStrategy(strategyCode, moveValidator);
        this.currentStrategy.configureWeights(weights);
    }

    /**
     * Imposta il contesto del gioco per la validazione delle mosse.
     */
    public void setGameContext(IPlayer player, List<IPlayer> players) {
        this.currentPlayer = player;
        this.allPlayers = players;
        if (currentStrategy instanceof DefensiveDijkstraMovementStrategy) {
            ((DefensiveDijkstraMovementStrategy) currentStrategy).setGameContext(player, players);
        }
    }

    /**
     * Cambia la strategia di movimento corrente.
     *
     * @param newStrategy La nuova strategia da utilizzare
     */
    public void setStrategy(MovementStrategy newStrategy) {
        this.currentStrategy = newStrategy;
        this.currentStrategy.configureWeights(weights);
        if (newStrategy instanceof DefensiveDijkstraMovementStrategy) {
            ((DefensiveDijkstraMovementStrategy) newStrategy).setGameContext(currentPlayer, allPlayers);
        }
    }

    /**
     * Attiva la strategia difensiva Dijkstra.
     */
    public void activateDefensiveStrategy() {
        // Usa il codice 2 per la strategia Dijkstra difensiva
        MovementStrategy defensiveStrategy = MovementStrategyFactory.createStrategy(2, moveValidator);
        setStrategy(defensiveStrategy);
        
        // Configura i pesi per una guida più difensiva
        configureWeights(1.0, 2.0, 3.0, 1.0);
    }

    /**
     * Calcola la prossima mossa utilizzando la strategia corrente.
     *
     * @param currentPosition   Posizione attuale
     * @param currentVelocity   Velocità attuale
     * @param opponentPositions Lista delle posizioni degli avversari
     * @param circuit          Circuito di gioco
     * @param nextCheckpoint   Prossimo checkpoint da raggiungere
     * @return L'accelerazione calcolata per la prossima mossa
     */
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

    /**
     * Configura i pesi utilizzati per il bilanciamento delle decisioni.
     *
     * @param pathEfficiency     Peso per l'efficienza del percorso
     * @param speedControl       Peso per il controllo della velocità
     * @param collisionAvoidance Peso per l'evitamento delle collisioni
     * @param checkpointAlign    Peso per l'allineamento ai checkpoint
     */
    public void configureWeights(double pathEfficiency, double speedControl,
            double collisionAvoidance, double checkpointAlign) {
        weights.setPathEfficiencyWeight(pathEfficiency);
        weights.setSpeedControlWeight(speedControl);
        weights.setCollisionAvoidanceWeight(collisionAvoidance);
        weights.setCheckpointAlignmentWeight(checkpointAlign);
        currentStrategy.configureWeights(weights);
    }

    /**
     * Ottiene la strategia corrente.
     *
     * @return La strategia di movimento attualmente in uso
     */
    public MovementStrategy getCurrentStrategy() {
        return currentStrategy;
    }
}
