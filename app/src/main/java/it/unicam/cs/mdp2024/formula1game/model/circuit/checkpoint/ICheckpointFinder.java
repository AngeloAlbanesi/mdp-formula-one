package it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint;

import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import java.util.List;

/**
 * Interfaccia per la ricerca delle linee di checkpoint nel circuito.
 */
public interface ICheckpointFinder {
    /**
     * Trova tutte le linee di checkpoint nel circuito.
     * Una linea di checkpoint è una sequenza di celle checkpoint adiacenti.
     * 
     * @return Lista di liste di posizioni, dove ogni lista interna
     *         rappresenta una linea di checkpoint
     */
    List<List<IPosition>> findCheckpointLines();

    /**
     * Verifica se una posizione contiene un checkpoint.
     * 
     * @param x coordinata x
     * @param y coordinata y
     * @return true se la posizione contiene un checkpoint
     */
    boolean isCheckpoint(int x, int y);
}