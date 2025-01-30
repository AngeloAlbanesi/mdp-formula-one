package it.unicam.cs.mdp2024.formula1game.model.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayerLoader implements IPlayerLoader {

    public List<IPlayer> loadPlayers(String filePath) throws IOException, InvalidPlayerFormatException {
        List<IPlayer> players = new ArrayList<>();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.startsWith("#") || line.trim().isEmpty())
                    continue;

                String[] parts = line.split(";");
                if (parts.length != 3) {
                    throw new InvalidPlayerFormatException("Formato riga non valido", lineNumber);
                }

                String type = parts[0].trim();
                String name = parts[1].trim();
                String color = parts[2].trim();

                if (!isValidColor(color)) {
                    throw new InvalidPlayerFormatException("Formato colore non valido", lineNumber);
                }

                players.add(createPlayer(type, name, color));
            }
        }
        return players;
    }

    private IPlayer createPlayer(String type, String name, String color) {
        return switch (type.toLowerCase()) {
            case "bot" -> new BotPlayer(name, color);
            default -> throw new IllegalArgumentException("Tipo giocatore non supportato: " + type);
        };
    }

    private boolean isValidColor(String hexColor) {
        return hexColor.matches("^[0-9A-Fa-f]{6}$");
    }
}