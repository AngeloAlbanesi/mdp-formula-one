package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Implementation of the IVelocity interface.
 */
public class Velocity implements IVelocity {
    private int x;
    private int y;

    public Velocity(int x, int y) {
        this.x = x;
        this.y = y;
    }
    private IVector currentVelocity;

    /**
     * Constructor to initialize velocity with an initial vector.
     * 
     * @param initialVelocity the starting velocity vector
     */
    public Velocity(IVector initialVelocity) {
        this.currentVelocity = initialVelocity;
    }

    @Override
    public IVector getCurrentVelocity() {
        return currentVelocity;
    }

    @Override
    public void update(IVector newVelocity) {
        if (isValidChange(newVelocity)) {
            this.currentVelocity = newVelocity;
        } else {
            throw new IllegalArgumentException("Invalid velocity change: " + newVelocity);
        }
    }

    @Override
    public boolean isValidChange(IVector newVelocity) {
        int dx = Math.abs(newVelocity.getX() - currentVelocity.getX());
        int dy = Math.abs(newVelocity.getY() - currentVelocity.getY());
        return dx <= 1 && dy <= 1; // Change in each component must be at most ±1
    }

    @Override
    public boolean isWithinMaxVelocity(int maxMagnitude) {
        return currentVelocity.magnitude() <= maxMagnitude;
    }

    @Override
    public void clamp(int maxMagnitude) {
        if (!isWithinMaxVelocity(maxMagnitude)) {
            double factor = maxMagnitude / currentVelocity.magnitude();
            currentVelocity = currentVelocity.scale((int) factor);
        }
    }

    @Override
    public IVelocity addAcceleration(IAcceleration acceleration) {
        // Usa il vettore di accelerazione per modificare la velocità
        IVector newVector = this.currentVelocity.add(acceleration.getAccelerationVector());
        return new Velocity(newVector);
    }

    @Override
    public String toString() {
        return "Velocity: " + this.currentVelocity.toString();
    }
}