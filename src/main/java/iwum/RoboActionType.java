package iwum;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;

import java.util.Arrays;
import java.util.List;

public class RoboActionType implements ActionType {
    @Override
    public String typeName() {
        return "robo-action-type";
    }

    @Override
    public Action associatedAction(String strRep) {
        return RoboAction.valueOf(strRep);
    }

    @Override
    public List<Action> allApplicableActions(State s) {
        return Arrays.asList(RoboAction.values());
    }
}
