package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;
import it.unicam.cs.mdp2024.formula1game.model.util.IVector;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.car.ICar;

import java.util.List;

/**
 * Implementazione predefinita del validatore delle mosse che verifica
 * la validità delle mosse nel gioco secondo le regole stabilite.
 * Applica una serie di controlli sequenziali per garantire che ogni mossa
 * rispetti i vincoli del gioco.
 */
public class DefaultMoveValidator implements IMoveValidator {

    /**
     * Verifica la validità di una mossa considerando la posizione attuale,
     * l'accelerazione proposta e il contesto di gioco.
     *
     * @param currentPlayer il giocatore che sta effettuando la mossa
     * @param position      la posizione attuale
     * @param acceleration  l'accelerazione proposta
     * @param circuit       il circuito di gioco
     * @param players       la lista dei giocatori
     * @return true se la mossa è valida, false altrimenti
     * @throws IllegalArgumentException se uno dei parametri è null
     * @throws IllegalStateException    se il giocatore non ha un'auto assegnata
     */
    private static final double MAX_SPEED = 5.0;

    @Override
    public boolean isValidMove(IPlayer currentPlayer,
            IPosition position,
            IAcceleration acceleration,
            ICircuit circuit,
            List<IPlayer> players) {
        try {
            validateParameters(currentPlayer, position, acceleration, circuit, players);
            validatePlayerCar(currentPlayer);

            System.out.println("Debug: Validating move for " + currentPlayer.getName() +
                    " from " + position +
                    " with acceleration " + acceleration.getAccelerationVector());

            if (!isValidAcceleration(acceleration)) {
                System.out.println("Debug: Invalid acceleration values");
                return false;
            }

            IPosition futurePosition = calculateFuturePosition(currentPlayer.getCar(), position, acceleration);
            System.out.println("Debug: Calculated future position: " + futurePosition);

            // Verifica velocità massima
            IVelocity futureVelocity = currentPlayer.getCar().getVelocity().addAcceleration(acceleration);
            IVector velocityVector = futureVelocity.getCurrentVelocity();
            double speed = Math.sqrt(velocityVector.getX() * velocityVector.getX() +
                    velocityVector.getY() * velocityVector.getY());

            if (speed > MAX_SPEED) {
                System.out.println("Debug: Speed " + speed + " exceeds maximum allowed " + MAX_SPEED);
                return false;
            }

            // Verifica posizione valida
            if (!isValidDestination(futurePosition, circuit)) {
                System.out.println("Debug: Invalid destination: " + futurePosition);
                return false;
            }

            // Verifica collisioni
            if (hasCollisionWithOtherPlayers(futurePosition, currentPlayer, players)) {
                System.out.println("Debug: Collision detected at " + futurePosition);
                return false;
            }

            System.out.println("Debug: Move validation successful");
            return true;
        } catch (Exception e) {
            System.out.println("Debug: Move validation failed with exception: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica che l'accelerazione rispetti i limiti consentiti (-1, 0, 1 per ogni
     * componente).
     *
     * @param acceleration l'accelerazione da verificare
     * @return true se l'accelerazione è valida, false altrimenti
     */
    @Override
    public boolean isValidAcceleration(IAcceleration acceleration) {
        if (acceleration == null) {
            return false;
        }

        IVector acc = acceleration.getAccelerationVector();
        return Math.abs(acc.getX()) <= 1 && Math.abs(acc.getY()) <= 1;
    }

    /**
     * Valida che tutti i parametri necessari non siano null.
     */
    private void validateParameters(IPlayer currentPlayer,
            IPosition position,
            IAcceleration acceleration,
            ICircuit circuit,
            List<IPlayer> players) {
        if (currentPlayer == null || position == null ||
                acceleration == null || circuit == null || players == null) {
            throw new IllegalArgumentException("Parametri non possono essere null");
        }
    }

    /**
     * Verifica che il giocatore abbia un'auto assegnata.
     */
    private void validatePlayerCar(IPlayer player) {
        if (player.getCar() == null) {
            throw new IllegalStateException("Il giocatore non ha un'auto assegnata");
        }
    }

    /**
     * Calcola la posizione futura basata sulla posizione attuale, velocità e
     * accelerazione.
     */
    private IPosition calculateFuturePosition(ICar car,
            IPosition currentPosition,
            IAcceleration acceleration) {
        IVelocity futureVelocity = car.getVelocity().addAcceleration(acceleration);
        return currentPosition.nextPosition(futureVelocity);
    }

    /**
     * Verifica che la posizione di destinazione sia valida nel circuito.
     */
    private boolean isValidDestination(IPosition position, ICircuit circuit) {
        System.out.println("Debug: Validating destination position: " + position);

        // Verifica confini e muri
        if (!circuit.isValidPosition(position)) {
            System.out.println("Debug: Position is outside circuit bounds");
            return false;
        }
        if (circuit.isWall(position.getColumn(), position.getRow())) {
            System.out.println("Debug: Position is on a wall");
            return false;
        }

        // Verifica che ci siano almeno due celle adiacenti valide per evitare stalli
        int validDirections = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int newX = position.getColumn() + dx;
                int newY = position.getRow() + dy;
                
                if (circuit.isValidPosition(new Position(newX, newY)) &&
                    !circuit.isWall(newX, newY)) {
                    validDirections++;
                    if (validDirections >= 2) { // Richiediamo almeno 2 direzioni valide
                        System.out.println("Debug: Position has enough valid directions");
                        return true;
                    }
                }
            }
        }

        System.out.println("Debug: Position is in a potential stall condition");
        return false; // Non ci sono abbastanza direzioni valide
    }

    /**
     * Verifica che non ci siano collisioni con altri giocatori attivi.
     */
    private boolean hasCollisionWithOtherPlayers(IPosition position,
            IPlayer currentPlayer,
            List<IPlayer> players) {
        // Controlla le collisioni con le posizioni attuali degli altri giocatori
        for (IPlayer other : players) {
            if (other != currentPlayer && other.isActive()) {
                IPosition otherPos = other.getCar().getPosition();
                double distance = position.distanceTo(otherPos);

                System.out.println("Debug: Checking collision between " + currentPlayer.getName() +
                        " at " + position + " and " + other.getName() + " at " + otherPos +
                        " (distance: " + distance + ")");

                if (distance <= 1.0) {  // Modificata soglia di collisione
                    System.out.println("Debug: Collision detected at distance " + distance);
                    return true;
                }
            }
        }
        return false;
    }
}
