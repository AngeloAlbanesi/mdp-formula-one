package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;
import it.unicam.cs.mdp2024.formula1game.model.util.IVector;
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
    @Override
    public boolean isValidMove(IPlayer currentPlayer,
            IPosition position,
            IAcceleration acceleration,
            ICircuit circuit,
            List<IPlayer> players) {
        validateParameters(currentPlayer, position, acceleration, circuit, players);
        validatePlayerCar(currentPlayer);

        if (!isValidAcceleration(acceleration)) {
            return false;
        }

        IPosition futurePosition = calculateFuturePosition(currentPlayer.getCar(), position, acceleration);

        return isValidDestination(futurePosition, circuit) &&
                !hasCollisionWithOtherPlayers(futurePosition, currentPlayer, players);
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
        return circuit.isValidPosition(position);
    }

    /**
     * Verifica che non ci siano collisioni con altri giocatori attivi.
     */
    private boolean hasCollisionWithOtherPlayers(IPosition position,
            IPlayer currentPlayer,
            List<IPlayer> players) {
        return players.stream()
                .filter(p -> p != currentPlayer && p.isActive())
                .anyMatch(p -> position.equals(p.getCar().getPosition()));
    }
}