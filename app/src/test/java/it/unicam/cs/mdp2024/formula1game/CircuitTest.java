package it.unicam.cs.mdp2024.formula1game;


import it.unicam.cs.mdp2024.formula1game.model.algorithms.AStar;
import it.unicam.cs.mdp2024.formula1game.model.circuit.CircuitLoader;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.FinishCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.RoadCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.StartCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.Circuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.WallCell;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CircuitTest {

    // TODO:Fai 3 test di caricamento dei circuiti, uno per ogni circuito

    // Test caricamento circuito 1
    @Test
    public void testCircuit1Loading() throws IOException {
        String[] filePaths = { "src/main/resources/circuits/circuit1.txt",
                "src/main/resources/circuits/circuit2.txt", "src/main/resources/circuits/circuit3.txt" };
        CircuitLoader loader = new CircuitLoader(filePaths);
        ICircuit circuit = loader.loadCircuit(1);

        // Verifica dimensioni
        assertEquals(22, circuit.getGrid()[0].length);
        assertEquals(10, circuit.getGrid().length);

        // Verifica tipi di celle
        assertTrue(circuit.getGrid()[0][0] instanceof WallCell);
        assertTrue(circuit.getGrid()[1][0] instanceof StartCell);
        assertTrue(circuit.getGrid()[1][1] instanceof RoadCell);
        assertTrue(circuit.getGrid()[8][19] instanceof FinishCell);
    }

    // Test caricamento circuito 2
    @Test
    public void testCircuit2Loading() throws IOException {
        String[] filePaths = { "src/main/resources/circuits/circuit1.txt",
                "src/main/resources/circuits/circuit2.txt", "src/main/resources/circuits/circuit3.txt" };
        CircuitLoader loader = new CircuitLoader(filePaths);
        ICircuit circuit = loader.loadCircuit(2);

        // Verifica dimensioni
        assertEquals(62, circuit.getGrid()[0].length);
        assertEquals(10, circuit.getGrid().length);

        // Verifica tipi di celle
        assertTrue(circuit.getGrid()[0][0] instanceof WallCell);
        assertTrue(circuit.getGrid()[2][30] instanceof StartCell);
        assertTrue(circuit.getGrid()[3][30] instanceof StartCell);
        assertTrue(circuit.getGrid()[2][31] instanceof RoadCell);
        assertTrue(circuit.getGrid()[2][28] instanceof FinishCell);
        assertTrue(circuit.getGrid()[3][28] instanceof FinishCell);
    }

    // Test caricamento circuito 3
    @Test
    public void testCircuit3Loading() throws IOException {
        String[] filePaths = { "src/main/resources/circuits/circuit1.txt",
                "src/main/resources/circuits/circuit2.txt", "src/main/resources/circuits/circuit3.txt" };
        CircuitLoader loader = new CircuitLoader(filePaths);
        ICircuit circuit = loader.loadCircuit(3);

        // Verifica dimensioni
        assertEquals(86, circuit.getGrid()[0].length);
        assertEquals(65, circuit.getGrid().length);

        // Verifica tipi di celle
        assertTrue(circuit.isWall(0, 0));
        assertTrue(circuit.isStartingPoint(1, 27));
        assertTrue(circuit.getGrid()[1][42] instanceof RoadCell);
        assertTrue(circuit.getGrid()[43][2] instanceof FinishCell);
    }

    @Test
    public void printCell() throws IOException {
        String[] filePaths = { "src/main/resources/circuits/circuit1.txt",
                "src/main/resources/circuits/circuit2.txt", "src/main/resources/circuits/circuit3.txt" };
        CircuitLoader loader = new CircuitLoader(filePaths);
        ICircuit circuit = loader.loadCircuit(3);
        System.out.println(circuit.getCell(27, 1).getSymbol());
        // System.out.println("Cella trovata");
    }

    @Test
    public void printCircuit() throws IOException {
        String[] filePaths = { "src/main/resources/circuits/circuit1.txt",
                "src/main/resources/circuits/circuit2.txt", "src/main/resources/circuits/circuit3.txt",
                "src/main/resources/circuits/circuit4.txt" };
        CircuitLoader loader = new CircuitLoader(filePaths);
        ICircuit circuit = loader.loadCircuit(1);
        circuit.printCircuit();
    }

    @Test
    public void testCircuitValidation() throws IOException {
        String[] filePaths = { "src/main/resources/circuits/circuit1.txt",
                "src/main/resources/circuits/circuit2.txt", "src/main/resources/circuits/circuit3.txt" };
        CircuitLoader loader = new CircuitLoader(filePaths);
        ICircuit circuit = loader.loadCircuit(3);

        // Verifica numero di celle di partenza
        int startCells = 0;
        for (int i = 0; i < circuit.getGrid().length; i++) {
            for (int j = 0; j < circuit.getGrid()[0].length; j++) {
                if (circuit.getGrid()[i][j] instanceof StartCell) {
                    startCells++;
                }
            }
        }
        assertEquals(7, startCells);

        // Verifica numero di celle di arrivo
        int finishCells = 0;
        for (int i = 0; i < circuit.getGrid().length; i++) {
            for (int j = 0; j < circuit.getGrid()[0].length; j++) {
                if (circuit.getGrid()[i][j] instanceof FinishCell) {
                    finishCells++;
                }
            }
        }
        assertEquals(7, finishCells);

        // Verifica esistenza di un percorso tra una cella di partenza e una di arrivo
        StartCell startCell = null;
        FinishCell finishCell = null;
        outerLoop: for (int i = 0; i < circuit.getGrid().length; i++) {
            for (int j = 0; j < circuit.getGrid()[0].length; j++) {
                if (circuit.getGrid()[i][j] instanceof StartCell && startCell == null) {
                    startCell = (StartCell) circuit.getGrid()[i][j];
                }
                if (circuit.getGrid()[i][j] instanceof FinishCell && finishCell == null) {
                    finishCell = (FinishCell) circuit.getGrid()[i][j];
                }
                if (startCell != null && finishCell != null) {
                    break outerLoop;
                }
            }
        }
        assertNotNull(startCell);
        assertNotNull(finishCell);

    }
}
