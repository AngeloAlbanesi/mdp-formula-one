package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.circuit.checkpoint.CheckpointManager;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;

import java.util.List;

public interface IGame2 {
    /**
     * Avvia la partita e continua a eseguire i turni finché la partita non finisce.
     */
    void start();

    /**
     * Coordina l'esecuzione del turno.
     */
    void executeTurn();

    /**
     * Ritorna true se la partita è finita.
     * @return true se la partita è finita, false altrimenti.
     */
    boolean isGameOver();

    /**
     * Ritorna il vincitore della partita.
     * @return il vincitore della partita.
     */
    IPlayer getWinner();

    /**
     * Ritorna la lista dei giocatori
     * @return la lista dei giocatori
     */
    List<IPlayer> getPlayers();

    /**
     * Ritorna il circuito
     * @return il circuito
     */
    ICircuit getCircuit();

    /**
     * Ritorna lo stato della partita
     * @return lo stato della partita
     */
    String getGameState();

    /**
     * Ottiene il gestore dei checkpoint del gioco.
     * @return il gestore dei checkpoint
     */
    CheckpointManager getCheckpointManager();
}
