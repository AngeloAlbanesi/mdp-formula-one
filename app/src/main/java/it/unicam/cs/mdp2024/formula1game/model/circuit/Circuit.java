package it.unicam.cs.mdp2024.formula1game.model.circuit;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;

/**
 * This class represents a circuit in the Formula 1 game.
 * It contains a grid of CircuitCell objects that represent the circuit.
 */
public class Circuit implements ICircuit {

    private final CircuitCell[][] circuit; // Rappresentazione della pista
    // Altezza della pista
    private int width;
    // Larghezza della pista
    private int height;

    /**
     * Constructor for the Circuit class.
     *
     * @param circuit the grid of CircuitCell objects that represent the circuit
     */
    public Circuit(CircuitCell[][] circuit) {
        this.circuit = circuit;
        this.height = circuit.length;
        this.width = circuit[0].length;
    }

    /**
     * Returns the grid of CircuitCell objects that represent the circuit.
     *
     * @return the grid of CircuitCell objects that represent the circuit
     */
    public CircuitCell[][] getGrid() {
        return this.circuit;
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
    public boolean isOncircuit(int x, int y) {
        return circuit[y][x].isTraversable();
    }

    @Override
    public boolean isStartingPoint(int x, int y) {
        return circuit[y][x] instanceof StartCell;
    }

    @Override
    public boolean isFinishLine(int x, int y) {
        return circuit[y][x] instanceof FinishCell;
    }

    @Override
    public boolean isWall(int x, int y) {
        return circuit[y][x] instanceof WallCell;
    }

    @Override
    public void validate() {
        boolean hasStart = false;
        boolean hasFinish = false;

        for (CircuitCell[] row : circuit) {
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
        return circuit[y][x];
    }


    @Override
    /**
     * Prints the circuit to the console.
     */
    public void printCircuit() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                System.out.print(circuit[i][j].getSymbol());
            }
            System.out.println();
        }
    }

}
