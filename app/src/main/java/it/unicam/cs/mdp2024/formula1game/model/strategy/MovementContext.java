package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;

import java.util.List;

/**
 * Classe che implementa il pattern Context per le strategie di movimento.
 * Gestisce la strategia corrente e coordina le operazioni di movimento.
 */
public class MovementContext {
    private MovementStrategy currentStrategy;
    private final MovementWeights weights;

    /**
     * Costruttore che inizializza il contesto con una strategia predefinita (A*).
     */
    public MovementContext() {
        this.currentStrategy = new AStarMovementStrategy();
        this.weights = new MovementWeights();
        this.currentStrategy.configureWeights(weights);
    }

    /**
     * Costruttore che permette di specificare una strategia iniziale.
     *
     * @param strategy La strategia di movimento da utilizzare
     */
    public MovementContext(MovementStrategy strategy) {
        this.currentStrategy = strategy;
        this.weights = new MovementWeights();
        this.currentStrategy.configureWeights(weights);
    }

    /**
     * Cambia la strategia di movimento corrente.
     *
     * @param newStrategy La nuova strategia da utilizzare
     */
    public void setStrategy(MovementStrategy newStrategy) {
        this.currentStrategy = newStrategy;
        this.currentStrategy.configureWeights(weights);
    }

    /**
     * Calcola la prossima mossa utilizzando la strategia corrente.
     *
     * @param currentPosition   Posizione attuale
     * @param currentVelocity   Velocità attuale
     * @param opponentPositions Lista delle posizioni degli avversari
     * @param circuit           Circuito di gioco
     * @param nextCheckpoint    Prossimo checkpoint da raggiungere
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