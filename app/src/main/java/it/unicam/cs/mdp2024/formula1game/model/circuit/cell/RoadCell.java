package it.unicam.cs.mdp2024.formula1game.model.circuit.cell;

public class RoadCell extends CircuitCell {
    public RoadCell(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isTraversable() {
        return true; // La strada è percorribile
    }

    @Override
    public char getSymbol() {
        return '.';
    }
}
