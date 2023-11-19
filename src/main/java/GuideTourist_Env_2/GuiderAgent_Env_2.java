package GuideTourist_Env_2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

public class GuiderAgent_Env_2 extends Agent {
    private static final long serialVersionUID = 1L;
    int numberOfRooms = 6;
    int[] sequencesOfVisiting = {1, 2, 3, 4, 5, 6};
    private AID[] touristAgents;
    int currentRoomIndex = 0;

    protected void setup() {
        System.out.println("GUIDE: Hello! Guider-agent " + getAID().getName() + " has entered the Waiting Room.");

        System.out.print("GUIDE: The proposed order of rooms visited today: {");
        for (int i = 0; i < sequencesOfVisiting.length; i++) {
            System.out.print(sequencesOfVisiting[i]);
            if (i < sequencesOfVisiting.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("} by guider agent");

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
                System.out.print(touristAgents[i].getName() + " ; ");
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("\nGUIDE: The trip now begins. The guider now collect preferences of order of visiting from tourists");

        // Collect preference visits orders from the tourists

        addBehaviour(new CollectPreferenceVisitsOrder());



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

    public class CollectPreferenceVisitsOrder extends OneShotBehaviour {
        public void action() {
            DFAgentDescription tourist_template = new DFAgentDescription();
            ServiceDescription tourist_sd = new ServiceDescription();
            tourist_sd.setType("tourist");
            tourist_template.addServices(tourist_sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, tourist_template);

                // Collect preferences from each tourist
                List<int[]> preferencesList = new ArrayList<>();
                for (DFAgentDescription agent : result) {
                    AID touristAID = agent.getName();
                    int[] preferences = getTouristPreferences(touristAID);
                    preferencesList.add(preferences);
                }

                // Select common preferences
                int[] commonPreferences = getMajorityPreferences(preferencesList);

                System.out.println("Majority preferences: " + Arrays.toString(commonPreferences));
                sequencesOfVisiting = commonPreferences;

            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        private int[] getTouristPreferences(AID touristAID) {
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(touristAID);
            cfp.setContent("Please provide your preferences of visiting:");

            myAgent.send(cfp);

            // Wait for the response
            MessageTemplate template = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                    MessageTemplate.MatchSender(touristAID)
            );

            ACLMessage response = myAgent.blockingReceive(template);

            if (response != null) {
                // Parse the preferences from the response content
                String[] preferencesStr = response.getContent().split(",");
                int[] preferences = Arrays.stream(preferencesStr)
                        .mapToInt(Integer::parseInt)
                        .toArray();

                return preferences;
            } else {
                System.out.println("No preferences order of visits received from tourist " + touristAID.getName());

                return sequencesOfVisiting;

            }
        }


        private int[] getMajorityPreferences(List<int[]> preferencesList) {
            int sequenceLength = preferencesList.get(0).length; // Assuming all sequences have the same length
            int[] majorityPreferences = new int[sequenceLength];

            for (int i = 0; i < sequenceLength; i++) {
                final int position = i;

                // Count occurrences for the current position
                Map<Integer, Long> countByPosition = preferencesList.stream()
                        .map(sequence -> sequence[position])
                        .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

                // Find the preference with the maximum count for the current position,
                // excluding preferences that are already in the majorityPreferences array
                int majorityPreference = countByPosition.entrySet().stream()
                        .filter(entry -> !contains(majorityPreferences, entry.getKey()))
                        .max(Comparator.comparingLong(Map.Entry::getValue))
                        .map(Map.Entry::getKey)
                        .orElseThrow(); // Handle the case where there is no majority preference

                majorityPreferences[i] = majorityPreference;
            }

            System.out.println("Majority Preferences: " + Arrays.toString(majorityPreferences));

            return majorityPreferences;
        }

        private boolean contains(int[] array, int value) {
            return Arrays.stream(array).anyMatch(element -> element == value);
        }


    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        System.out.println("GUIDE: GuiderAgent "+getAID().getName()+" has finished the tour");
    }
}
