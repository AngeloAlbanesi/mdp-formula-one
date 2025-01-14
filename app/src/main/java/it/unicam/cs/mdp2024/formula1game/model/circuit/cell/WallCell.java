package it.unicam.cs.mdp2024.formula1game.model.circuit.cell;

public class WallCell extends CircuitCell {
    public WallCell(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isTraversable() {
        return false; // Un muro non è percorribile
    }

    @Override
    public char getSymbol() {
        return '#';
    }
}
