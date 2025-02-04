package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.game.Game2;

/**
 * Factory per creare strategie di movimento.
 * Supporta diversi tipi di strategie come A* e Dijkstra.
 */
public class MovementStrategyFactory {
    
    /**
     * Crea una nuova strategia di movimento basata sul codice fornito.
     *
     * @param strategyCode codice della strategia (1=A*, 2=Dijkstra)
     * @param moveValidator validatore delle mosse
     * @return la strategia di movimento creata
     */
    public static MovementStrategy createStrategy(int strategyCode, DefaultMoveValidator moveValidator) {
        switch (strategyCode) {
            case 1:
                return new AStarMovementStrategy(moveValidator);
            case 2:
                return new DefensiveDijkstraMovementStrategy(moveValidator);
            default:
                throw new IllegalArgumentException("Codice strategia non valido: " + strategyCode);
        }
    }
}
