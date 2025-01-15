package it.unicam.cs.mdp2024.formula1game.model.util;

/**
 * Implementation of the IPosition interface.
 */
public class Position implements IPosition {
    private final int row;
    private final int column;

    /**
     * Constructor to initialize a position with row and column values.
     * 
     * @param row the row index
     * @param column the column index
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public boolean isInsideCircuit(int rows, int columns) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    @Override
    public double distanceTo(IPosition other) {
        int dRow = other.getRow() - this.row;
        int dCol = other.getColumn() - this.column;
        return Math.sqrt(dRow * dRow + dCol * dCol);
    }

    @Override
public int manhattanDistanceTo(IPosition other) {
    return Math.abs(this.row - other.getRow()) + Math.abs(this.column - other.getColumn());
}

@Override
public boolean isAdjacentTo(IPosition other) {
    int dRow = Math.abs(this.row - other.getRow());
    int dCol = Math.abs(this.column - other.getColumn());
    return dRow <= 1 && dCol <= 1;
}

@Override
public IVector vectorTo(IPosition other) {
    return new Vector(other.getColumn() - this.column, other.getRow() - this.row);
}

@Override
public IPosition nextPosition(IVelocity velocity) {
    IVector velocityVector = velocity.getCurrentVelocity();
    return new Position(this.row + velocityVector.getY(), this.column + velocityVector.getX());
}


    @Override
    public boolean equals(IPosition other) {
        if (other == null) return false;
        return this.row == other.getRow() && this.column == other.getColumn();
    }

    @Override
    public String toString() {
        return "(" + this.row + ", " + this.column + ")";
    }
}