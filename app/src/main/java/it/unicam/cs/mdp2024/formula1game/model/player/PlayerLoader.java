package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.game.Game2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayerLoader implements IPlayerLoader {

    @Override
    public List<IPlayer> loadPlayers(String filePath, Game2 game) throws IOException, InvalidPlayerFormatException {
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
                if (parts.length != 4) {
                    throw new InvalidPlayerFormatException("Il formato richiede 4 campi: tipo;nome;colore;strategia", lineNumber);
                }

                String type = parts[0].trim();
                String name = parts[1].trim();
                String color = parts[2].trim();
                String strategyStr = parts[3].trim();

                if (!isValidColor(color)) {
                    throw new InvalidPlayerFormatException("Formato colore non valido", lineNumber);
                }

                int strategy;
                try {
                    strategy = Integer.parseInt(strategyStr);
                    if (strategy < 1 || strategy > 2) {
                        throw new InvalidPlayerFormatException("Codice strategia non valido (deve essere 1 o 2)", lineNumber);
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidPlayerFormatException("Codice strategia deve essere un numero", lineNumber);
                }

                players.add(createPlayer(type, name, color, strategy, game));
            }
        }
        return players;
    }

    private IPlayer createPlayer(String type, String name, String color, int strategy, Game2 game) {
        return switch (type.toLowerCase()) {
            case "bot" -> {
                BotPlayer bot = new BotPlayer(name, color, strategy);
                bot.initializeGameDependencies(game.getCircuit(), game.getMoveValidator(), game.getCheckpointManager());
                yield bot;
            }
            default -> throw new IllegalArgumentException("Tipo giocatore non supportato: " + type);
        };
    }

    private boolean isValidColor(String hexColor) {
        return hexColor.matches("^[0-9A-Fa-f]{6}$");
    }
}