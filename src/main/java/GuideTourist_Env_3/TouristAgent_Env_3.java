package GuideTourist_Env_3;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Arrays;
import java.util.*;

public class TouristAgent_Env_3 extends Agent {
    private int numberOfRooms = 6;
    private boolean AlreadyProposedSequencesOfVisits = false;

    int[] preferredOrder;

    protected void setup() {
        System.out.println("TOURIST: Tourist-agent " + getAID().getName() + " has joined the Waiting Room.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            // Create an array to store the preferred order
            preferredOrder = new int[args.length];

            // Loop through all arguments
            for (int i = 0; i < args.length; i++) {
                // Convert each argument to an integer and store it in the preferredOrder array
                preferredOrder[i] = Integer.parseInt(args[i].toString());
            }
        } else {
            // Default preferred order if no arguments are provided
            preferredOrder = new int[]{1, 2, 3, 4, 5, 6};
        }

        // Register the tourist agent in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("tourist");
        sd.setName("JADE-tourist");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        DFAgentDescription guider_template = new DFAgentDescription();
        ServiceDescription guider_sd = new ServiceDescription();
        guider_sd.setType("guider");
        guider_template.addServices(guider_sd);
        // Add behavior to handle CFPs
        addBehaviour(new ProposeSequenceOfVisits());

        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                try {DFAgentDescription[] guiderResult = DFService.search(myAgent, guider_template);
                    boolean guidersPresent = guiderResult.length > 0;

                    if (guidersPresent) {
                        boolean rand = new Random().nextInt(100) <= 2;
                        if(rand){
                            System.out.println("TOURIST "+ myAgent.getName() + " skips the listening and moves to the next room");
                            sendRequestForAdditionalInformation(false);
                        }
                        else{
                            sendRequestForAdditionalInformation(null);
                        }
                    }}
                catch (FIPAException e) {
                    e.printStackTrace();
                }
            }

            private void sendRequestForAdditionalInformation(Boolean bool_value) {
                // Check if there's a CFP message in the queue
                ACLMessage cfp = receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));

                if (cfp != null) {
                    if (bool_value==null) {
                        ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                        // Create a proposal message (CFP)
                        boolean additionalInformationRequested = new Random().nextInt(100) <= 50;
                        response.setContent(String.valueOf(additionalInformationRequested));
                        response.addReceiver(cfp.getSender()); // Replace with the actual AID of the guider agent
                        send(response);
                    }
                    else {
                        ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                        // Create a proposal message (CFP)
                        response.setContent(String.valueOf(bool_value));
                        response.addReceiver(cfp.getSender()); // Replace with the actual AID of the guider agent
                        send(response);

                    }
                }
            }
        });

    }

    private class ProposeSequenceOfVisits extends CyclicBehaviour {
        public void action() {
            // Check if the tourist has already responded to the CFP
            if (!AlreadyProposedSequencesOfVisits) {
                // Receive CFP from the guider agent
                ACLMessage cfp = receive(MessageTemplate.MatchPerformative(ACLMessage.CFP));

                if (cfp != null) {

                    // Respond with the generated sequence as a comma-separated string
                    ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                    response.setContent(String.join(",", Arrays.stream(preferredOrder)
                            .mapToObj(String::valueOf)
                            .toArray(String[]::new)));
                    response.addReceiver(cfp.getSender());
                    send(response);

                    // Set the flag to indicate that the tourist has responded to the CFP
                    System.out.println("TOURIST:" + getAID().getName() + " proposes sequence of visiting: " + response.getContent());
                    AlreadyProposedSequencesOfVisits = true;
                } else {
                    // If no CFP is received, block the behavior
                    block();
                }
            }
        }
    }
}

