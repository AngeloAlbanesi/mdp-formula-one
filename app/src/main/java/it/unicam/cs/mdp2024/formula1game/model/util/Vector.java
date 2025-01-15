package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Implementation of the IVector interface.
 */
public class Vector implements IVector {
    private final int x;
    private final int y;

    /**
     * Constructor to initialize a vector with x and y components.
     * 
     * @param x the X component of the vector
     * @param y the Y component of the vector
     */
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public IVector add(IVector other) {
        return new Vector(this.x + other.getX(), this.y + other.getY());
    }

    @Override
    public IVector subtract(IVector other) {
        return new Vector(this.x - other.getX(), this.y - other.getY());
    }

    @Override
    public IVector scale(int factor) {
        return new Vector(this.x * factor, this.y * factor);
    }

    @Override
    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    @Override
public IVector unitVector() {
    double mag = magnitude();
    return mag == 0 ? new Vector(0, 0) : new Vector((int) (x / mag), (int) (y / mag));
}

@Override
public double dotProduct(IVector other) {
    return this.x * other.getX() + this.y * other.getY();
}

@Override
public int manhattanDistance(IVector other) {
    return Math.abs(this.x - other.getX()) + Math.abs(this.y - other.getY());
}

//TODO: creare enumerazione direzioni
@Override
public int direction() {
    // Example implementation: converts the vector to a direction (N, NE, etc.)
    if (x == 0 && y > 0) return 0; // North
    if (x > 0 && y > 0) return 1; // North-East
    if (x > 0 && y == 0) return 2; // East
    // ...other directions
    return -1; // Undefined
}


    @Override
    public boolean equals(IVector other) {
        if (other == null) return false;
        return this.x == other.getX() && this.y == other.getY();
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}