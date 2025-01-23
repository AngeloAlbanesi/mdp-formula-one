package it.unicam.cs.mdp2024.formula1game.model.car;

import it.unicam.cs.mdp2024.formula1game.model.util.Acceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IAcceleration;
import it.unicam.cs.mdp2024.formula1game.model.util.IPosition;
import it.unicam.cs.mdp2024.formula1game.model.util.IVelocity;
import it.unicam.cs.mdp2024.formula1game.model.util.Position;
import it.unicam.cs.mdp2024.formula1game.model.util.Velocity;

public class Car implements ICar {
    private IPosition position;
    private IVelocity velocity;
    private IAcceleration acceleration;

    public Car(IPosition position, IVelocity velocity, IAcceleration acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    @Override
    public IPosition getPosition() {
        return position;
    }

    @Override
    public IVelocity getVelocity() {
        return velocity;
    }

    @Override
    public IAcceleration getAcceleration() {
        return acceleration;
    }

    @Override
    public void setPosition(IPosition position) {
        this.position = position;
    }

    @Override
    public void setVelocity(IVelocity velocity) {
        this.velocity = velocity;
    }

    @Override
    public void setAcceleration(IAcceleration acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public void move() {
        // Update velocity by adding acceleration
        velocity = velocity.addAcceleration(acceleration);

        // Update position using nextPosition
        position = position.nextPosition(velocity);
    }

}
