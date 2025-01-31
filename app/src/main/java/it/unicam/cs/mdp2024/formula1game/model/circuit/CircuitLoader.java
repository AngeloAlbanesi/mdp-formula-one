package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.io.IOException;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CheckpointCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.RoadCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;

public class CircuitLoader implements ICircuitLoader {
    private final String[] filePaths;

    public CircuitLoader(String[] filePaths) {
        this.filePaths = filePaths;
    }

    @Override
    public ICircuit loadCircuit(int index) throws IOException {
        if (index < 1 || index > filePaths.length) {
            throw new IllegalArgumentException("Invalid circuit index. Must be 1, 2, or 3.");
        }

        String filePath = filePaths[index - 1];
        char[][] charTrack = CircuitFileReader.readFromFile(filePath);
        CircuitCell[][] cellTrack = convertToCells(charTrack);

        ICircuit circuit = new Circuit(cellTrack);
        circuit.validate();
        return circuit;
    }

    public static CircuitCell[][] convertToCells(char[][] charTrack) {
        int height = charTrack.length;
        int width = charTrack[0].length;
        CircuitCell[][] cellTrack = new CircuitCell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (charTrack[y][x]) {
                    case 'S' -> cellTrack[y][x] = new StartCell(x, y);
                    case '#' -> cellTrack[y][x] = new WallCell(x, y);
                    case '.' -> cellTrack[y][x] = new RoadCell(x, y);
                    case '*' -> cellTrack[y][x] = new FinishCell(x, y);
                    case '@' -> cellTrack[y][x] = new CheckpointCell(x, y);
                    default -> throw new IllegalStateException("Invalid character in track: " + charTrack[y][x]);
                }
            }
        }

        return cellTrack;
    }
}

