package agents;

import TF.Functions;
import TF.HostAgent;
import TF.Position;
import classes.Antibody;
import classes.BCell;
import classes.Virus;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.Random;

import static classes.Cell.StatusCell.GOINGTO;
import static classes.Cell.StatusCell.MOVING;
import static classes.Cell.StatusCell.*;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;

public class BCellAgent extends CellAgent {

    @Override
    protected void setup() {
        super.setup();
        parallel.addSubBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                if(HostAgent.INFECTION) {
                    for (int i = 0; i < 5; i++) {
                        Antibody a = new Antibody();
                        a.position = new Position(getLocal().position.getX(),getLocal().position.getY());
                        a.start();
                        HostAgent.number_of_Antibodies += 1;
                    }
                }
            }
        });
    }

    @Override
    protected void computeStatus() {
        switch(getLocal().status) {
            case MOVING: getLocal().movementRandom();
                break;
            case GOINGTO:
                getLocal().goToGoal();
                if(getLocal().hasArrived())
                    getLocal().status = MOVING;
                break;
            default:
                break;
        }
    }

    @Override
    protected void computeMessage(ACLMessage msg) {
        if (getLocal().status == MOVING) {
            switch(msg.getPerformative()){
                case INFORM:
                    if (!msg.getContent().equals("infection")) {
                        String string = msg.getContent();
                        String[] parts = string.split(",");
                        int posx = Integer.parseInt(parts[0]);
                        int posy = Integer.parseInt(parts[1]);
                        SeguirAntibody(posx, posy);
                        break;
                    }
            }
        }
    }

    public void SeguirAntibody(int posx, int posy){
        parallel.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                if (getLocal().status == MOVING){
                    getLocal().status = GOINGTO;
                    getLocal().setGoal(new Position(posx, posy));
                }
            }
        });
    }
}