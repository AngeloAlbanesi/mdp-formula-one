package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Interface for a position on a 2D grid.
 */
public interface IPosition {
    int getRow(); // Gets the row index of the position

    int getColumn(); // Gets the column index of the position

    boolean isInsideCircuit(int rows, int columns); // Checks if the position is within the bounds of the circuit

    double distanceTo(IPosition other); // Calculates the Euclidean distance to another position

    int manhattanDistanceTo(IPosition other); // Calculates the Manhattan distance to another position

    boolean isAdjacentTo(IPosition other); // Checks if this position is adjacent to another position

    IVector vectorTo(IPosition other); // Returns the vector from this position to another position

    IPosition nextPosition(IVelocity velocity); // Updated to use IVelocity // Returns the next position after a given
                                                // velocity

    boolean equals(IPosition other); // Checks if this position equals another position

    String toString(); // Returns a string representation of the position
}