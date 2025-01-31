package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CheckpointCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.CheckpointRegistry;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.DefaultCheckpointFinder;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.ICheckpointFinder;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

/**
 * This class represents a circuit in the Formula 1 game.
 * It contains a grid of CircuitCell objects that represent the circuit.
 */
public class Circuit implements ICircuit {
    private final CircuitCell[][] circuit; // Rappresentazione della pista
    private int width; // Larghezza della pista
    private int height; // Altezza della pista
    private final CheckpointRegistry checkpointRegistry; // Gestore dei checkpoint

    /**
     * Constructor for the Circuit class.
     *
     * @param circuit the grid of CircuitCell objects that represent the circuit
     */
    public Circuit(CircuitCell[][] circuit) {
        this.circuit = circuit;
        this.height = circuit.length;
        this.width = circuit[0].length;
        ICheckpointFinder finder = new DefaultCheckpointFinder(circuit);
        this.checkpointRegistry = new CheckpointRegistry(finder);
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
    public boolean isOnCircuit(int x, int y) {
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
    public boolean isCheckpoint(int x, int y) {
        return checkpointRegistry.isCheckpoint(x, y);
    }

    @Override
    public List<List<Position>> getCheckpoints() {
        return checkpointRegistry.getCheckpointLines();
    }

    @Override
    public void validate() {
        boolean hasStart = false;
        boolean hasFinish = false;
        boolean hasCheckpoint = false;

        for (CircuitCell[] row : circuit) {
            for (CircuitCell cell : row) {
                if (cell instanceof StartCell) {
                    hasStart = true;
                } else if (cell instanceof FinishCell) {
                    hasFinish = true;
                } else if (cell instanceof CheckpointCell) {
                    hasCheckpoint = true;
                }
            }
        }

        if (!hasStart) {
            throw new IllegalStateException("Invalid circuit: No starting points ('S') found.");
        }
        if (!hasFinish) {
            throw new IllegalStateException("Invalid circuit: No finish line ('*') found.");
        }
        // Non lanciamo eccezioni per i checkpoint mancanti poiché sono opzionali
    }

    @Override
    public boolean isValid() {
        try {
            validate();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public CircuitCell getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException("Posizione (" + x + ", " + y + ") non valida.");
        }
        return circuit[y][x];
    }

    @Override
    public void printCircuit() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                System.out.print(circuit[i][j].getSymbol());
            }
            System.out.println();
        }
    }

    @Override
    public List<Position> getStartPositions() {
        List<Position> startPositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isStartingPoint(x, y)) {
                    startPositions.add(new Position(x, y));
                }
            }
        }
        return startPositions;
    }

    @Override
    public List<Position> getFinishPositions() {
        List<Position> finishPositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isFinishLine(x, y)) {
                    finishPositions.add(new Position(x, y));
                }
            }
        }
        return finishPositions;
    }

    @Override
    public boolean isValidPosition(IPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 0 && row < height && col >= 0 && col < width && !isWall(col, row);
    }
}
