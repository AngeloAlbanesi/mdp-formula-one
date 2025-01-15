package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Interface for calculating and validating acceleration linked to velocity.
 */
public interface IAcceleration {
    /**
     * Calculates the acceleration vector required to transition
     * from one velocity to another.
     * 
     * @param previousVelocity the initial velocity
     * @param currentVelocity  the final velocity
     * @return the acceleration as a velocity
     */
    IVelocity calculateAcceleration(IVelocity previousVelocity, IVelocity currentVelocity);

    /**
     * Checks if the given acceleration is valid based on the maximum allowed
     * change.
     * 
     * @param acceleration the acceleration (as a velocity)
     * @param maxChange    the maximum allowed change per component
     * @return true if the acceleration is valid, false otherwise
     */
    boolean isValidAcceleration(IVelocity acceleration, int maxChange);

    /**
     * Clamps the acceleration to respect the maximum allowed change.
     * 
     * @param acceleration the acceleration (as a velocity)
     * @param maxChange    the maximum allowed change per component
     * @return the clamped acceleration as a velocity
     */
    IVelocity clampAcceleration(IVelocity acceleration, int maxChange);
}