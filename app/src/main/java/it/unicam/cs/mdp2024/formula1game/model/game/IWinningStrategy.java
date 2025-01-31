package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import java.util.List;
import java.util.Map;

/**
 * Definisce la strategia per determinare il vincitore di una partita.
 * Diverse implementazioni possono usare criteri diversi (es. primo a tagliare
 * il traguardo,
 * maggior numero di giri completati, ecc.)
 */
public interface IWinningStrategy {

    /**
     * Determina se c'è un vincitore in base allo stato corrente del gioco.
     * 
     * @param players   i giocatori in gioco
     * @param circuit   il circuito di gioco
     * @param laps      mappa che associa ad ogni giocatore il numero di giri
     *                  completati
     * @param turnCount numero di turni giocati
     * @param maxTurns  numero massimo di turni consentiti
     * @return il giocatore vincitore, o null se non c'è ancora un vincitore
     */
    IPlayer determineWinner(List<IPlayer> players,
            ICircuit circuit,
            Map<IPlayer, Integer> laps,
            int turnCount,
            int maxTurns);

    /**
     * Verifica se la partita è terminata in base alle condizioni specifiche
     * della strategia di vittoria.
     * 
     * @param players   i giocatori in gioco
     * @param circuit   il circuito di gioco
     * @param laps      mappa che associa ad ogni giocatore il numero di giri
     *                  completati
     * @param turnCount numero di turni giocati
     * @param maxTurns  numero massimo di turni consentiti
     * @return true se la partita è terminata, false altrimenti
     */
    boolean isGameOver(List<IPlayer> players,
            ICircuit circuit,
            Map<IPlayer, Integer> laps,
            int turnCount,
            int maxTurns);

    /**
     * Aggiorna il conteggio dei giri per un giocatore.
     * 
     * @param player il giocatore che ha completato un giro
     * @param laps   mappa che associa ad ogni giocatore il numero di giri
     *               completati
     * @return true se l'aggiornamento ha causato la vittoria del giocatore
     */
    boolean updateLaps(IPlayer player, Map<IPlayer, Integer> laps);
}