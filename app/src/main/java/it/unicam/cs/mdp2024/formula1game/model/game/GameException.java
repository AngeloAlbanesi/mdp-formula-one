package it.unicam.cs.mdp2024.formula1game.model.game;

/**
 * Eccezioni specifiche per il gioco Formula 1.
 */
public class GameException extends RuntimeException {
    
    public GameException(String message) {
        super(message);
    }
    
    /**
     * Lanciata quando non ci sono giocatori attivi nel gioco.
     */
    public static class NoActivePlayersException extends GameException {
        public NoActivePlayersException() {
            super("Non ci sono giocatori attivi nel gioco");
        }
    }
    
    /**
     * Lanciata quando lo stato del gioco non è valido.
     */
    public static class InvalidGameStateException extends GameException {
        public InvalidGameStateException(String message) {
            super(message);
        }
    }
    
    /**
     * Lanciata quando una mossa porta a una posizione non valida.
     */
    public static class InvalidPositionException extends GameException {
        public InvalidPositionException(String message) {
            super(message);
        }
    }
}