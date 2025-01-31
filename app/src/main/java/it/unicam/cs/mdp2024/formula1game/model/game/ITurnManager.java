package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;

/**
 * Gestisce i turni del gioco, mantenendo traccia del giocatore corrente
 * e del conteggio dei turni.
 */
public interface ITurnManager {
    
    /**
     * Ottiene il giocatore corrente.
     * @return il giocatore corrente
     */
    IPlayer getCurrentPlayer();

    /**
     * Passa al prossimo turno.
     */
    void nextTurn();

    /**
     * Resetta lo stato del gestore dei turni.
     */
    void reset();

    /**
     * Ottiene il numero totale di turni giocati.
     * @return il numero di turni
     */
    int getTurnCount();

    /**
     * Verifica se il giocatore corrente è attivo.
     * @return true se il giocatore corrente è attivo
     */
    boolean isCurrentPlayerActive();

    /**
     * Imposta i giocatori per il manager dei turni.
     * @param players la lista dei giocatori
     */
    void setPlayers(java.util.List<IPlayer> players);
}