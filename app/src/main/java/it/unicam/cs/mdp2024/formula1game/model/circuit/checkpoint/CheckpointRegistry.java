package it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

import java.util.List;
import java.util.ArrayList;

/**
 * Mantiene il registro delle linee di checkpoint nel circuito.
 * Fornisce metodi per accedere e gestire le linee di checkpoint.
 */
public class CheckpointRegistry {
    private final List<List<IPosition>> checkpointLines;
    private final ICheckpointFinder finder;

    /**
     * Crea un nuovo registro dei checkpoint utilizzando il finder specificato.
     *
     * @param finder il finder da utilizzare per trovare i checkpoint
     */
    public CheckpointRegistry(ICheckpointFinder finder) {
        this.finder = finder;
        this.checkpointLines = new ArrayList<>();
    }

    /**
     * Crea un nuovo registro dei checkpoint per il circuito specificato.
     *
     * @param circuit la griglia del circuito
     */
    public CheckpointRegistry(CircuitCell[][] circuit) {
        this(new DefaultCheckpointFinder(circuit));
        findCheckpoints();
    }

    /**
     * Trova e registra tutte le linee di checkpoint nel circuito.
     */
    public void findCheckpoints() {
        checkpointLines.clear();
        checkpointLines.addAll(finder.findCheckpointLines());
    }

    /**
     * Restituisce tutte le linee di checkpoint registrate.
     *
     * @return lista delle linee di checkpoint
     */
    public List<List<IPosition>> getCheckpointLines() {
        return new ArrayList<>(checkpointLines);
    }

    /**
     * Verifica se una posizione è parte di una linea di checkpoint.
     *
     * @param position la posizione da verificare
     * @return true se la posizione è parte di una linea di checkpoint
     */
    public boolean isCheckpoint(IPosition position) {
        return checkpointLines.stream()
                .anyMatch(line -> line.contains(position));
    }

    /**
     * Restituisce il numero di linee di checkpoint registrate.
     *
     * @return numero di linee di checkpoint
     */
    public int getCheckpointCount() {
        return checkpointLines.size();
    }
}