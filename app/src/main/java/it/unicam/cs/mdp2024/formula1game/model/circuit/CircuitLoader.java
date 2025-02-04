package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.io.IOException;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CheckpointCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.RoadCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;

public class CircuitLoader implements ICircuitLoader {
    private static final String[] DEFAULT_CIRCUITS = {
        "circuits/circuit1.txt",
        "circuits/circuit2.txt"
    };
    
    private final String[] filePaths;

    public CircuitLoader() {
        this(DEFAULT_CIRCUITS);
    }

    public CircuitLoader(String[] filePaths) {
        if (filePaths == null || filePaths.length == 0) {
            throw new IllegalArgumentException("I percorsi dei circuiti non possono essere vuoti");
        }
        this.filePaths = filePaths;
    }

    @Override
    public ICircuit loadCircuit(int index) throws IOException {
        // L'indice è basato su zero nell'array ma viene mostrato come 1-based all'utente
        if (index < 0 || index >= filePaths.length) {
            throw new IllegalArgumentException("Circuito " + (index + 1) + " non valido");
        }

        String filePath = filePaths[index];
        try {
            char[][] charTrack = CircuitFileReader.readFromFile(filePath);
            CircuitCell[][] cellTrack = convertToCells(charTrack);

            ICircuit circuit = new Circuit(cellTrack);
            circuit.validate();
            return circuit;
        } catch (IOException e) {
            throw new IOException("Impossibile caricare il circuito " + (index + 1) + ": " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IOException("Formato del circuito " + (index + 1) + " non valido: " + e.getMessage());
        }
    }

    public static CircuitCell[][] convertToCells(char[][] charTrack) {
        int height = charTrack.length;
        int width = charTrack[0].length;
        CircuitCell[][] cellTrack = new CircuitCell[height][width];
        int startCellCount = 0;

        System.out.println("Debug: Converting circuit with dimensions " + width + "x" + height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (charTrack[y][x]) {
                    case 'S' -> {
                        cellTrack[y][x] = new StartCell(x, y);
                        startCellCount++;
                        System.out.println("Debug: Found StartCell at position (" + x + "," + y + ")");
                    }
                    case '#' -> cellTrack[y][x] = new WallCell(x, y);
                    case '.' -> cellTrack[y][x] = new RoadCell(x, y);
                    case '*' -> cellTrack[y][x] = new FinishCell(x, y);
                    case '@' -> cellTrack[y][x] = new CheckpointCell(x, y);
                    default -> {
                        System.out.println("Debug: Found invalid character '" + charTrack[y][x] +
                            "' at position (" + x + "," + y + ")");
                        throw new IllegalStateException("Carattere non valido nel circuito: " + charTrack[y][x]);
                    }
                }
            }
        }

        System.out.println("Debug: Found " + startCellCount + " start positions in total");
        return cellTrack;
    }
}
