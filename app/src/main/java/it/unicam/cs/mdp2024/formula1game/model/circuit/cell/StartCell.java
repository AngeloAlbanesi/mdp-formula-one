package it.unicam.cs.mdp2024.formula1game.model.circuit.cell;

public class StartCell extends CircuitCell {
    public StartCell(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isTraversable() {
        return true; // La casella di partenza è percorribile
    }

    @Override
    public char getSymbol() {
        return 'S';
    }
}
