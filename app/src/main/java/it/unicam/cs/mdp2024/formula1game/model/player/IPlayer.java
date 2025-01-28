package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.car.ICar;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;

/**
 * Interface representing a player in the Formula 1 game.
 * Each player controls a car and can be either human or bot.
 */
public interface IPlayer {

    /**
     * Returns the player's name.
     *
     * @return the name of the player
     */
    String getName();

    /**
     * Returns the car controlled by the player.
     *
     * @return the player's car
     */
    ICar getCar();

    /**
     * Sets the car controlled by the player.
     *
     * @param car the car to be assigned to the player
     */
    void setCar(ICar car);

    /**
     * Chooses the next acceleration for the car following the 8-neighbor rule.
     * The player can:
     * 1. Move to any of the 8 adjacent cells
     * 2. Brake by reducing acceleration
     * 
     * The acceleration must be valid according to:
     * - Maximum allowed change per component
     * - Current velocity limitations
     * - 8-neighbor movement constraints
     *
     * @return the chosen acceleration for the next move, constrained by game rules
     */
    IAcceleration chooseAcceleration();

    /**
     * Checks if the player is a bot.
     *
     * @return true if the player is a bot, false if human
     */
    boolean isBot();

    /**
     * Checks if the player has completed the race.
     *
     * @return true if the player has reached the finish line, false otherwise
     */
    boolean hasFinished();

    /**
     * Checks if the player is currently active in the race.
     * An inactive player might have crashed, retired, or been disqualified.
     *
     * @return true if the player is active, false otherwise
     */
    boolean isActive();

    /**
     * Sets the active state of the player in the race.
     * Can be used to mark a player as inactive after a crash or disqualification.
     *
     * @param active the active state to set
     */
    void setActive(boolean active);

    /**
     * Returns the player's color in hexadecimal format.
     *
     * @return the player's color as a hexadecimal string (e.g., "FF0000" for red)
     */
    String getColor();

    /**
     * Sets the player's color.
     *
     * @param color the color in hexadecimal format (e.g., "FF0000" for red)
     */
    void setColor(String color);
}