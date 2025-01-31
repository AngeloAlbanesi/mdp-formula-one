package it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint;

import java.util.List;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;

/**
 * Interfaccia che definisce il contratto per la ricerca dei checkpoint nel circuito.
 * Implementa il Single Responsibility Principle separando la logica di ricerca dei checkpoint
 * dalla classe Circuit.
 */
public interface ICheckpointFinder {
    /**
     * Trova tutte le linee di checkpoint nel circuito.
     * Una linea di checkpoint è una sequenza di celle checkpoint adiacenti in linea retta
     * (orizzontale o verticale).
     * 
     * @return Lista di linee di checkpoint, dove ogni linea è una lista di posizioni
     */
    List<List<Position>> findCheckpointLines();

    /**
     * Verifica se una coordinata contiene un checkpoint.
     *
     * @param x coordinata x
     * @param y coordinata y
     * @return true se la cella è un checkpoint
     */
    boolean isCheckpoint(int x, int y);
}