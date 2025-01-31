package it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint;

import java.util.ArrayList;
import java.util.List;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;

/**
 * Classe responsabile della gestione e memorizzazione delle linee di checkpoint.
 * Implementa il pattern Registry per mantenere uno stato centralizzato dei checkpoint
 * e fornisce metodi di accesso ai dati memorizzati.
 */
public class CheckpointRegistry {
    private List<List<Position>> checkpointLines;
    private final ICheckpointFinder checkpointFinder;

    /**
     * Crea un nuovo registro dei checkpoint.
     *
     * @param checkpointFinder il finder utilizzato per cercare i checkpoint
     */
    public CheckpointRegistry(ICheckpointFinder checkpointFinder) {
        this.checkpointFinder = checkpointFinder;
        this.checkpointLines = null; // Inizializzazione lazy
    }

    /**
     * Restituisce tutte le linee di checkpoint trovate nel circuito.
     * Utilizza una cache per evitare di ricalcolare le linee ad ogni chiamata.
     *
     * @return Lista delle linee di checkpoint
     */
    public List<List<Position>> getCheckpointLines() {
        if (checkpointLines == null) {
            checkpointLines = checkpointFinder.findCheckpointLines();
        }
        return new ArrayList<>(checkpointLines);
    }

    /**
     * Invalida la cache delle linee di checkpoint.
     * Da chiamare se il circuito viene modificato.
     */
    public void invalidateCache() {
        this.checkpointLines = null;
    }

    /**
     * Verifica se una coordinata contiene un checkpoint.
     *
     * @param x coordinata x
     * @param y coordinata y
     * @return true se la cella è un checkpoint
     */
    public boolean isCheckpoint(int x, int y) {
        return checkpointFinder.isCheckpoint(x, y);
    }
}