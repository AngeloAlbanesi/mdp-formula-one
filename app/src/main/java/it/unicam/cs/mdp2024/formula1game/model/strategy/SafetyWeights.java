package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

/**
 * Questa classe estende MovementWeights aggiungendo pesi specifici per
 * la strategia difensiva di guida.
 */
public class SafetyWeights extends MovementWeights {
    
    private static final double CURVE_PENALTY = 2.0;
    private static final double OPPONENT_PROXIMITY_PENALTY = 3.0;
    private static final double SAFE_DISTANCE = 3.0;
    
    private final IPosition[] opponentPositions;
    
    public SafetyWeights(IPosition[] opponentPositions) {
        super(1.0, 2.0, 3.0, 1.0); // Aumenta i pesi per velocità e collisioni
        this.opponentPositions = opponentPositions;
        
        // Imposta pesi specifici per la guida difensiva
        setSpeedControlWeight(2.0);        // Maggior controllo della velocità
        setCollisionAvoidanceWeight(3.0);  // Massima priorità all'evitamento collisioni
    }
    
    /**
     * Calcola il peso del movimento considerando la sicurezza.
     * 
     * @param from posizione di partenza
     * @param to posizione di arrivo
     * @return peso del movimento
     */
    public double calculateSafetyWeight(IPosition from, IPosition to) {
        double baseWeight = getPathEfficiencyWeight(); // Usa il peso base per l'efficienza
        
        // Penalizza le curve (cambi di direzione)
        if (isTurn(from, to)) {
            baseWeight *= CURVE_PENALTY;
        }
        
        // Penalizza le posizioni vicine agli avversari
        for (IPosition opponent : opponentPositions) {
            if (isNearOpponent(to, opponent)) {
                baseWeight *= OPPONENT_PROXIMITY_PENALTY;
            }
        }
        
        return baseWeight;
    }
    
    private boolean isTurn(IPosition from, IPosition to) {
        // Calcola se c'è un cambio di direzione significativo
        int dRow = to.getRow() - from.getRow();
        int dCol = to.getColumn() - from.getColumn();
        return Math.abs(dRow) > 0 && Math.abs(dCol) > 0;
    }
    
    private boolean isNearOpponent(IPosition pos, IPosition opponent) {
        double distance = Math.sqrt(
            Math.pow(pos.getRow() - opponent.getRow(), 2) +
            Math.pow(pos.getColumn() - opponent.getColumn(), 2)
        );
        return distance < SAFE_DISTANCE;
    }
}