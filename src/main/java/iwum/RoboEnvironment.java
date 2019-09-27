package iwum;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;

public class RoboEnvironment implements Environment {
    private final MlRobot robot;

    public RoboEnvironment(MlRobot robot) {
        this.robot = robot;
    }

    @Override
    public State currentObservation() {
        return robot.getCurrentState();
    }

    @Override
    public double lastReward() {
        final RoboState state = robot.getCurrentState();
        return stateReward(state);
    }

    @Override
    public EnvironmentOutcome executeAction(Action a) {
        try {
            return robot.executeAction(a);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isInTerminalState() {
        return robot.getCurrentState().isTerminal();
    }

    @Override
    public void resetEnvironment() {
        throw new UnsupportedOperationException("resetEnvironment not supported");
    }

    public static double stateReward(RoboState state) {
        return state.getMyEnergy() * 0.2 + (double) state.getShotsHit();
    }
}