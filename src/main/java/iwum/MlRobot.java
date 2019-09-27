package iwum;

import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.statehashing.HashableStateFactory;
import robocode.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MlRobot extends AdvancedRobot {
    private static LearningAgent LEARNING_AGENT;
    private final Lock environmentOutcomeLock = new ReentrantLock();
    private final Condition newEnvironmentOutcomeCondition = environmentOutcomeLock.newCondition();

    private final Lock actionLock = new ReentrantLock();
    private final Condition newActionCondition = actionLock.newCondition();

    private RoboState currentState;
    private AtomicInteger shotsHit = new AtomicInteger(0);

    private RoboAction action;


    static {
        SADomain domain = new SADomain()
                .setActionTypes(Collections.singletonList(new RoboActionType()));
        HashableStateFactory hashableStateFactory = (State s) -> (RoboState) s;
        double gamma = 0.1;
        double qInit = 1.0;
        double learningRate = 0.01;
        double lambda = 0.3;
        LEARNING_AGENT = new SarsaLam(domain, gamma, hashableStateFactory, qInit, learningRate, lambda);
    }

    @Override
    public void run() {
        setAdjustRadarForGunTurn(false);
        setRadarColor(Color.RED);
        setScanColor(Color.RED);

        this.shotsHit.set(0);
        this.currentState = new RoboState(false, getEnergy(), this.shotsHit.get(), 180.0, 0.0, 0.0, 0.0);
        final RoboEnvironment environment = new RoboEnvironment(this);

        new Thread(() -> LEARNING_AGENT.runLearningEpisode(environment)).start();

        while (true) {
            scan();
            turnGunRight(200);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        setNewState(new RoboState(
                false,
                getEnergy(),
                shotsHit.get(),
                e.getBearing() - getGunHeading(),
                e.getDistance(),
                e.getVelocity(),
                e.getHeading()
        ));
        try {
            awaitAndExecuteAction();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        if (!getName().equals(event.getName())) {
            shotsHit.incrementAndGet();
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        setNewState(new RoboState(
                true,
                getEnergy(),
                shotsHit.get(), 180.0, 0.0, 0.0, 0.0
        ));
    }

    public EnvironmentOutcome executeAction(Action a) throws InterruptedException {
        final RoboState beforeState = getCurrentState();
        final double beforeReward = RoboEnvironment.stateReward(beforeState);

        final RoboAction action = (RoboAction) a;

        dispatchAction(action);

        final RoboState afterState = awaitNewState();
        final double afterReward = RoboEnvironment.stateReward(afterState);
        final double rewardDiff = afterReward - beforeReward;
        return new EnvironmentOutcome(
                beforeState,
                action,
                afterState,
                rewardDiff,
                afterState.isTerminal()
        );
    }

    private void executeAction(RoboAction action) {
        stop();
        switch (action) {
            case TURRENT_LEFT_1:
                turnGunLeft(action.getAmount());
                break;
            case TURRENT_LEFT_2:
                turnGunLeft(action.getAmount());
                break;
            case TURRENT_LEFT_3:
                turnGunLeft(action.getAmount());
                break;
            case TURRENT_LEFT_4:
                turnGunLeft(action.getAmount());
                break;
            case TURRENT_RIGHT_1:
                turnGunRight(action.getAmount());
                break;
            case TURRENT_RIGHT_2:
                turnGunRight(action.getAmount());
                break;
            case TURRENT_RIGHT_3:
                turnGunRight(action.getAmount());
                break;
            case TURRENT_RIGHT_4:
                turnGunRight(action.getAmount());
                break;
            case FIRE_1:
                fire(action.getAmount());
                break;
            case FIRE_2:
                fire(action.getAmount());
                break;
            case FIRE_3:
                fire(action.getAmount());
                break;
            default:
                resume();
                throw new IllegalArgumentException("Invalid action: " + action);
        }
        resume();
    }

    private void awaitAndExecuteAction() throws InterruptedException {
        actionLock.lock();
        try {
            newActionCondition.await(50, TimeUnit.MILLISECONDS);
            final RoboAction action = this.action;
            if (action != null) {
                this.action = null;
                executeAction(action);
            }
        } finally {
            actionLock.unlock();
        }
    }

    private void dispatchAction(RoboAction action) {
        actionLock.lock();
        try {
            this.action = action;
            newActionCondition.signalAll();
        } finally {
            actionLock.unlock();
        }
    }

    public RoboState getCurrentState() {
        environmentOutcomeLock.lock();
        try {
            return this.currentState;
        } finally {
            environmentOutcomeLock.unlock();
        }
    }

    private void setNewState(RoboState newState) {
        if (newState.isTerminal()) {
            writeData(RoboEnvironment.stateReward(newState));
        }
        environmentOutcomeLock.lock();
        try {
            this.currentState = newState;
            newEnvironmentOutcomeCondition.signalAll();
        } finally {
            environmentOutcomeLock.unlock();
        }
    }

    private RoboState awaitNewState() throws InterruptedException {
        environmentOutcomeLock.lock();
        try {
            newEnvironmentOutcomeCondition.await();
            return this.currentState;
        } finally {
            environmentOutcomeLock.unlock();
        }
    }

    private void writeData(double reward) {
        try {
            File file = getDataFile("results.csv");
            final Writer writer = new RobocodeFileWriter(file.getPath(), true);
            writer.write(String.format("%s\n", reward));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
