package it.unicam.cs.mdp2024.formula1game.model.circuit;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;

/**
 * This class represents a circuit in the Formula 1 game.
 * It contains a grid of CircuitCell objects that represent the track.
 */
public class Circuit implements ICircuit {

    // TODO: cambiare tutte le occorrenze di track con circuit
    private final CircuitCell[][] track; // Rappresentazione della pista
    // Altezza della pista
    private int width;
    // Larghezza della pista
    private int height;

    /**
     * Constructor for the Circuit class.
     *
     * @param circuit the grid of CircuitCell objects that represent the track
     */
    public Circuit(CircuitCell[][] circuit) {
        this.track = circuit;
        this.height = circuit.length;
        this.width = circuit[0].length;
    }

    /**
     * Returns the grid of CircuitCell objects that represent the track.
     *
     * @return the grid of CircuitCell objects that represent the track
     */
    public CircuitCell[][] getGrid() {
        return this.track;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean isOnTrack(int x, int y) {
        return track[y][x].isTraversable();
    }

    @Override
    public boolean isStartingPoint(int x, int y) {
        return track[y][x] instanceof StartCell;
    }

    @Override
    public boolean isFinishLine(int x, int y) {
        return track[y][x] instanceof FinishCell;
    }

    @Override
    public boolean isWall(int x, int y) {
        return track[y][x] instanceof WallCell;
    }

    @Override
    public void validate() {
        boolean hasStart = false;
        boolean hasFinish = false;

        for (CircuitCell[] row : track) {
            for (CircuitCell cell : row) {
                if (cell instanceof StartCell) {
                    hasStart = true;
                } else if (cell instanceof FinishCell) {
                    hasFinish = true;
                }
            }
        }

        if (!hasStart) {
            throw new IllegalStateException("Invalid circuit: No starting points ('S') found.");
        }
        if (!hasFinish) {
            throw new IllegalStateException("Invalid circuit: No finish line ('*') found.");
        }
    }

    @Override
    public boolean isValid() {
        // A circuit is valid if it passes the validate method
        try {
            validate();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public CircuitCell getCell(int x, int y) {
        return track[y][x];
    }


    @Override
    /**
     * Prints the circuit to the console.
     */
    public void printCircuit() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                System.out.print(track[i][j].getSymbol());
            }
            System.out.println();
        }
    }

}
