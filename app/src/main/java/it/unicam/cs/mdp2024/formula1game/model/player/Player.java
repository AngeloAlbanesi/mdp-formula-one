package it.unicam.cs.mdp2024.formula1game.model.player;

import it.unicam.cs.mdp2024.formula1game.model.car.ICar;

/**
 * Abstract base class for players in the Formula 1 game.
 * Implements common functionality shared between human and bot players.
 */
public abstract class Player implements IPlayer {
    
    private final String name;
    private final String color;
    private ICar car;
    private boolean active;
    private boolean hasFinishedRace;

    /**
     * Creates a new player with the specified name.
     *
     * @param name the name of the player
     */
    protected Player(String name, String color) {
        this.name = name;
        this.color = color;
        this.active = true;
        this.hasFinishedRace = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ICar getCar() {
        return this.car;
    }

    @Override
    public void setCar(ICar car) {
        this.car = car;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean hasFinished() {
        return this.hasFinishedRace;
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

    @Override
    public String getColor() {
        return this.color;
    }

    @Override
    public void setColor(String color) {
        throw new UnsupportedOperationException("Color cannot be changed after player creation");
    }
}
