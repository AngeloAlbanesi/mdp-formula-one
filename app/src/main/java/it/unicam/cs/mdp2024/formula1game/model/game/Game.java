package it.unicam.cs.mdp2024.formula1game.model.game;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayerLoader;
import it.unicam.cs.mdp2024.formula1game.model.player.InvalidPlayerFormatException;
import it.unicam.cs.mdp2024.formula1game.model.player.PlayerLoader;
import it.unicam.cs.mdp2024.formula1game.model.car.Car;
import it.unicam.cs.mdp2024.formula1game.model.car.ICar;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.util.Velocity;
import it.unicam.cs.mdp2024.formula1game.model.util.Acceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;
import it.unicam.cs.mdp2024.formula1game.model.util.IVector;

/**
 * Implementation of the Formula 1 game engine.
 * Manages the game flow, enforces rules, and maintains game state.
 */
public class Game implements IGame {

    private List<IPlayer> players;
    private ICircuit circuit;
    private int currentPlayerIndex;
    private int turnCount;
    private boolean gameOver;
    private IPlayer winner;

    /**
     * Creates a new game instance.
     */
    public Game() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.turnCount = 0;
        this.gameOver = false;
        this.winner = null;
    }

    // TODO: inizializzazione giocatori
    @Override
    public void initializePlayers() {
        // TODO Creazione diretta di PlayerLoader
        // Nessun injection delle dipendenze
        try {
            IPlayerLoader loader = new PlayerLoader();
            List<IPlayer> players = loader.loadPlayers("players/players.txt");
            // Inizializza i giocatori nel gioco
        } catch (IOException | InvalidPlayerFormatException e) {
            // Gestione errori
        }
    }

    @Override
    public void initializeGame(List<IPlayer> players, ICircuit circuit) {
        if (players == null || players.isEmpty() || circuit == null) {
            throw new IllegalArgumentException("Players and circuit must not be null or empty");
        }

        this.players = new ArrayList<>(players);
        this.circuit = circuit;
        this.currentPlayerIndex = 0;
        this.turnCount = 0;
        this.gameOver = false;
        this.winner = null;

        // Get all available starting positions
        List<Position> startPositions = circuit.getStartPositions();
        if (startPositions.size() < players.size()) {
            throw new IllegalStateException("Not enough starting positions for all players");
        }

        // Create cars and assign them to players with starting positions
        for (int i = 0; i < players.size(); i++) {
            IPosition startPosition = startPositions.get(i);
            // Creare Vector con coordinate (0,0) per velocità e accelerazione iniziali
            IVector zeroVector = new Vector(0, 0);
            ICar car = new Car(
                    startPosition, // posizione di partenza dal circuito
                    new Velocity(zeroVector), // velocità iniziale (0,0)
                    new Acceleration(zeroVector) // accelerazione iniziale (0,0)
            );
            players.get(i).setCar(car);
        }
    }

    @Override
    public boolean executeTurn() {
        if (isGameOver()) {
            return false;
        }

        IPlayer currentPlayer = getCurrentPlayer();
        if (!currentPlayer.isActive()) {
            moveToNextPlayer();
            return !isGameOver();
        }

        // Get and validate player's move
        IAcceleration acceleration = currentPlayer.chooseAcceleration();
        if (!isValidMove(currentPlayer.getCar().getPosition(), acceleration)) {
            currentPlayer.setActive(false); // Player crashed
            moveToNextPlayer();
            return !isGameOver();
        }

        // Apply move
        currentPlayer.getCar().setAcceleration(acceleration);
        currentPlayer.getCar().move();

        // Check if player reached any finish position
        List<Position> finishPositions = circuit.getFinishPositions();
        for (Position finishPos : finishPositions) {
            if (currentPlayer.getCar().getPosition().equals(finishPos)) {
                currentPlayer.setActive(false);
                winner = currentPlayer;
                gameOver = true;
                return false;
            }
        }

        moveToNextPlayer();
        return !isGameOver();
    }

    // TODO Gestione turni:
    /*
     * Incremento turnCount ad ogni cambio giocatore invece che a fine round
     * Manca controllo del numero massimo di turni
     */
    private void moveToNextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            turnCount++;

            // Check if we've completed a full round and all players are inactive
            if (currentPlayerIndex == 0) {
                checkGameOver();
            }
        } while (!isGameOver() && !getCurrentPlayer().isActive());
    }

    private void checkGameOver() {
        boolean allInactive = true;
        for (IPlayer player : players) {
            if (player.isActive()) {
                allInactive = false;
                break;
            }
        }
        gameOver = allInactive;
    }

    @Override
    public IPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    @Override
    public List<IPlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public ICircuit getCircuit() {
        return circuit;
    }

    // TODO: validazione movimento
    /*
     * Verifica solo la posizione corrente, non quella futura dopo l'accelerazione
     * Manca controllo collisioni tra giocatori
     */
    @Override
    public boolean isValidMove(IPosition position, IAcceleration acceleration) {
        return circuit.isValidPosition(position);
    }

    @Override
    public int getTurnCount() {
        return turnCount;
    }

    // TODO: Formattazione testo semplice potrebbe limitare l'espansione
    // Manca persistenza dello stato
    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public IPlayer getWinner() {
        return winner;
    }

    @Override
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        state.append("Turn: ").append(turnCount).append("\n");
        state.append("Current Player: ").append(getCurrentPlayer().getName()).append("\n");
        state.append("Players State:\n");

        for (IPlayer player : players) {
            state.append("- ").append(player.getName())
                    .append(" (").append(player.isActive() ? "Active" : "Inactive").append(")")
                    .append(" Position: ").append(player.getCar().getPosition())
                    .append("\n");
        }

        return state.toString();
    }
}
