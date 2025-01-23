package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.util.List;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

public interface ICircuit {

    int getWidth(); // Restituisce la larghezza del circuito

    int getHeight(); // Restituisce l'altezza del circuito

    boolean isOncircuit(int x, int y); // Verifica se una coordinata è sulla pista

    boolean isStartingPoint(int x, int y); // Verifica se una coordinata è un punto di partenza

    boolean isFinishLine(int x, int y); // Verifica se una coordinata è una linea di arrivo

    boolean isWall(int x, int y); // Verifica se una coordinata è una parete

    boolean isValid(); // Verifica che il circuito sia valido

    void validate(); // Valida il circuito

    CircuitCell[][] getGrid(); // Restituisce la griglia del circuito

    CircuitCell getCell(int x, int y); // Restituisce la cella alla posizione (x,y)

    void printCircuit(); // Stampa il circuito su console

    /**
     * Returns all starting positions available in the circuit.
     * These are all cells marked with 'S' in the circuit layout.
     *
     * @return list of all starting positions in the circuit
     */
    List<Position> getStartPositions();

    /**
     * Returns all finish line positions in the circuit.
     * These are all cells marked with '*' in the circuit layout.
     *
     * @return list of all finish line positions in the circuit
     */
    List<Position> getFinishPositions();

    /**
     * Checks if a given position is valid in the circuit.
     * A valid position is within bounds and not on a wall.
     *
     * @param position the position to check
     * @return true if the position is valid, false otherwise
     */
    boolean isValidPosition(IPosition position);
}
