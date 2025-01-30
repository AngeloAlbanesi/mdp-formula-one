package it.unicam.cs.mdp2024.formula1game.model.game;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;

/**
 * Implementation of the Formula 1 game engine.
 * Manages the game flow, enforces rules, and maintains game state.
 */
/**
 * Implementazione del motore di gioco Formula 1.
 * Gestisce il flusso di gioco, applica le regole e mantiene lo stato della
 * partita.
 */
public class Game implements IGame {

    // Costanti di gioco
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 5;
    private static final int MAX_TURNS = 100;
    private static final int REQUIRED_LAPS = 1;
    private static final String PLAYERS_FILE = "players/players.txt";
    private static final int REFERENCE_START_POSITION = 2; // Indice della terza posizione di partenza

    // Limiti di accelerazione
    private static final int MIN_ACCELERATION = -1;
    private static final int MAX_ACCELERATION = 1;

    // Stato del gioco
    private final IPlayerLoader playerLoader;
    private Map<IPlayer, Integer> laps;
    private List<IPlayer> players;

    private ICircuit circuit;
    private int currentPlayerIndex;
    private int turnCount;
    private boolean gameOver;
    private IPlayer winner;

    // Logger per debugging e monitoraggio
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Game.class.getName());

    /**
     * Creates a new game instance.
     */
    /**
     * Creates a new game instance.
     * 
     * @param playerLoader The loader used to initialize players from file
     * @throws IllegalArgumentException if playerLoader is null
     */
    public Game(IPlayerLoader playerLoader) {
        if (playerLoader == null) {
            throw new IllegalArgumentException("PlayerLoader cannot be null");
        }
        this.playerLoader = playerLoader;
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.turnCount = 0;
        this.gameOver = false;
        this.winner = null;
        this.laps = new HashMap<>();
    }

    @Override
    public void initializePlayers() {
        try {
            List<IPlayer> loadedPlayers = playerLoader.loadPlayers(PLAYERS_FILE);

            // Verifica che ci siano almeno 2 giocatori e non più di 5
            if (loadedPlayers.size() < MIN_PLAYERS || loadedPlayers.size() > MAX_PLAYERS) {
                throw new IllegalStateException(
                        String.format("Il numero di giocatori deve essere tra %d e %d",
                                MIN_PLAYERS, MAX_PLAYERS));
            }

            // Pulisci lo stato precedente e inizializza i nuovi giocatori
            this.players.clear();
            this.laps.clear();
            this.players = new ArrayList<>(loadedPlayers);

            // Inizializza il contatore dei giri per ogni giocatore
            for (IPlayer player : this.players) {
                this.laps.put(player, 0);
                player.setActive(true); // Assicura che tutti i giocatori siano attivi all'inizio
            }

        } catch (IOException | InvalidPlayerFormatException e) {
            throw new IllegalStateException("Errore nel caricamento dei giocatori: " + e.getMessage());
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
        this.laps.clear();

        // Get all available starting positions
        List<Position> startPositions = circuit.getStartPositions();
        if (startPositions.size() < players.size()) {
            throw new IllegalStateException("Not enough starting positions for all players");
        }

        // Create cars and assign them to players with starting positions
        for (int i = 0; i < players.size(); i++) {
            IPlayer player = players.get(i);
            IPosition startPosition = startPositions.get(i);
            // Creare Vector con coordinate (0,0) per velocità e accelerazione iniziali
            IVector zeroVector = new Vector(0, 0);
            ICar car = new Car(
                    startPosition, // posizione di partenza dal circuito
                    new Velocity(zeroVector), // velocità iniziale (0,0)
                    new Acceleration(zeroVector) // accelerazione iniziale (0,0)
            );
            player.setCar(car);
            // Inizializza il contatore dei giri per ogni giocatore
            this.laps.put(player, 0);
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
                // Incrementa il contatore dei giri per il giocatore corrente
                int playerLaps = laps.get(currentPlayer) + 1;
                laps.put(currentPlayer, playerLaps);

                // Se il giocatore ha completato il giro richiesto, vince la partita
                if (playerLaps >= REQUIRED_LAPS) {
                    LOGGER.info(() -> String.format("Player %s ha vinto completando %d giri!",
                            currentPlayer.getName(), playerLaps));
                    winner = currentPlayer;
                    gameOver = true;
                    return false;
                }
            }
        }

        moveToNextPlayer();
        return !isGameOver();
    }

    private void moveToNextPlayer() {
        boolean completedRound = false;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            // Incrementa il contatore dei turni solo quando completiamo un round
            if (currentPlayerIndex == 0) {
                turnCount++;
                completedRound = true;

                // Verifica se abbiamo raggiunto il numero massimo di turni
                if (turnCount >= MAX_TURNS) {
                    gameOver = true;
                    winner = determineWinnerByLaps();
                    return;
                }

                checkGameOver();
            }
        } while (!isGameOver() && !getCurrentPlayer().isActive());
    }

    /**
     * Determina il vincitore in base al numero di giri completati quando si
     * raggiunge il limite di turni.
     * In caso di parità nei giri:
     * 1. Vince chi ha completato più giri
     * 2. A parità di giri, vince chi è più avanti sulla pista (usando la distanza
     * di Manhattan dalla terza posizione di partenza)
     * 3. A parità di posizione, vince chi ha raggiunto per primo quella posizione
     */
    private IPlayer determineWinnerByLaps() {
        int maxLaps = -1;

        // Trova il numero massimo di giri tra i giocatori attivi
        for (IPlayer player : players) {
            if (!player.isActive())
                continue;
            int playerLaps = laps.get(player);
            if (playerLaps > maxLaps) {
                maxLaps = playerLaps;
            }
        }

        // Se nessun giocatore attivo ha completato giri
        if (maxLaps == -1)
            return null;

        // Filtra i giocatori attivi che hanno completato il massimo numero di giri
        List<IPlayer> playersAtMaxLaps = new ArrayList<>();
        for (IPlayer player : players) {
            if (player.isActive() && laps.get(player) == maxLaps) {
                playersAtMaxLaps.add(player);
            }
        }

        // Se c'è un solo leader, è il vincitore
        if (playersAtMaxLaps.size() == 1) {
            return playersAtMaxLaps.get(0);
        }

        // In caso di parità di giri, determina chi è più avanti sulla pista
        Map<IPlayer, Integer> distanceMap = new HashMap<>();
        IPosition referencePosition = circuit.getStartPositions().get(REFERENCE_START_POSITION);
        final int minDistance = playersAtMaxLaps.stream()
                .mapToInt(player -> {
                    int distance = player.getCar().getPosition().manhattanDistanceTo(referencePosition);
                    distanceMap.put(player, distance);
                    return distance;
                })
                .min()
                .orElse(Integer.MAX_VALUE);

        // Filtra i giocatori con la distanza minima
        List<IPlayer> closestPlayers = playersAtMaxLaps.stream()
                .filter(p -> distanceMap.get(p) == minDistance)
                .collect(Collectors.toList());

        // Se c'è un solo giocatore alla distanza minima, è il vincitore
        if (closestPlayers.size() == 1) {
            return closestPlayers.get(0);
        }

        // In caso di ulteriore parità, ritorna il primo della lista
        // (che sarà quello che ha raggiunto per primo quella posizione)
        return closestPlayers.get(0);
    }

    private void checkGameOver() {
        // Controlla se tutti i giocatori sono inattivi (si sono schiantati)
        boolean allInactive = true;
        for (IPlayer player : players) {
            if (player.isActive()) {
                allInactive = false;
                break;
            }
        }

        // Se tutti i giocatori sono inattivi, la partita finisce senza vincitore
        if (allInactive) {
            gameOver = true;
            winner = null;
        }

        // La partita può anche finire se qualcuno ha già vinto (completando il giro)
        // ma questo è già gestito nel metodo executeTurn
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

    @Override
    public boolean isValidMove(IPosition position, IAcceleration acceleration) {
        ICar car = getCurrentPlayer().getCar();

        // Verifica che l'accelerazione non superi i limiti consentiti
        if (!isValidAcceleration(acceleration)) {
            return false;
        }

        // Calcola la velocità futura
        IVelocity futureVelocity = car.getVelocity().addAcceleration(acceleration);

        // Calcola la posizione futura
        IPosition futurePosition = position.nextPosition(futureVelocity);

        // Verifica che la posizione futura sia valida (non sia un muro)
        if (!circuit.isValidPosition(futurePosition)) {
            return false;
        }

        // Verifica collisioni con altri giocatori
        for (IPlayer otherPlayer : players) {
            if (otherPlayer != getCurrentPlayer() && otherPlayer.isActive() &&
                    futurePosition.equals(otherPlayer.getCar().getPosition())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica che l'accelerazione rispetti i limiti del gioco.
     * Nel gioco Formula 1, l'accelerazione può essere solo -1, 0, o 1 per ogni
     * componente.
     */
    private boolean isValidAcceleration(IAcceleration acceleration) {
        IVector acc = acceleration.getAccelerationVector();
        return Math.abs(acc.getX()) <= 1 && Math.abs(acc.getY()) <= 1;
    }

    @Override
    public int getTurnCount() {
        return turnCount;
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
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        state.append("Turn: ").append(turnCount).append("\n");
        state.append("Current Player: ").append(getCurrentPlayer().getName()).append("\n");
        state.append("Players State:\n");

        for (IPlayer player : players) {
            state.append("- ").append(player.getName())
                    .append(" (").append(player.isActive() ? "Active" : "Inactive").append(")")
                    .append(" Position: ").append(player.getCar().getPosition())
                    .append(" Laps: ").append(laps.get(player))
                    .append("\n");
        }

        if (isGameOver()) {
            state.append("\nGame Over! ");
            if (winner != null) {
                state.append("Winner: ").append(winner.getName());
            } else {
                state.append("Tutti i giocatori si sono schiantati!");
            }
        }

        return state.toString();
    }
}
