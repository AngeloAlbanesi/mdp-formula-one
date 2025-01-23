package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Interface for managing the velocity of a player.
 */
public interface IVelocity {
    IVector getCurrentVelocity(); // Gets the current velocity vector

    void update(IVector newVelocity); // Updates the velocity vector

    boolean isValidChange(IVector newVelocity); // Checks if the new velocity is a valid change

    boolean isWithinMaxVelocity(int maxMagnitude); // Checks if the velocity is within a maximum magnitude

    IVelocity addAcceleration(IAcceleration acceleration); // Adds an acceleration to the velocity

    void clamp(int maxMagnitude); // Clamps the velocity to a maximum magnitude

    String toString(); // Returns a string representation of the velocity
}