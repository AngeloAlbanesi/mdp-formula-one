package it.unicam.cs.mdp2024.formula1game.model.car;

import it.unicam.cs.mdp2024.formula1game.model.util.Acceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.util.Velocity;

public interface ICar {
    // Get the current position of the car
    IPosition getPosition();

    // Get the current velocity of the car
    IVelocity getVelocity();

    // Get the current acceleration of the car
    IAcceleration getAcceleration();

    // Set the position of the car
    void setPosition(IPosition startPosition);

    // Set the velocity of the car
    void setVelocity(IVelocity velocity);

    // Set the acceleration of the car
    void setAcceleration(IAcceleration acceleration);

    // Move the car, updating its position and velocity
    void move();
}