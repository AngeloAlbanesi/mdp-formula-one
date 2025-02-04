package it.unicam.cs.mdp2024.formula1game.controller;

import it.unicam.cs.mdp2024.formula1game.model.circuit.Circuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.CircuitLoader;
import it.unicam.cs.mdp2024.formula1game.model.game.GameConfiguration;
import it.unicam.cs.mdp2024.formula1game.model.game.Game2;
import it.unicam.cs.mdp2024.formula1game.model.game.IGame2;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultTurnManager;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultWinningStrategy;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.PlayerLoader;
import it.unicam.cs.mdp2024.formula1game.model.player.InvalidPlayerFormatException;
import it.unicam.cs.mdp2024.formula1game.model.car.Car;
import it.unicam.cs.mdp2024.formula1game.model.util.Acceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;
import it.unicam.cs.mdp2024.formula1game.model.util.Velocity;
import it.unicam.cs.mdp2024.formula1game.view.CircuitRenderer;
import it.unicam.cs.mdp2024.formula1game.model.game.GameException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.util.List;

public class MainViewController {
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Button stepButton;
    @FXML
    private Button runButton;
    @FXML
    private BorderPane gamePane;
    @FXML
    private VBox menuPane;

    private CircuitRenderer renderer;
    private IGame2 game;
    private boolean isSimulationRunning = false;
    @FXML
    public void initialize() {
        renderer = new CircuitRenderer(gameCanvas);
        setupCanvasResizing();
    }

    private void setupCanvasResizing() {
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Ascolta la windowProperty della scena
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        BorderPane parent = (BorderPane) gameCanvas.getParent();

                        stage.widthProperty().addListener((o, oldVal, newVal) -> renderer.onResize());
                        stage.heightProperty().addListener((o, oldVal, newVal) -> renderer.onResize());
                    }
                });
            }
        });
    }

    @FXML
    private void onCircuit1Selected() {
        startGameWithCircuit(0);
    }

    @FXML
    private void onCircuit2Selected() {
        startGameWithCircuit(1);
    }

    @FXML
    private void onBackToMenuClicked() {
        if (isSimulationRunning) {
            stopSimulation();
        }
        game = null;
        menuPane.setVisible(true);
        gamePane.setVisible(false);
    }

    private void startGameWithCircuit(int circuitIndex) {
        try {
            setupGame(circuitIndex);
            menuPane.setVisible(false);
            gamePane.setVisible(true);
            stepButton.setDisable(false);
            runButton.setDisable(false);
        } catch (Exception e) {
            showError("Errore", "Impossibile avviare il gioco", e.getMessage());
        }
    }

    private void setupGame(int circuitIndex) throws IOException, InvalidPlayerFormatException {
        try {
            CircuitLoader circuitLoader = new CircuitLoader();
            Circuit circuit = (Circuit) circuitLoader.loadCircuit(circuitIndex);
            if (circuit == null) {
                throw new IOException("Impossibile caricare il circuito. Il file potrebbe essere danneggiato o in un formato non valido.");
            }

            // Crea una configurazione di gioco con parametri di default
            GameConfiguration config = new GameConfiguration();
            PlayerLoader playerLoader = new PlayerLoader();

            // Crea un'istanza di Game2 con tutte le sue dipendenze
            Game2 gameInstance = new Game2(
                    new DefaultTurnManager(),
                    new DefaultWinningStrategy(),
                    new DefaultMoveValidator(),
                    circuit,
                    playerLoader,
                    config);

            // Carica i giocatori passando l'istanza del gioco
            List<IPlayer> players = playerLoader.loadPlayers("players/players.txt", gameInstance);
            if (players == null || players.isEmpty()) {
                throw new InvalidPlayerFormatException("Nessun giocatore trovato nel file players.txt", 0);
            }

            this.game = gameInstance;

            // Passa il circuito e i giocatori al renderer
            renderer.setCircuit(circuit);
            renderer.setPlayers(game.getPlayers());
            renderer.render();

            // Inizializza il gioco
            game.start();
        } catch (IOException e) {
            throw new IOException("Errore nel caricamento dei file: " + e.getMessage());
        } catch (InvalidPlayerFormatException e) {
            throw new InvalidPlayerFormatException("Errore nel formato dei giocatori: " + e.getMessage(), e.getLineNumber());
        } catch (Exception e) {
            throw new IOException("Errore imprevisto durante l'inizializzazione del gioco: " + e.getMessage());
        }
    }

    @FXML
    private void onStepButtonClicked() {
        if (game != null && !game.isGameOver()) {
            executeSingleStep();
        }
    }

    @FXML
    private void onRunButtonClicked() {
        if (game == null || game.isGameOver())
            return;

        if (!isSimulationRunning) {
            startSimulation();
        } else {
            stopSimulation();
        }
    }

    private void executeSingleStep() {
        game.executeTurn();
        updateView();

        if (game.isGameOver()) {
            disableButtons();
            showGameOver();
        }
    }

    private void startSimulation() {
        isSimulationRunning = true;
        runButton.setText("Ferma Simulazione");
        stepButton.setDisable(true);

        Thread simulationThread = new Thread(() -> {
            while (isSimulationRunning && !game.isGameOver()) {
                Platform.runLater(this::executeSingleStep);

                try {
                    Thread.sleep(500); // Pausa di mezzo secondo tra i turni
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            Platform.runLater(() -> {
                isSimulationRunning = false;
                runButton.setText("Esegui Fino alla Fine");
                stepButton.setDisable(false);

                if (game.isGameOver()) {
                    disableButtons();
                }
            });
        });

        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    private void stopSimulation() {
        isSimulationRunning = false;
        runButton.setText("Esegui Fino alla Fine");
        stepButton.setDisable(false);
    }

    private void updateView() {
        renderer.render();
    }

    private void disableButtons() {
        stepButton.setDisable(true);
        runButton.setDisable(true);
        if (game != null && game.isGameOver()) {
            showGameOver();
        }
    }

    private void showGameOver() {
        IPlayer winner = game.getWinner();
        String message = winner != null ? "Il vincitore è: " + winner.getName() : "La partita è terminata in parità";

        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Gioco Terminato");
            alert.setHeaderText("Fine della Partita");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showError(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}
