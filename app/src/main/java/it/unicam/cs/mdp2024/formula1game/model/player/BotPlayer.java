package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.car.Car;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.CheckpointManager;
import it.unicam.cs.mdp2024.formula1game.model.game.DefaultMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.game.IMoveValidator;
import it.unicam.cs.mdp2024.formula1game.model.strategy.MovementContext;
import it.unicam.cs.mdp2024.formula1game.model.util.*;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rappresenta un giocatore bot nel gioco Formula 1.
 * I bot eseguono mosse automaticamente usando algoritmi di AI.
 */
public class BotPlayer extends Player {

    private final int strategyCode;
    private ICircuit circuit;
    private IMoveValidator moveValidator;
    private CheckpointManager checkpointManager;
    private MovementContext movementContext;

    /**
     * Crea un nuovo bot player con il nome specificato e la strategia A* di default.
     *
     * @param name il nome del bot player
     * @param color il colore del bot player
     */
    public BotPlayer(String name, String color) {
        this(name, color, 1); // Default alla strategia A*
    }

    /**
     * Crea un nuovo bot player con il nome e la strategia specificati.
     *
     * @param name il nome del bot player
     * @param color il colore del bot player
     * @param strategyCode il codice della strategia di pathfinding (1=A*, 2=Dijkstra)
     */
    public BotPlayer(String name, String color, int strategyCode) {
        super(name, color);
        if (strategyCode < 1 || strategyCode > 2) {
            throw new IllegalArgumentException("Codice strategia non valido: " + strategyCode);
        }
        this.strategyCode = strategyCode;
    }

    /**
     * Inizializza il bot con le dipendenze necessarie dal gioco.
     */
    public void initializeGameDependencies(ICircuit circuit, IMoveValidator validator, CheckpointManager checkpointManager) {
        this.circuit = circuit;
        this.moveValidator = validator;
        this.checkpointManager = checkpointManager;
        
        if (!(validator instanceof DefaultMoveValidator)) {
            throw new IllegalArgumentException("Il validatore deve essere di tipo DefaultMoveValidator");
        }
        
        // Inizializza il movimento context
        this.movementContext = new MovementContext(strategyCode, (DefaultMoveValidator) validator);
        
        // Configura i pesi per il movimento
        if (strategyCode == 2) { // Strategia difensiva Dijkstra
            this.movementContext.configureWeights(0.8, 1.5, 2.5, 0.8);
        } else { // Strategia A*
            this.movementContext.configureWeights(1.5, 0.8, 1.2, 1.5);
        }
        
        // Inizializza la macchina del bot dalla posizione di partenza del circuito
        if (!circuit.getStartPositions().isEmpty()) {
            IPosition startPosition = circuit.getStartPositions().get(0);
            this.setCar(new Car(
                startPosition,
                new Velocity(new it.unicam.cs.mdp2024.formula1game.model.util.Vector(0, 0)),
                new Acceleration(new it.unicam.cs.mdp2024.formula1game.model.util.Vector(0, 0))
            ));
        }
    }

    /**
     * Ottiene il codice della strategia di pathfinding di questo bot.
     *
     * @return il codice della strategia (1=A*, 2=Dijkstra)
     */
    public int getStrategyCode() {
        return strategyCode;
    }

    private List<IPlayer> currentPlayers = new ArrayList<>();
    
    public void setCurrentPlayers(List<IPlayer> players) {
        this.currentPlayers = players;
    }

    @Override
    public IAcceleration chooseAcceleration() {
        // Verifica che il movimento context sia inizializzato
        if (this.movementContext == null || this.circuit == null) {
            return new Acceleration(new Vector(0, 0));
        }

        // Ottieni la posizione e velocità corrente
        IPosition currentPosition = this.getCar().getPosition();
        IVelocity currentVelocity = this.getCar().getVelocity();
        
        // Calcola le posizioni degli avversari
        List<IPosition> opponentPositions = currentPlayers.stream()
            .filter(p -> p != this && p.isActive())
            .map(p -> p.getCar().getPosition())
            .collect(Collectors.toList());
            
        // Aggiorna il contesto di gioco
        movementContext.setGameContext(this, currentPlayers);
        
        // Ottieni il prossimo checkpoint da raggiungere
        IPosition nextCheckpoint = checkpointManager.getNextCheckpoint(this);
        
        // Se non ci sono più checkpoint da visitare, usa la linea del traguardo
        if (nextCheckpoint == null && !circuit.getFinishPositions().isEmpty()) {
            nextCheckpoint = circuit.getFinishPositions().get(0);
        }
        
        // Se non c'è né checkpoint né traguardo, mantieni la posizione
        if (nextCheckpoint == null) {
            return new Acceleration(new Vector(0, 0));
        }
        
        // Calcola prossima mossa escludendo (0,0)
        // Calcola la mossa principale
        IAcceleration nextMove = movementContext.calculateNextMove(
            currentPosition,
            currentVelocity,
            opponentPositions,
            circuit,
            nextCheckpoint
        );

        // Verifica che la mossa sia valida
        if (!moveValidator.isValidMove(this, currentPosition, nextMove, circuit, currentPlayers)) {
            System.out.println("Debug: Primary move invalid, trying alternatives");
            // Prova mosse alternative in ordine di priorità
            int[][] alternatives = {
                {0, -1}, {0, 1},  // Verticale
                {-1, 0}, {1, 0},  // Orizzontale
                {-1, -1}, {1, -1}, {-1, 1}, {1, 1}  // Diagonali
            };
            
            for (int[] alt : alternatives) {
                IAcceleration altMove = new Acceleration(new Vector(alt[0], alt[1]));
                if (moveValidator.isValidMove(this, currentPosition, altMove, circuit, currentPlayers)) {
                    System.out.println("Debug: Found valid alternative move: " + alt[0] + "," + alt[1]);
                    nextMove = altMove;
                    break;
                }
            }
        }

        // Limita l'accelerazione massima
        IVector moveVector = nextMove.getAccelerationVector();
        if (moveVector.magnitude() > 1) {
            IVector unitVec = moveVector.unitVector();
            nextMove = new Acceleration(new Vector(unitVec.getX(), unitVec.getY()));
        }
        
        return nextMove;
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
