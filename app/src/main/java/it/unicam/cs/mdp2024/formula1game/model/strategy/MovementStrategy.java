package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;

import java.util.List;

/**
 * Interfaccia che definisce una strategia di movimento per i bot.
 * Implementa il pattern Strategy per permettere diverse implementazioni
 * di algoritmi di movimento.
 */
public interface MovementStrategy {
    
    /**
     * Calcola la prossima mossa ottimale per il bot.
     *
     * @param currentPosition Posizione attuale del bot
     * @param currentVelocity Velocità attuale del bot
     * @param opponentPositions Lista delle posizioni degli altri giocatori
     * @param circuit Circuito di gioco
     * @param nextCheckpoint Posizione del prossimo checkpoint da raggiungere
     * @return Accelerazione calcolata per la prossima mossa
     */
    IAcceleration calculateMove(
            IPosition currentPosition,
            IVelocity currentVelocity,
            List<IPosition> opponentPositions,
            ICircuit circuit,
            IPosition nextCheckpoint
    );

    /**
     * Configura i pesi utilizzati dalla strategia per bilanciare
     * i diversi fattori nella scelta della mossa.
     *
     * @param weights Mappa dei pesi per i vari fattori
     */
    void configureWeights(MovementWeights weights);
}