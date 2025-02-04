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
        this.players = players;
    }

    private static final double MAX_CELL_SIZE = 40.0;
    private static final double MIN_CELL_SIZE = 10.0;
    private static final double MAX_TEXTURE_SIZE = 16384.0; // Limite massimo di texture JavaFX

    private void calculateCellSize() {
        if (circuit == null) return;
        
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
                Math.max(MIN_CELL_SIZE, Math.min(maxCellSizeForWidth, maxCellSizeForHeight))
            )
        );

        // Calcola le dimensioni finali del canvas
        double finalWidth = Math.min(gridWidth * cellSize, maxAllowedWidth);
        double finalHeight = Math.min(gridHeight * cellSize, maxAllowedHeight);
        
        canvas.setWidth(finalWidth);
        canvas.setHeight(finalHeight);
        
        // Aggiorna le dimensioni della viewport
        viewportWidth = finalWidth;
        viewportHeight = finalHeight;
    }

    public void render() {
        if (circuit == null) return;

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
        int startX = Math.max(0, (int)(viewportX / cellSize));
        int startY = Math.max(0, (int)(viewportY / cellSize));
        int endX = Math.min(circuit.getWidth(), (int)((viewportX + canvas.getWidth()) / cellSize) + 1);
        int endY = Math.min(circuit.getHeight(), (int)((viewportY + canvas.getHeight()) / cellSize) + 1);

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
        if (cell instanceof WallCell) return WALL_COLOR;
        if (cell instanceof StartCell) return START_COLOR;
        if (cell instanceof FinishCell) return FINISH_COLOR;
        if (cell instanceof CheckpointCell) return CHECKPOINT_COLOR;
        return ROAD_COLOR; // RoadCell o default
    }

   private void drawCars() {
        for (IPlayer player : players) {
            if (player instanceof BotPlayer) {
                drawCar((BotPlayer) player);
            }
        }
    }

    private void drawCar(BotPlayer player) {
        // Calcola la posizione dell'auto tenendo conto della viewport
        double x = (player.getCar().getPosition().getColumn() * cellSize) - viewportX;
        double y = (player.getCar().getPosition().getRow() * cellSize) - viewportY;

        // Skip se l'auto è fuori dalla viewport
        if (x + cellSize < 0 || x > canvas.getWidth() ||
            y + cellSize < 0 || y > canvas.getHeight()) {
            return;
        }

        // Dimensione dell'auto (80% della cella)
        double carSize = cellSize * 0.8;
        double offset = (cellSize - carSize) / 2;

        // Disegna l'auto come un cerchio
        gc.setFill(Color.BLUE); // TODO: Usa il colore del giocatore quando implementato
        gc.fillOval(x + offset, y + offset, carSize, carSize);
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
}