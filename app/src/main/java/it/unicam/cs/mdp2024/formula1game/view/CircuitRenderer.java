package it.unicam.cs.mdp2024.formula1game.view;

import it.unicam.cs.mdp2024.formula1game.model.circuit.Circuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.*;
import it.unicam.cs.mdp2024.formula1game.model.player.BotPlayer;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class CircuitRenderer {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private double cellSize;
    private Circuit circuit;
    private List<IPlayer> players;
    private double calculatedWidth;
    private double calculatedHeight;

    // Viewport per il rendering parziale
    private double viewportX = 0;
    private double viewportY = 0;
    private double viewportWidth;
    private double viewportHeight;

    // Colori definiti per ogni tipo di cella
    private static final Color ROAD_COLOR = Color.web("#F0F0F0");
    private static final Color WALL_COLOR = Color.web("#000000");
    private static final Color START_COLOR = Color.web("#00FF00");
    private static final Color FINISH_COLOR = Color.web("#FF0000");
    private static final Color CHECKPOINT_COLOR = Color.web("#FFFF00");
    private static final Color GRID_LINE_COLOR = Color.web("#808080");

    public CircuitRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
        // Inizializza la viewport con le dimensioni del canvas
        viewportWidth = canvas.getWidth();
        viewportHeight = canvas.getHeight();
        // Reset della posizione della viewport
        viewportX = 0;
        viewportY = 0;
        calculateCellSize();
        setupScrolling();
    }

    public void setPlayers(List<IPlayer> players) {
        if (players == null) {
            System.out.println("Debug: setPlayers - players list is null");
            this.players = null;
            return;
        }

        // Verifica che tutti i giocatori siano correttamente inizializzati
        boolean allPlayersValid = players.stream().allMatch(player -> player != null &&
                player.getCar() != null &&
                player.getCar().getPosition() != null);

        if (!allPlayersValid) {
            System.out.println("Debug: setPlayers - alcuni giocatori non sono inizializzati correttamente");
            return;
        }

        // Mantieni il riferimento alla lista originale
        this.players = players;
        System.out.println("Debug: setPlayers - Inizializzati " + players.size() + " giocatori");

        // Log dettagliato per ogni giocatore
        for (IPlayer player : players) {
            System.out.println("Debug: Player " + player.getName() +
                    " - Position: " + player.getCar().getPosition() +
                    " - Color: " + player.getColor());
        }

        // Forza un aggiornamento del rendering
        render();
    }

    private static final double MAX_CELL_SIZE = 40.0;
    private static final double MIN_CELL_SIZE = 10.0;
    private static final double MAX_TEXTURE_SIZE = 16384.0; // Limite massimo di texture JavaFX

    private void calculateCellSize() {
        if (circuit == null)
            return;

        int gridWidth = circuit.getWidth();
        int gridHeight = circuit.getHeight();

        // Prima calcola le dimensioni massime consentite per il canvas
        double maxAllowedWidth = Math.min(1920.0, MAX_TEXTURE_SIZE);
        double maxAllowedHeight = Math.min(1080.0, MAX_TEXTURE_SIZE);

        // Calcola la dimensione massima della cella che rispetta i limiti di texture
        double maxCellSizeForWidth = maxAllowedWidth / gridWidth;
        double maxCellSizeForHeight = maxAllowedHeight / gridHeight;

        // Usa il minimo tra tutte le limitazioni
        cellSize = Math.min(
                MAX_CELL_SIZE,
                Math.min(
                        Math.min(maxCellSizeForWidth, maxCellSizeForHeight),
                        Math.max(MIN_CELL_SIZE, Math.min(maxCellSizeForWidth, maxCellSizeForHeight))));

        // Calcola le dimensioni finali del canvas
        calculatedWidth = Math.min(gridWidth * cellSize, maxAllowedWidth);
        calculatedHeight = Math.min(gridHeight * cellSize, maxAllowedHeight);

        canvas.setWidth(calculatedWidth);
        canvas.setHeight(calculatedHeight);

        // Aggiorna le dimensioni della viewport
        viewportWidth = calculatedWidth;
        viewportHeight = calculatedHeight;
    }

    public void render() {
        if (circuit == null)
            return;

        // Pulisci il canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Disegna la griglia
        drawGrid();

        // Disegna le auto
        if (players != null) {
            drawCars();
        }
    }

    private void drawGrid() {
        // Calcola gli indici delle celle visibili nella viewport
        int startX = Math.max(0, (int) (viewportX / cellSize));
        int startY = Math.max(0, (int) (viewportY / cellSize));
        int endX = Math.min(circuit.getWidth(), (int) ((viewportX + canvas.getWidth()) / cellSize) + 1);
        int endY = Math.min(circuit.getHeight(), (int) ((viewportY + canvas.getHeight()) / cellSize) + 1);

        // Disegna solo le celle visibili
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                CircuitCell cell = circuit.getCell(x, y);
                drawCell(x, y, cell);
            }
        }
    }

    // Aggiungi handler per lo scrolling
    public void setupScrolling() {
        canvas.setOnScroll(event -> {
            // Aggiorna la posizione della viewport con lo scrolling
            viewportX = Math.max(0, Math.min(viewportX - event.getDeltaX(),
                    circuit.getWidth() * cellSize - canvas.getWidth()));
            viewportY = Math.max(0, Math.min(viewportY - event.getDeltaY(),
                    circuit.getHeight() * cellSize - canvas.getHeight()));
            render();
        });
    }

    private void drawCell(int x, int y, CircuitCell cell) {
        // Calcola la posizione effettiva tenendo conto della viewport
        double xPos = (x * cellSize) - viewportX;
        double yPos = (y * cellSize) - viewportY;

        // Non disegnare celle fuori dalla viewport
        if (xPos + cellSize < 0 || xPos > canvas.getWidth() ||
                yPos + cellSize < 0 || yPos > canvas.getHeight()) {
            return;
        }

        // Colore di sfondo della cella
        gc.setFill(getCellColor(cell));
        gc.fillRect(xPos, yPos, cellSize, cellSize);

        // Griglia
        gc.setStroke(GRID_LINE_COLOR);
        gc.setLineWidth(1);
        gc.strokeRect(xPos, yPos, cellSize, cellSize);
    }

    private Color getCellColor(CircuitCell cell) {
        if (cell instanceof WallCell)
            return WALL_COLOR;
        if (cell instanceof StartCell)
            return START_COLOR;
        if (cell instanceof FinishCell)
            return FINISH_COLOR;
        if (cell instanceof CheckpointCell)
            return CHECKPOINT_COLOR;
        return ROAD_COLOR; // RoadCell o default
    }

    private void drawCars() {
        if (players == null || players.isEmpty()) {
            System.out.println("Debug: drawCars - lista giocatori vuota o non inizializzata");
            return;
        }

        System.out.println("Debug: drawCars - Inizio rendering di " + players.size() + " giocatori");

        for (IPlayer player : players) {
            try {
                if (player == null) {
                    System.out.println("Debug: drawCars - trovato giocatore null nella lista");
                    continue;
                }

                if (player.getCar() == null) {
                    System.out.println("Debug: drawCars - " + player.getName() + " non ha un'auto associata");
                    continue;
                }

                if (player.getCar().getPosition() == null) {
                    System.out.println("Debug: drawCars - " + player.getName() + " ha un'auto senza posizione");
                    continue;
                }

                drawCar(player);
                System.out.println("Debug: drawCars - Disegnata auto di " + player.getName() +
                        " in posizione " + player.getCar().getPosition().getRow() +
                        "," + player.getCar().getPosition().getColumn());

            } catch (Exception e) {
                System.out.println("Debug: drawCars - Errore nel rendering del giocatore: " + e.getMessage());
            }
        }
    }

    private void drawCar(IPlayer player) {
        // Calcola la posizione dell'auto tenendo conto della viewport
        double x = (player.getCar().getPosition().getRow() * cellSize) - viewportX;
        double y = (player.getCar().getPosition().getColumn() * cellSize) - viewportY;

        // Skip se l'auto è fuori dalla viewport
        if (x + cellSize < 0 || x > canvas.getWidth() ||
                y + cellSize < 0 || y > canvas.getHeight()) {
            return;
        }

        // Dimensione dell'auto (80% della cella)
        double carSize = cellSize * 0.8;
        double offset = (cellSize - carSize) / 2;

        // Disegna l'auto come un cerchio usando il colore del giocatore
        Color carColor;
        try {
            String playerColor = player.getColor();
            if (playerColor == null || playerColor.isEmpty()) {
                carColor = Color.BLUE;
            } else {
                if (!playerColor.startsWith("#")) {
                    playerColor = "#" + playerColor;
                }
                carColor = Color.web(playerColor);
            }
        } catch (Exception e) {
            carColor = Color.BLUE;
        }

        gc.setFill(carColor);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.0);
        gc.fillOval(x + offset, y + offset, carSize, carSize);
        gc.strokeOval(x + offset, y + offset, carSize, carSize);
    }

    // Aggiungi supporto per il drag and drop per lo scrolling
    public void setupDragHandlers() {
        final double[] lastX = new double[1];
        final double[] lastY = new double[1];

        canvas.setOnMousePressed(e -> {
            lastX[0] = e.getX();
            lastY[0] = e.getY();
        });

        canvas.setOnMouseDragged(e -> {
            double deltaX = e.getX() - lastX[0];
            double deltaY = e.getY() - lastY[0];

            // Aggiorna la posizione della viewport con i limiti
            viewportX = Math.max(0, Math.min(viewportX - deltaX,
                    circuit.getWidth() * cellSize - viewportWidth));
            viewportY = Math.max(0, Math.min(viewportY - deltaY,
                    circuit.getHeight() * cellSize - viewportHeight));

            lastX[0] = e.getX();
            lastY[0] = e.getY();

            render();
        });
    }

    public void onResize() {
        calculateCellSize();
        render();
    }

    /**
     * Restituisce la larghezza calcolata del canvas in base alle dimensioni del
     * circuito.
     * 
     * @return la larghezza calcolata del canvas
     */
    public double getCalculatedWidth() {
        return calculatedWidth;
    }

    /**
     * Restituisce l'altezza calcolata del canvas in base alle dimensioni del
     * circuito.
     * 
     * @return l'altezza calcolata del canvas
     */
    public double getCalculatedHeight() {
        return calculatedHeight;
    }
}