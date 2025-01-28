package it.unicam.cs.mdp2024.formula1game.model.player;

import java.io.IOException;
import java.util.List;

public interface IPlayerLoader {
    List<IPlayer> loadPlayers(String filePath) throws IOException, InvalidPlayerFormatException;
}