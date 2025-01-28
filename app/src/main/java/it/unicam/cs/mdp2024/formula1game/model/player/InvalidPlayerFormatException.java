package it.unicam.cs.mdp2024.formula1game.model.player;

public class InvalidPlayerFormatException extends Exception {
    private final int lineNumber;

    public InvalidPlayerFormatException(String message, int lineNumber) {
        super(message + " alla riga " + lineNumber);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}