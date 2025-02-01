package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.CheckpointManager;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayerLoader;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Game2 implements IGame2 {

    private final ITurnManager turnManager;
    private final IWinningStrategy winningStrategy;
    private final IMoveValidator moveValidator;
    private final ICircuit circuit;
    private final IPlayerLoader playerLoader;
    private final GameConfiguration config;
    private final Map<IPlayer, Integer> laps;
    private final List<IPlayer> players;
    private final Map<IPlayer, Boolean> hasFinished;
    private final CheckpointManager checkpointManager;
    private IPlayer winner;
    private boolean gameOver;

    public Game2(ITurnManager turnManager,
            IWinningStrategy winningStrategy,
            IMoveValidator moveValidator,
            ICircuit circuit,
            IPlayerLoader playerLoader,
            GameConfiguration config) {
        this.turnManager = turnManager;
        this.winningStrategy = winningStrategy;
        this.moveValidator = moveValidator;
        this.circuit = circuit;
        this.playerLoader = playerLoader;
        this.config = config;
        this.laps = new HashMap<>();
        this.players = new ArrayList<>();
        this.hasFinished = new HashMap<>();
        this.checkpointManager = new CheckpointManager(circuit);
        this.gameOver = false;
        this.winner = null;
    }

    public Game2(ITurnManager turnManager,
            IWinningStrategy winningStrategy,
            IMoveValidator moveValidator,
            ICircuit circuit,
            IPlayerLoader playerLoader) {
        this(turnManager, winningStrategy, moveValidator, circuit, playerLoader, new GameConfiguration());
    }

    @Override
    public void start() {
        try {
            // Carica i giocatori e inizializza il gioco
            players.clear();
            players.addAll(playerLoader.loadPlayers(config.getPlayersFilePath()));
            if (players.isEmpty()) {
                throw new GameException.InvalidGameStateException("Nessun giocatore caricato");
            }

            turnManager.setPlayers(players);

            // Inizializza il contatore dei giri e lo stato per ogni giocatore
            players.forEach(player -> {
                laps.put(player, 0);
                hasFinished.put(player, false);
                checkpointManager.initializePlayer(player);

                // Verifica che la posizione iniziale sia valida
                if (!circuit.isValidPosition(player.getCar().getPosition())) {
                    throw new GameException.InvalidPositionException(
                            "Posizione iniziale non valida per il giocatore: " + player.getName());
                }
            });

            // Esegui i turni finché la partita non è finita
            while (!isGameOver()) {
                executeTurn();
            }
        } catch (Exception e) {
            throw new GameException.InvalidGameStateException(
                    "Errore nell'inizializzazione del gioco: " + e.getMessage());
        }
    }

    @Override
    public void executeTurn() {
        if (isGameOver()) {
            return;
        }

        IPlayer currentPlayer = turnManager.getCurrentPlayer();

        // Se il giocatore ha già finito o non è attivo, passa al prossimo
        if (!currentPlayer.isActive() || hasFinished.get(currentPlayer)) {
            turnManager.nextTurn();
            return;
        }

        try {
            // Ottieni la posizione corrente prima del movimento
            IPosition oldPosition = currentPlayer.getCar().getPosition();
            
            // Ottieni e valida l'accelerazione scelta dal giocatore
            IAcceleration acceleration = currentPlayer.chooseAcceleration();

            // Verifica se la mossa è valida usando il validator
            if (!moveValidator.isValidMove(currentPlayer,
                    oldPosition,
                    acceleration,
                    circuit,
                    players)) {
                currentPlayer.setActive(false); // Il giocatore si è schiantato
                turnManager.nextTurn();
                return;
            }

            // Applica la mossa
            currentPlayer.getCar().setAcceleration(acceleration);
            currentPlayer.getCar().move();
            
            // Ottieni la nuova posizione dopo il movimento
            IPosition newPosition = currentPlayer.getCar().getPosition();
            
            // Verifica se il giocatore ha attraversato un checkpoint
            boolean checkpointCrossed = checkpointManager.checkAndUpdateCheckpoints(
                currentPlayer, oldPosition, newPosition);
            
            // Se ha attraversato tutti i checkpoint e raggiunge il traguardo
            if (checkpointManager.hasCompletedAllCheckpoints(currentPlayer) &&
                circuit.getFinishPositions().contains(newPosition)) {
                if (winningStrategy.updateLaps(currentPlayer, laps)) {
                    hasFinished.put(currentPlayer, true);
                }
            }

        } catch (Exception e) {
            // In caso di errore durante il movimento, disattiva il giocatore
            currentPlayer.setActive(false);
        } finally {
            // Passa al prossimo turno
            turnManager.nextTurn();

            // Verifica se la partita è finita
            gameOver = winningStrategy.isGameOver(
                    players, circuit, laps, turnManager.getTurnCount(), config.getMaxTurns());
            if (gameOver) {
                winner = winningStrategy.determineWinner(
                        players, circuit, laps, turnManager.getTurnCount(), config.getMaxTurns());
            }
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public IPlayer getWinner() {
        return winner;
    }

    @Override
    public List<IPlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public ICircuit getCircuit() {
        return circuit;
    }

    @Override
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        state.append("Turn: ").append(turnManager.getTurnCount())
                .append("/").append(config.getMaxTurns()).append("\n");
        state.append("Current Player: ").append(turnManager.getCurrentPlayer().getName()).append("\n");
        state.append("Players State:\n");

        for (IPlayer player : players) {
            state.append("- ").append(player.getName())
                    .append(" (").append(player.isActive() ? hasFinished.get(player) ? "Finished" : "Active"
                            : "Inactive")
                    .append(")")
                    .append(" Position: ").append(player.getCar().getPosition())
                    .append(" Laps: ").append(laps.get(player))
                    .append("/").append(config.getRequiredLaps());
            
            // Aggiungi informazioni sui checkpoint
            IPosition nextCheckpoint = checkpointManager.getNextCheckpoint(player);
            if (nextCheckpoint != null) {
                state.append(" Next Checkpoint: ").append(nextCheckpoint);
            }
            state.append("\n");
        }

        if (isGameOver()) {
            state.append("\nGame Over! ");
            if (winner != null) {
                state.append("Winner: ").append(winner.getName());
                if (turnManager.getTurnCount() >= config.getMaxTurns()) {
                    state.append(" (Per massimo numero di turni)");
                } else {
                    state.append(" (Primo a completare il percorso)");
                }
            } else {
                state.append("Nessun vincitore - Tutti i giocatori si sono schiantati!");
            }
        }

        return state.toString();
    }
}