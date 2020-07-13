package agents;

import TF.Functions;
import TF.HostAgent;
import TF.Position;
import classes.Antibody;
import static classes.Cell.StatusCell.GOINGTO;
import static classes.Cell.StatusCell.MOVING;

import classes.Cell;
import classes.NCell;
import classes.Virus;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.Random;
import java.lang.*;

public class TCellAgent extends CellAgent {
    public int matados = 0;

    @Override
    protected void setup() {
        super.setup();
        parallel.addSubBehaviour(new TickerBehaviour(this, 4000) {
            @Override
            protected void onTick() {
                if (getLocal().status == MOVING && matados == 0){
                    getLocal().status = GOINGTO;
                    Random aleatorio = new Random(System.currentTimeMillis());
                    int posicionx;
                    int posiciony;
                    do {
                        posicionx = aleatorio.nextInt(1325);
                        posiciony = 100+aleatorio.nextInt(400);
                    } while (Math.abs(getLocal().position.getX()-posicionx) < 200);
                    getLocal().setGoal(new Position(posicionx, posiciony));
                }
                if (matados > 0){
                    matados = 0;
                }
            }
        });
    }

    @Override
    protected void computeStatus() {
        /*if(getLocal() == null)
            System.out.println("=======>" + getLocalName());*/
        switch(getLocal().status) {
            case MOVING:
                getLocal().movementRandom();
                patrullar();
                break;
            /*case INFECTED:
                break;*/
            case ATTACKING:
                break;
            case GOINGTO:
                getLocal().goToGoal();
                patrullar();
                if(getLocal().hasArrived())
                    getLocal().status = MOVING;
                break;
            /*case DEAD:
                break;*/
        }
    }

    @Override
    protected void computeMessage(ACLMessage msg) { }

    public void patrullar(){
        for (Map.Entry<String, NCell> entry : NCell.getNormalCells().entrySet()){
            NCell ncell = entry.getValue();
            if (Functions.isClose(getLocal().position, ncell.position) && ncell.status==NCell.StatusCell.INFECTED){
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("die");
                msg.addReceiver(new AID(entry.getValue().getName(), AID.ISLOCALNAME));
                send(msg);
                if (HostAgent.number_of_I_cells > 0 && HostAgent.number_of_Virus > 0){
                    HostAgent.number_of_I_cells -= 1;
                    HostAgent.number_of_Virus -= 1;
                }
                matados+=1;
                break;
            }
        }
    }
}