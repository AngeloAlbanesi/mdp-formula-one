package it.unicam.cs.mdp2024.formula1game.model.circuit.cell;

public abstract class CircuitCell {
    private final int x;
    private final int y;

    public CircuitCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public abstract boolean isTraversable();

    public abstract char getSymbol();

}
