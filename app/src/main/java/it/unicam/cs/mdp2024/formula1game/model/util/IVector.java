package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Interface for a 2D vector.
 */
public interface IVector {
    int getX(); // Returns the X component of the vector

    int getY(); // Returns the Y component of the vector

    IVector add(IVector other); // Adds another vector to this vector

    IVector subtract(IVector other); // Subtracts another vector from this vector

    IVector scale(int factor); // Scales the vector by a factor

    double magnitude(); // Calculates the magnitude of the vector

    IVector unitVector(); // New

    double dotProduct(IVector other); // New

    int manhattanDistance(IVector other); // New

    int direction(); // New: e.g., 0=N, 1=NE, 2=E, ...

    boolean equals(IVector other); // Checks if this vector is equal to another

    String toString(); // Returns a string representation of the vector
}