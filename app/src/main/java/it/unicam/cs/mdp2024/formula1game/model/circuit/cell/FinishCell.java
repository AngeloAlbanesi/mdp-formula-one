package it.unicam.cs.mdp2024.formula1game.model.circuit.cell;

public class FinishCell extends CircuitCell {
    public FinishCell(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isTraversable() {
        return true; // La linea di arrivo è percorribile
    }

    @Override
    public char getSymbol() {
        return '*';
    }
}
