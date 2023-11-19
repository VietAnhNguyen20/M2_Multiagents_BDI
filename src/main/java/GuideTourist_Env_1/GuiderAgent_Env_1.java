package GuideTourist_Env_1;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class GuiderAgent_Env_1 extends Agent {
    private static final long serialVersionUID = 1L;
    int numberOfRooms = 6;
    int[] sequencesOfVisiting = {1, 2, 3, 4, 5, 6};
    private AID[] touristAgents;
    int currentRoomIndex = 0;

    protected void setup() {
        System.out.println("GUIDE: Hello! Guider-agent " + getAID().getName() + " has entered the Waiting Room.");

        System.out.print("GUIDE: The order of rooms visited today: {");
        for (int i = 0; i < sequencesOfVisiting.length; i++) {
            System.out.print(sequencesOfVisiting[i]);
            if (i < sequencesOfVisiting.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("}");

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("guider");
        sd.setName("JADE-guider");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        DFAgentDescription tourist_template = new DFAgentDescription();
        ServiceDescription tourist_sd = new ServiceDescription();
        tourist_sd.setType("tourist");
        tourist_template.addServices(tourist_sd);
        try {
            DFAgentDescription[] result = DFService.search(this, tourist_template);
            System.out.print("GUIDE: Found the following tourist agents:");
            touristAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                touristAgents[i] = result[i].getName();
                System.out.print(touristAgents[i].getName() + "  ;  ");
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("\nGUIDE: The trip now begins");

        // Agent initialization code
        addBehaviour(new TickerBehaviour(this, 10000) {
            public void onTick() {
                int currentRoom = sequencesOfVisiting[currentRoomIndex];

                // Receive tour requests from Touristic Agents
                ACLMessage request = receive();
                if (request != null) {
                    // Process the tour request
                    System.out.println("GUIDE: Received tour request from " + request.getSender().getName() + ": " + request.getContent());

                    // Respond to the tour request
                    ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                    response.setContent("PRESENTATION: Tour details for Room " + currentRoom);
                    response.addReceiver(request.getSender());
                    send(response);
                } else {
                    // No request received yet
                    System.out.println("No additional request received for Room " + currentRoom + ". Moving to the next room.");
                }

                // Move to the next room
                currentRoomIndex = (currentRoomIndex + 1) % numberOfRooms;

                if (currentRoomIndex == 0) {
                    System.out.println("GUIDE: The trip has ended!");
                    doDelete();
                }
            }
        });
    }



    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Printout a dismissal message
        System.out.println("GUIDE: GuiderAgent "+getAID().getName()+" has finished the tour");
    }
}
