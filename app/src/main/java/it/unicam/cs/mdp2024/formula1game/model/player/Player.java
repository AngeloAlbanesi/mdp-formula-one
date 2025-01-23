package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.car.ICar;

/**
 * Abstract base class for players in the Formula 1 game.
 * Implements common functionality shared between human and bot players.
 */
public abstract class Player implements IPlayer {
    
    private final String name;
    private ICar car;
    private boolean active;
    private boolean hasFinishedRace;

    /**
     * Creates a new player with the specified name.
     *
     * @param name the name of the player
     */
    protected Player(String name) {
        this.name = name;
        this.active = true;
        this.hasFinishedRace = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ICar getCar() {
        return car;
    }

    @Override
    public void setCar(ICar car) {
        this.car = car;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean hasFinished() {
        return hasFinishedRace;
    }

    /**
     * Sets the finished state of the player.
     * Should be called when the player reaches the finish line.
     *
     * @param hasFinishedRace the finished state to set
     */
    protected void setFinished(boolean hasFinishedRace) {
        this.hasFinishedRace = hasFinishedRace;
    }
}
