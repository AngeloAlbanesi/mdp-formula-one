package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;

/**
 * Represents a human player in the Formula 1 game.
 * Human players make moves based on user input.
 */
public class HumanPlayer extends Player {

    /**
     * Creates a new human player with the specified name.
     *
     * @param name the name of the player
     */
    public HumanPlayer(String name, String color) {
        super(name, color);
        
    }

    @Override
    public IAcceleration chooseAcceleration() {
        // TODO: Implement human player logic
        // This will be implemented later to handle user input
        // For now, return null as a placeholder
        return null;
    }

    @Override
    public boolean isBot() {
        return false;
    }
}
