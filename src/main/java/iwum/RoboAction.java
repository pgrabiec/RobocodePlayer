package iwum;

import burlap.mdp.core.action.Action;

public enum RoboAction implements Action {
    TURRENT_LEFT_1(2.0), TURRENT_LEFT_2(10.0), TURRENT_LEFT_3(20.0), TURRENT_LEFT_4(5.0),
    TURRENT_RIGHT_1(2.0), TURRENT_RIGHT_2(10.0), TURRENT_RIGHT_3(20.0), TURRENT_RIGHT_4(5.0),
    FIRE_1(1.0), FIRE_2(2.0), FIRE_3(3.0);

    RoboAction(double amount) {
        this.amount = amount;
    }

    private double amount;

    public double getAmount() {
        return amount;
    }

    @Override
    public String actionName() {
        return this.name();
    }

    @Override
    public Action copy() {
        return this;
    }
}
