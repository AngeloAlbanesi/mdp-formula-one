package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import java.util.List;

/**
 * Interfaccia per la validazione delle mosse nel gioco Formula 1.
 * Responsabile di verificare che le mosse rispettino le regole del gioco.
 */
public interface IMoveValidator {

    /**
     * Verifica se una mossa è valida secondo le regole del gioco.
     * 
     * @param currentPlayer il giocatore che sta effettuando la mossa
     * @param position la posizione attuale
     * @param acceleration l'accelerazione da applicare
     * @param circuit il circuito di gioco
     * @param players lista di tutti i giocatori per verificare le collisioni
     * @return true se la mossa è valida, false altrimenti
     */
    boolean isValidMove(IPlayer currentPlayer, 
                       IPosition position, 
                       IAcceleration acceleration,
                       ICircuit circuit,
                       List<IPlayer> players);

    /**
     * Verifica che l'accelerazione rispetti i limiti del gioco.
     * Nel gioco Formula 1, l'accelerazione può essere solo -1, 0, o 1 per ogni componente.
     * 
     * @param acceleration l'accelerazione da verificare
     * @return true se l'accelerazione è valida, false altrimenti
     */
    boolean isValidAcceleration(IAcceleration acceleration);
}