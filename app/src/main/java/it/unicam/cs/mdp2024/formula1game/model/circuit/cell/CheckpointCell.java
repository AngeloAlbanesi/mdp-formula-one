package it.unicam.cs.mdp2024.formula1game.model.circuit.cell;

/**
 * Rappresenta una cella checkpoint nel circuito, identificata dal simbolo '@'.
 * I checkpoint sono punti intermedi che possono essere utilizzati per ottimizzare
 * il percorso delle auto nel raggiungimento del traguardo.
 */
public class CheckpointCell extends CircuitCell {
    
    /**
     * Costruisce una nuova cella checkpoint.
     * 
     * @param x coordinata x della cella
     * @param y coordinata y della cella
     */
    public CheckpointCell(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean isTraversable() {
        return true;
    }

    @Override
    public char getSymbol() {
        return '@';
    }
}