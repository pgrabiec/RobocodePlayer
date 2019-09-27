package iwum;

import burlap.mdp.core.state.State;
import burlap.statehashing.HashableState;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RoboState implements State, HashableState {
    private boolean terminal;
    private double myEnergy;
    private int shotsHit;

    private double bearingFiff; // difference between enemy bearing and my gun heading
    private double enemyDistance; // distance between me and enemy
    private double enemyVelocity;
    private double enemyHeading; // direction of the enemy


    public RoboState(boolean terminal, double myEnergy, int shotsHit, double bearingFiff, double enemyDistance, double enemyVelocity, double enemyHeading) {
        this.terminal = terminal;
        this.myEnergy = myEnergy;
        this.shotsHit = shotsHit;
        this.bearingFiff = bearingFiff;
        this.enemyDistance = enemyDistance;
        this.enemyVelocity = enemyVelocity;
        this.enemyHeading = enemyHeading;
    }

    @Override
    public State s() {
        return this;
    }

    @Override
    public List<Object> variableKeys() {
        return Arrays.asList(
                "bearingFiff",
                "enemyDistance",
                "enemyVelocity",
                "enemyHeading"
        );
    }

    @Override
    public Object get(Object variableKey) {
        if ("bearingFiff".equals(variableKey)) {
            return bearingFiff;
        } else if ("enemyDistance".equals(variableKey)) {
            return enemyDistance;
        } else if ("enemyVelocity".equals(variableKey)) {
            return enemyVelocity;
        } else if ("enemyHeading".equals(variableKey)) {
            return enemyHeading;
        }
        throw new IllegalArgumentException("No such variable key: " + variableKey);
    }

    @Override
    public RoboState copy() {
        return new RoboState(terminal, myEnergy, shotsHit, bearingFiff, enemyDistance, enemyVelocity, enemyHeading);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoboState roboState = (RoboState) o;
        return terminal == roboState.terminal &&
                Double.compare(roboState.myEnergy, myEnergy) == 0 &&
                shotsHit == roboState.shotsHit &&
                Double.compare(roboState.bearingFiff, bearingFiff) == 0 &&
                Double.compare(roboState.enemyDistance, enemyDistance) == 0 &&
                Double.compare(roboState.enemyVelocity, enemyVelocity) == 0 &&
                Double.compare(roboState.enemyHeading, enemyHeading) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminal, myEnergy, shotsHit, bearingFiff, enemyDistance, enemyVelocity, enemyHeading);
    }

    public boolean isTerminal() {
        return terminal;
    }

    public double getMyEnergy() {
        return myEnergy;
    }

    public int getShotsHit() {
        return shotsHit;
    }
}