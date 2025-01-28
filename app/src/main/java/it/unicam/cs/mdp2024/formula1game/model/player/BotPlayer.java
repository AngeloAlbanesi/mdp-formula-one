package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;

/**
 * Represents a bot player in the Formula 1 game.
 * Bot players make moves automatically using AI algorithms.
 */
public class BotPlayer extends Player {

    /**
     * Creates a new bot player with the specified name.
     *
     * @param name the name of the bot player
     */
    public BotPlayer(String name, String color) {
        super(name, color);

    }

    @Override
    public IAcceleration chooseAcceleration() {
        // TODO: Implement bot player logic
        // This will be implemented later using A* algorithm
        // For now, return null as a placeholder
        return null;
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
