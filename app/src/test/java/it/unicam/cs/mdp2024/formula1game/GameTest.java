package it.unicam.cs.mdp2024.formula1game;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unicam.cs.mdp2024.formula1game.model.car.Car;
import it.unicam.cs.mdp2024.formula1game.model.circuit.CircuitLoader;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.game.Game;
import it.unicam.cs.mdp2024.formula1game.model.player.HumanPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;
import it.unicam.cs.mdp2024.formula1game.model.util.Velocity;
import it.unicam.cs.mdp2024.formula1game.model.util.Acceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

public class GameTest {
    
    private Game game;
    private ICircuit circuit;
    private CircuitLoader loader;

    @BeforeEach
    public void setup() throws IOException {
        game = new Game();
        String[] filePaths = {
            "src/main/resources/circuits/circuit1.txt",
            "src/main/resources/circuits/circuit2.txt",
            "src/main/resources/circuits/circuit3.txt"
        };
        loader = new CircuitLoader(filePaths);
    }

    private Car createCarAtStartPosition(Position startPosition) {
        Vector zeroVector = new Vector(0, 0);
        Velocity initialVelocity = new Velocity(zeroVector);
        Acceleration initialAcceleration = new Acceleration(zeroVector);
        return new Car(startPosition, initialVelocity, initialAcceleration);
    }

    @Test
    public void testGameInitializationWithMultipleStartPositions() throws IOException {
        // Use circuit1 which has 3 starting positions
        circuit = loader.loadCircuit(1);
        List<Position> startPositions = circuit.getStartPositions();
        
        // Create 3 players
        List<IPlayer> players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            IPlayer player = new HumanPlayer("Player " + (i + 1));
            player.setCar(createCarAtStartPosition(startPositions.get(i)));
            players.add(player);
        }

        // Initialize game
        game.initializeGame(players, circuit);

        // Verify each player is at a different starting position
        for (int i = 0; i < players.size(); i++) {
            IPosition playerPos = players.get(i).getCar().getPosition();
            assertTrue(startPositions.contains(playerPos), 
                "Player " + (i + 1) + " should be at a valid starting position");
        }
    }

    @Test
    public void testTooManyPlayersException() throws IOException {
        circuit = loader.loadCircuit(1);
        List<Position> startPositions = circuit.getStartPositions();
        Position firstStartPosition = startPositions.get(0);
        
        // Create more players than starting positions
        List<IPlayer> players = new ArrayList<>();
        for (int i = 0; i < startPositions.size() + 1; i++) {
            IPlayer player = new HumanPlayer("Player " + (i + 1));
            player.setCar(createCarAtStartPosition(firstStartPosition));
            players.add(player);
        }

        // Verify that initialization throws exception
        assertThrows(IllegalStateException.class, () -> {
            game.initializeGame(players, circuit);
        });
    }

    @Test
    public void testWinnerDetectionAtAnyFinishPosition() throws IOException {
        circuit = loader.loadCircuit(1);
        List<Position> startPositions = circuit.getStartPositions();
        List<Position> finishPositions = circuit.getFinishPositions();
        
        // Create a player at a starting position
        List<IPlayer> players = new ArrayList<>();
        IPlayer player = new HumanPlayer("TestPlayer");
        player.setCar(createCarAtStartPosition(startPositions.get(0)));
        players.add(player);

        // Initialize game
        game.initializeGame(players, circuit);

        // Move player to a finish position
        player.getCar().setPosition(finishPositions.get(0));
        game.executeTurn();

        // Verify game is over and player won
        assertTrue(game.isGameOver(), "Game should be over");
        assertEquals(player, game.getWinner(), "Player should be the winner");
    }

    @Test
    public void testValidPositionChecking() throws IOException {
        circuit = loader.loadCircuit(1);
        List<Position> startPositions = circuit.getStartPositions();
        
        // Create a player
        List<IPlayer> players = new ArrayList<>();
        IPlayer player = new HumanPlayer("TestPlayer");
        player.setCar(createCarAtStartPosition(startPositions.get(0)));
        players.add(player);

        game.initializeGame(players, circuit);

        // Test wall position
        Position wallPosition = new Position(0, 0); // Known wall in circuit1
        assertFalse(circuit.isValidPosition(wallPosition), 
            "Wall position should be invalid");

        // Test valid starting position
        assertTrue(circuit.isValidPosition(startPositions.get(0)), 
            "Starting position should be valid");
    }
}