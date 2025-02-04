package it.unicam.cs.mdp2024.formula1game.model.circuit;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.RoadCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.CheckpointRegistry;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Circuit implements ICircuit {
    private final CircuitCell[][] grid;
    private final int width;
    private final int height;
    private final CheckpointRegistry checkpointRegistry;
    private final List<Position> startPositions;
    private final List<Position> finishPositions;
    private boolean isValid;

    public Circuit(CircuitCell[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            throw new IllegalArgumentException("La griglia non può essere null o vuota");
        }
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
        this.checkpointRegistry = new CheckpointRegistry(grid);
        this.startPositions = findCellPositions(StartCell.class);
        this.finishPositions = findCellPositions(FinishCell.class);
        validate();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public CircuitCell[][] getGrid() {
        CircuitCell[][] copy = new CircuitCell[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, width);
        }
        return copy;
    }

    @Override
    public List<IPosition> getStartPositions() {
        return new ArrayList<>(startPositions.stream().map(pos -> (IPosition) pos).collect(Collectors.toList()));
    }

    @Override
    public List<Position> getFinishPositions() {
        return new ArrayList<>(finishPositions);
    }

    @Override
    public boolean isWall(int x, int y) {
        return isValidCoordinate(x, y) && grid[y][x] instanceof WallCell;
    }

    @Override
    public boolean isCheckpoint(int x, int y) {
        if (!isValidCoordinate(x, y)) return false;
        return checkpointRegistry.isCheckpoint(new Position(x, y));
    }

    @Override
    public List<List<Position>> getCheckpoints() {
        return checkpointRegistry.getCheckpointLines().stream()
            .map(line -> line.stream()
                .map(pos -> new Position(pos.getColumn(), pos.getRow()))
                .collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    @Override
    public boolean isValidPosition(IPosition position) {
        return position != null && isValidCoordinate(position.getColumn(), position.getRow());
    }

    @Override
    public boolean isOnCircuit(int x, int y) {
        return isValidCoordinate(x, y) && grid[y][x] instanceof RoadCell;
    }

    @Override
    public boolean isStartingPoint(int x, int y) {
        return isValidCoordinate(x, y) && grid[y][x] instanceof StartCell;
    }

    @Override
    public boolean isFinishLine(int x, int y) {
        return isValidCoordinate(x, y) && grid[y][x] instanceof FinishCell;
    }

    @Override
    public CircuitCell getCell(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            throw new IllegalArgumentException("Coordinate non valide: (" + x + ", " + y + ")");
        }
        return grid[y][x];
    }

    @Override
    public void validate() {
        try {
            StringBuilder errors = new StringBuilder();
            if (startPositions.isEmpty()) {
                errors.append("- Il circuito deve avere almeno una posizione di partenza (S)\n");
            }
            if (finishPositions.isEmpty()) {
                errors.append("- Il circuito deve avere almeno una posizione di arrivo (*)\n");
            }
            if (checkpointRegistry.getCheckpointCount() == 0) {
                errors.append("- Il circuito deve avere almeno un checkpoint (@)\n");
            }
            
            if (errors.length() > 0) {
                throw new IllegalStateException("Errori di validazione del circuito:\n" + errors.toString());
            }
            isValid = true;
        } catch (Exception e) {
            isValid = false;
            throw e;
        }
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public void printCircuit() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(getCellSymbol(grid[y][x]) + " ");
            }
            System.out.println();
        }
    }

    private String getCellSymbol(CircuitCell cell) {
        if (cell instanceof WallCell) return "#";
        if (cell instanceof StartCell) return "S";
        if (cell instanceof FinishCell) return "*";
        if (cell instanceof RoadCell) return ".";
        return " ";
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private List<Position> findCellPositions(Class<? extends CircuitCell> cellType) {
        List<Position> positions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cellType.isInstance(grid[y][x])) {
                    positions.add(new Position(x, y));
                }
            }
        }
        return positions;
    }
}
