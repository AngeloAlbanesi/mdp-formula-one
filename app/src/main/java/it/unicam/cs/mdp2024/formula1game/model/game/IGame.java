package it.unicam.cs.mdp2024.formula1game.model.game;

import java.util.List;
import it.unicam.cs.mdp2024.formula1game.model.player.IPlayer;
import it.unicam.cs.mdp2024.formula1game.model.circuit.ICircuit;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;

/**
 * Interface representing the Formula 1 game engine.
 * Manages game state, player turns, and enforces game rules including:
 * - 8-neighbor movement rules
 * - Collision detection
 * - Race completion conditions
 */
public interface IGame {

    void initializePlayers();

    /**
     * Initializes and starts a new game with the given players and circuit.
     * Places players at their starting positions and initializes their cars.
     *
     * @param players the list of players participating in the race
     * @param circuit the racing circuit for the game
     */
    void initializeGame(List<IPlayer> players, ICircuit circuit);

    /**
     * Executes a complete turn for the current player:
     * 1. Gets acceleration choice from current player
     * 2. Validates the acceleration against game rules (8-neighbor, max change)
     * 3. Applies the acceleration and updates car position
     * 4. Checks for collisions and finish line
     * 5. Updates player status (active/finished)
     * 6. Moves to next player
     * 
     * @return true if the turn was executed successfully, false if game is over
     */
    boolean executeTurn();

    /**
     * Returns the current player whose turn it is.
     *
     * @return the current player
     */
    IPlayer getCurrentPlayer();

    /**
     * Returns all players in the race.
     *
     * @return list of all players
     */
    List<IPlayer> getPlayers();

    /**
     * Returns the circuit being used for the race.
     *
     * @return the current circuit
     */
    ICircuit getCircuit();

    /**
     * Validates a potential move based on:
     * - 8-neighbor rule compliance
     * - No wall collisions
     * - Within circuit bounds
     * 
     * @param position     the position to validate
     * @param acceleration the proposed acceleration
     * @return true if the move is valid, false otherwise
     */
    boolean isValidMove(IPosition position, IAcceleration acceleration);

    /**
     * Returns the number of completed turns.
     *
     * @return the current turn count
     */
    int getTurnCount();

    /**
     * Checks if the game is over (someone won or all players are inactive).
     *
     * @return true if the game is over, false if it's still ongoing
     */
    boolean isGameOver();

    /**
     * Returns the winner of the game, if any.
     *
     * @return the winning player, or null if there's no winner yet
     *         or all players crashed
     */
    IPlayer getWinner();

    /**
     * Returns the current game state as a string representation.
     * Useful for debugging or displaying game status.
     *
     * @return string representation of the current game state
     */
    String getGameState();
}
