package it.unicam.cs.mdp2024.formula1game.model.strategy;

import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.Velocity;
import it.unicam.cs.mdp2024.formula1game.model.util.Vector;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;

/**
 * Controller per la gestione della velocità in base alle condizioni del
 * circuito.
 * Si occupa di regolare la velocità in modo difensivo, specialmente in curva.
 */
public class SpeedController {

    private static final double MAX_CURVE_SPEED = 1.5;
    private static final double SAFE_SPEED = 1.0;
    private static final double ACCELERATION_FACTOR = 0.5;

    /**
     * Calcola la velocità sicura per una data posizione e direzione.
     *
     * @param currentPos      posizione attuale
     * @param nextPos         prossima posizione
     * @param currentVelocity velocità attuale
     * @return velocità regolata in base alle condizioni
     */
    public IVelocity calculateSafeVelocity(IPosition currentPos, IPosition nextPos, IVelocity currentVelocity) {
        double speedMultiplier = 1.0;

        // Riduce la velocità in curva
        if (isTurning(currentPos, nextPos, currentVelocity)) {
            speedMultiplier = Math.min(speedMultiplier, MAX_CURVE_SPEED);
        }

        // Applica l'accelerazione graduale
        double currentSpeed = Math.sqrt(
                Math.pow(currentVelocity.getCurrentVelocity().getX(), 2) +
                        Math.pow(currentVelocity.getCurrentVelocity().getY(), 2));

        if (currentSpeed > SAFE_SPEED) {
            speedMultiplier *= ACCELERATION_FACTOR;
        }

        // Crea una nuova velocità regolata
        Vector newVector = new Vector(
                (int) (currentVelocity.getCurrentVelocity().getX() * speedMultiplier),
                (int) (currentVelocity.getCurrentVelocity().getY() * speedMultiplier));

        return new Velocity(newVector);
    }

    /**
     * Determina se la macchina sta effettuando una curva.
     */
    private boolean isTurning(IPosition currentPos, IPosition nextPos, IVelocity currentVelocity) {
        // Calcola il cambio di direzione
        int dRow = nextPos.getRow() - currentPos.getRow();
        int dCol = nextPos.getColumn() - currentPos.getColumn();

        // Se la direzione attuale è diversa dalla velocità corrente, è una curva
        return dRow != currentVelocity.getCurrentVelocity().getY() ||
                dCol != currentVelocity.getCurrentVelocity().getX();
    }
}