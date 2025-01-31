package it.unicam.cs.mdp2024.formula1game.model.game;

/**
 * Configurazione per il gioco Formula 1.
 * Contiene tutti i parametri configurabili del gioco.
 */
public class GameConfiguration {
    private final int maxTurns;
    private final int requiredLaps;
    private final int referenceStartPosition;
    private final String playersFilePath;

    /**
     * Costruttore con valori predefiniti.
     */
    public GameConfiguration() {
        this(100, 1, 2, "players/players.txt");
    }

    /**
     * Costruttore con parametri personalizzati.
     * 
     * @param maxTurns               numero massimo di turni prima del termine
     *                               forzato
     * @param requiredLaps           numero di giri necessari per vincere
     * @param referenceStartPosition posizione di partenza di riferimento per i
     *                               pareggi
     * @param playersFilePath        percorso del file contenente i giocatori
     * @throws IllegalArgumentException se i parametri non sono validi
     */
    public GameConfiguration(int maxTurns, int requiredLaps,
            int referenceStartPosition, String playersFilePath) {
        if (maxTurns <= 0)
            throw new IllegalArgumentException("maxTurns deve essere positivo");
        if (requiredLaps <= 0)
            throw new IllegalArgumentException("requiredLaps deve essere positivo");
        if (referenceStartPosition < 0)
            throw new IllegalArgumentException("referenceStartPosition non può essere negativo");
        if (playersFilePath == null || playersFilePath.trim().isEmpty())
            throw new IllegalArgumentException("playersFilePath non può essere null o vuoto");

        this.maxTurns = maxTurns;
        this.requiredLaps = requiredLaps;
        this.referenceStartPosition = referenceStartPosition;
        this.playersFilePath = playersFilePath;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getRequiredLaps() {
        return requiredLaps;
    }

    public int getReferenceStartPosition() {
        return referenceStartPosition;
    }

    public String getPlayersFilePath() {
        return playersFilePath;
    }
}