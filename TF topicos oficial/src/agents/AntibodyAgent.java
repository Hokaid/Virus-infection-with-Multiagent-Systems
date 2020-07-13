package agents;

import TF.Functions;
import TF.HostAgent;
import TF.Position;
import classes.*;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.Random;

import static classes.Antibody.StatusAntibody.GOINGTO;
import static classes.Antibody.StatusAntibody.MOVING;

public class AntibodyAgent extends EntityAgent {
    public int matados = 0;
    public Antibody getLocal() { return Antibody.getLocal(getLocalName()); }

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
        switch(getLocal().status) {
            case MOVING:
                getLocal().movementRandom();
                patrullar();
                break;
            case GOINGTO:
                getLocal().goToGoal();
                patrullar();
                if(getLocal().hasArrived())
                    getLocal().status = MOVING;
                break;
        }
    }

    @Override
    protected void computeMessage(ACLMessage msg) { }

    public void patrullar(){
        for (Map.Entry<String, Virus> entry : Virus.getActiveVirus().entrySet()){
            Virus virus = entry.getValue();
            if (Functions.isClose(getLocal().position, virus.position)){
                //Mensage 1
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("die");
                msg.addReceiver(new AID(entry.getValue().getName(), AID.ISLOCALNAME));
                send(msg);
                //Mensage 2
                ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                String msg_pos = getLocal().position.getX()+","+getLocal().position.getY();
                msg2.setContent(msg_pos);
                for(Map.Entry<String,BCell> receiver : BCell.getBCells().entrySet())
                    msg2.addReceiver(new AID(receiver.getValue().getName(), AID.ISLOCALNAME));
                send(msg2);
                //
                matados += 1;
                break;
            }
        }
    }
}