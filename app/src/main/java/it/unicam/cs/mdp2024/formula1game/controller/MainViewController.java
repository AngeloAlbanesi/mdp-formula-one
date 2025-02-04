package it.unicam.cs.mdp2024.formula1game.controller;

import javafx.animation.PauseTransition; // Import aggiunto

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
        Platform.runLater(this::setupInitialStageSize);
    }

    private void setupInitialStageSize() {
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && newScene.getWindow() != null) {
                Stage stage = (Stage) newScene.getWindow();
                // Imposta dimensioni iniziali ragionevoli per il menu di selezione
                stage.setWidth(400);  // Larghezza minima per il menu
                stage.setHeight(300); // Altezza minima per il menu
                stage.centerOnScreen(); // Centra la finestra sullo schermo
            }
        });
    }

    private void setupCanvasResizing() {
        gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Ascolta la windowProperty della scena
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        
                        // Imposta le dimensioni iniziali dello Stage in base al canvas
                        stage.setWidth(renderer.getCalculatedWidth() + 50); // Aggiungi un margine
                        stage.setHeight(renderer.getCalculatedHeight() + 100); // Aggiungi spazio per i controlli
                        
                        // Gestisci il ridimensionamento
                        stage.widthProperty().addListener((o, oldVal, newVal) -> {
                            renderer.onResize();
                            // Aggiorna la larghezza dello Stage se necessario
                            if (renderer.getCalculatedWidth() + 50 > newVal.doubleValue()) {
                                stage.setWidth(renderer.getCalculatedWidth() + 50);
                            }
                        });
                        
                        stage.heightProperty().addListener((o, oldVal, newVal) -> {
                            renderer.onResize();
                            // Aggiorna l'altezza dello Stage se necessario
                            if (renderer.getCalculatedHeight() + 100 > newVal.doubleValue()) {
                                stage.setHeight(renderer.getCalculatedHeight() + 100);
                            }
                        });
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
            System.out.println("Debug: Inizializzazione del gioco per il circuito " + circuitIndex);
            
            CircuitLoader circuitLoader = new CircuitLoader();
            Circuit circuit = (Circuit) circuitLoader.loadCircuit(circuitIndex);
            if (circuit == null) {
                throw new IOException("Impossibile caricare il circuito. Il file potrebbe essere danneggiato o in un formato non valido.");
            }
            System.out.println("Debug: Circuito caricato con dimensioni " + circuit.getWidth() + "x" + circuit.getHeight());

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
            System.out.println("Debug: Istanza di Game2 creata");

            // Carica i giocatori passando l'istanza del gioco
            List<IPlayer> players = playerLoader.loadPlayers("players/players.txt", gameInstance);
            if (players == null || players.isEmpty()) {
                throw new InvalidPlayerFormatException("Nessun giocatore trovato nel file players.txt", 0);
            }
            System.out.println("Debug: Caricati " + players.size() + " giocatori");

            this.game = gameInstance;

            // Inizializza il gioco prima del rendering
            System.out.println("Debug: Avvio del gioco...");
            game.start();
            System.out.println("Debug: Gioco inizializzato con successo");

            // Verifica che i giocatori siano stati inizializzati correttamente
            List<IPlayer> initializedPlayers = game.getPlayers();
            if (initializedPlayers == null || initializedPlayers.isEmpty()) {
                throw new GameException.InvalidGameStateException("Errore nell'inizializzazione dei giocatori");
            }

            // Configura il renderer con il circuito e i giocatori inizializzati
            System.out.println("Debug: Configurazione del renderer...");
            renderer.setCircuit(circuit);
            renderer.setPlayers(initializedPlayers);

            // Esegui il rendering iniziale
            try {
                System.out.println("Debug: Esecuzione del rendering iniziale...");
                renderer.render();
            } catch (Exception renderException) {
                System.err.println("Errore durante il rendering iniziale: " + renderException.getMessage());
                renderException.printStackTrace();
            }

            // Usa PauseTransition per un rendering ritardato più sicuro
            PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(100));
            pause.setOnFinished(event -> renderer.render());
            pause.play();

        } catch (IOException e) {
            // Gestione dell'eccezione (opzionale, puoi anche rilanciarla o loggarla)
            e.printStackTrace();
        } catch (InvalidPlayerFormatException e) {
            throw new InvalidPlayerFormatException("Errore nel formato dei giocatori: " + e.getMessage(), e.getLineNumber());
        } catch (Exception e) {
            throw new IOException("Errore imprevisto durante l'inizializzazione del gioco: " + e.getMessage());
        }
    }

    @FXML
    private void onStepButtonClicked() {
        System.out.println("Debug: Step button clicked");
        if (game != null && !game.isGameOver()) {
            executeSingleStep();
            renderer.render();
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
            Platform.runLater(() -> {
                game.executeTurn();  // Esegui il turno
                renderer.render();    // Aggiorna la vista
                
                if (game.isGameOver()) {
                    stopSimulation();
                    showGameOver();
                }
            });

            try {
                Thread.sleep(500); // Pausa di mezzo secondo tra i turni
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
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
