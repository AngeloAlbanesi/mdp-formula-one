package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Implementation of the IAcceleration interface linked to velocity.
 */
public class Acceleration implements IAcceleration {

    @Override
    public IVelocity calculateAcceleration(IVelocity previousVelocity, IVelocity currentVelocity) {
        // Calculate acceleration as the difference between current and previous
        // velocity
        IVector accelerationVector = currentVelocity.getCurrentVelocity()
                .subtract(previousVelocity.getCurrentVelocity());
        return new Velocity(accelerationVector);
    }

    @Override
    public boolean isValidAcceleration(IVelocity acceleration, int maxChange) {
        // Check if acceleration per component is within ±maxChange
        IVector vector = acceleration.getCurrentVelocity();
        return Math.abs(vector.getX()) <= maxChange && Math.abs(vector.getY()) <= maxChange;
    }

    @Override
    public IVelocity clampAcceleration(IVelocity acceleration, int maxChange) {
        // Clamp each component of the acceleration
        IVector clampedVector = new Vector(
                Math.max(-maxChange, Math.min(maxChange, acceleration.getCurrentVelocity().getX())),
                Math.max(-maxChange, Math.min(maxChange, acceleration.getCurrentVelocity().getY())));
        return new Velocity(clampedVector);
    }
}