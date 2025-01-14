package it.unicam.cs.mdp2024.formula1game.model.circuit;

import java.io.IOException;

public interface ICircuitLoader {
    ICircuit loadCircuit(int index) throws IOException; // Carica un circuito dato un indice
}