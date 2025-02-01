package it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CheckpointCell;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;

/**
 * Implementazione predefinita dell'interfaccia ICheckpointFinder.
 * Si occupa di trovare le linee di checkpoint nel circuito utilizzando
 * un algoritmo di ricerca lineare (orizzontale e verticale).
 */
public class DefaultCheckpointFinder implements ICheckpointFinder {
    private final CircuitCell[][] circuit;
    private final int width;
    private final int height;

    /**
     * Crea un nuovo finder di checkpoint per il circuito specificato.
     *
     * @param circuit la griglia del circuito
     */
    public DefaultCheckpointFinder(CircuitCell[][] circuit) {
        this.circuit = circuit;
        this.height = circuit.length;
        this.width = circuit[0].length;
    }

    @Override
    public List<List<IPosition>> findCheckpointLines() {
        List<List<IPosition>> result = new ArrayList<>();
        Set<IPosition> visited = new HashSet<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isCheckpoint(x, y)) {
                    IPosition pos = new Position(x, y);
                    if (!visited.contains(pos)) {
                        List<IPosition> line = new ArrayList<>();
                        findStraightLine(pos, visited, line);
                        if (line.size() >= 2) { // Includi solo linee con almeno 2 checkpoint
                            result.add(line);
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean isCheckpoint(int x, int y) {
        return circuit[y][x] instanceof CheckpointCell;
    }

    /**
     * Trova tutte le linee rette di checkpoint (orizzontali o verticali) a partire
     * da una posizione.
     */
    private void findStraightLine(IPosition start, Set<IPosition> visited, List<IPosition> line) {
        if (visited.contains(start)) {
            return;
        }

        List<IPosition> horizontal = new ArrayList<>();
        collectInDirection(start, -1, 0, horizontal, visited); // Sinistra
        Collections.reverse(horizontal); // Ordina da sinistra a destra
        horizontal.add(start);
        collectInDirection(start, 1, 0, horizontal, visited); // Destra

        List<IPosition> vertical = new ArrayList<>();
        collectInDirection(start, 0, -1, vertical, visited); // Su
        Collections.reverse(vertical); // Ordina da sopra a sotto
        vertical.add(start);
        collectInDirection(start, 0, 1, vertical, visited); // Giù

        List<IPosition> chosenLine = horizontal.size() >= vertical.size() ? horizontal : vertical;
        if (chosenLine.size() > 1) {
            line.addAll(chosenLine);
            visited.addAll(chosenLine);
        } else {
            line.add(start);
            visited.add(start);
        }
    }

    /**
     * Raccogli checkpoint consecutivi in una direzione specificata (dx, dy).
     */
    private void collectInDirection(IPosition start, int dx, int dy, List<IPosition> line, Set<IPosition> visited) {
        int x = start.getColumn() + dx;
        int y = start.getRow() + dy;
        while (x >= 0 && x < width && y >= 0 && y < height) {
            IPosition pos = new Position(x, y);
            if (isCheckpoint(x, y) && !visited.contains(pos)) {
                line.add(pos);
                x += dx;
                y += dy;
            } else {
                break;
            }
        }
    }
}