package it.unicam.cs.mdp2024.formula1game.model.circuit;

import it.unicam.cs.mdp2024.formula1game.model.circuit.cell.CircuitCell;

public interface ICircuit {

    int getWidth(); // Restituisce la larghezza del circuito

    int getHeight(); // Restituisce l'altezza del circuito

    boolean isOncircuit(int x, int y); // Verifica se una coordinata è sulla pista

    boolean isStartingPoint(int x, int y); // Verifica se una coordinata è un punto di partenza

    boolean isFinishLine(int x, int y); // Verifica se una coordinata è una linea di arrivo

    boolean isWall(int x, int y); // Verifica se una coordinata è una parete

    boolean isValid(); // Verifica che il circuito sia valido

    void validate(); // Valida il circuito

    public CircuitCell[][] getGrid(); // Restituisce la griglia del circuito

    public CircuitCell getCell(int x, int y); // Restituisce la cella alla posizione (x,y)

    public void printCircuit(); // Stampa il circuito su console

}
