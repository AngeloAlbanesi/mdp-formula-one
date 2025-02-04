package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Implementation of the IAcceleration interface linked to velocity.
 */
public class Acceleration implements IAcceleration {
    private final IVector accelerationVector;

    public Acceleration(IVector accelerationVector) {
        this.accelerationVector = accelerationVector;
    }

    public IVector getAccelerationVector() {
        return accelerationVector;
    }

    @Override
    public IVelocity calculateAcceleration(IVelocity previousVelocity, IVelocity currentVelocity) {
        IVector diffVector = currentVelocity.getCurrentVelocity()
                .subtract(previousVelocity.getCurrentVelocity());
        return new Velocity(diffVector.getX(), diffVector.getY());
    }

    @Override
    public boolean isValidAcceleration(IVelocity acceleration, int maxChange) {
        IVector vector = acceleration.getCurrentVelocity();
        return Math.abs(vector.getX()) <= maxChange &&
                Math.abs(vector.getY()) <= maxChange;
    }

    @Override
    public IVelocity clampAcceleration(IVelocity acceleration, int maxChange) {
        IVector clampedVector = new Vector(
                Math.max(-maxChange, Math.min(maxChange, acceleration.getCurrentVelocity().getX())),
                Math.max(-maxChange, Math.min(maxChange, acceleration.getCurrentVelocity().getY())));
        return new Velocity(clampedVector);
    }
}