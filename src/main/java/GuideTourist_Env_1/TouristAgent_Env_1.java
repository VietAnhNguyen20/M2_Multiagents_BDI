package GuideTourist_Env_1;



import jade.core.Agent;
import jade.core.AID;

import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class TouristAgent_Env_1 extends Agent {
    private AID GuiderAgent;
    protected void setup() {
        System.out.println("TOURIST: Tourist-agent " + getAID().getName() + " has joined the Waiting Room.");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("tourist");
        sd.setName("JADE-tourist");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}

