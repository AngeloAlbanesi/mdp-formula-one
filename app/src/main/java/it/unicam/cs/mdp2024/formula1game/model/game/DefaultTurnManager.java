package it.unicam.cs.mdp2024.formula1game.model.game;

import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione predefinita del gestore dei turni.
 * Gestisce la rotazione dei turni tra i giocatori e tiene traccia
 * del numero di turni giocati.
 */
public class DefaultTurnManager implements ITurnManager {
    
    private List<IPlayer> players;
    private int currentPlayerIndex;
    private int turnCount;

    /**
     * Crea un nuovo gestore dei turni.
     */
    public DefaultTurnManager() {
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.turnCount = 0;
    }

    @Override
    public IPlayer getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new GameException.InvalidGameStateException("Nessun giocatore disponibile");
        }
        if (!hasActivePlayers()) {
            throw new GameException.NoActivePlayersException();
        }
        return players.get(currentPlayerIndex);
    }

    @Override
    public void nextTurn() {
        if (players.isEmpty()) {
            return;
        }

        if (!hasActivePlayers()) {
            return;
        }

        int startingIndex = currentPlayerIndex;
        int attempts = 0;

        // Cerca il prossimo giocatore attivo
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            attempts++;

            // Se abbiamo fatto un giro completo, incrementa il contatore dei turni
            if (currentPlayerIndex == 0) {
                turnCount++;
            }

            // Se abbiamo provato tutti i giocatori e nessuno è attivo, termina
            if (attempts >= players.size()) {
                break;
            }
        } while (!isCurrentPlayerActive() && currentPlayerIndex != startingIndex);

        // Se non abbiamo trovato un giocatore attivo, torna all'indice iniziale
        if (!isCurrentPlayerActive()) {
            currentPlayerIndex = startingIndex;
        }
    }

    @Override
    public void reset() {
        currentPlayerIndex = 0;
        turnCount = 0;
    }

    @Override
    public int getTurnCount() {
        return turnCount;
    }

    @Override
    public boolean isCurrentPlayerActive() {
        return !players.isEmpty() && players.get(currentPlayerIndex).isActive();
    }

    @Override
    public void setPlayers(List<IPlayer> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("La lista dei giocatori non può essere null o vuota");
        }
        this.players = new ArrayList<>(players);
        reset();
    }

    /**
     * Verifica se ci sono ancora giocatori attivi nel gioco.
     * @return true se almeno un giocatore è ancora attivo
     */
    private boolean hasActivePlayers() {
        return players.stream().anyMatch(IPlayer::isActive);
    }
}