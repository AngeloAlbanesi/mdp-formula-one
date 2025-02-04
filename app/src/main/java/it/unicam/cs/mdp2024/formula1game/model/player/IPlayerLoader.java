package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.game.Game2;
import java.io.IOException;
import java.util.List;

public interface IPlayerLoader {
    /**
     * Carica la lista dei giocatori dal file specificato.
     * 
     * @param filePath il percorso del file contenente i giocatori
     * @param game il gioco per cui caricare i giocatori
     * @return la lista dei giocatori caricati
     * @throws IOException in caso di errori di I/O
     * @throws InvalidPlayerFormatException se il formato del file non è valido
     */
    List<IPlayer> loadPlayers(String filePath, Game2 game) throws IOException, InvalidPlayerFormatException;
}