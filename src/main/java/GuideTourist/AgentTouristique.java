package GuideTourist;



import jade.core.AID;

import jade.core.Agent;

import jade.core.behaviours.SimpleBehaviour;

import jade.lang.acl.ACLMessage;

import jade.lang.acl.MessageTemplate;



public class AgentTouristique extends Agent {



    private String currentPosition;

    private String expositionKnowledge;

    private String specificRequests;



    private boolean receiveInformation = false;

    private boolean exploreExhibition = false;

    private boolean followGuideExplanations = false;

    private boolean satisfied = false;



    protected void setup() {

        // Initialiser les croyances

        currentPosition = "StartingPoint";

        expositionKnowledge = "";

        specificRequests = "";



        // Ajouter le comportement pour l'agent touriste

        addBehaviour(new TouristBehaviour());

    }



    private class TouristBehaviour extends SimpleBehaviour {



        public void action() {

            // Implémentation du comportement de l'agent touriste

            if (!receiveInformation) {

                // L'agent touriste n'a pas encore reçu d'informations, il a l'intention de demander

                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);



                // Utilisez la méthode addReceiver avec une instance d'AID

                AID guideAgent = getGuideAgent();

                if (guideAgent != null) {

                    request.addReceiver(guideAgent);

                    request.setContent("Demande d'informations sur l'exposition");

                    send(request);



                    ACLMessage response = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

                    if (response != null) {

                        expositionKnowledge = response.getContent();

                        System.out.println("Agent touriste : J'ai reçu des informations de l'agent guide : " + expositionKnowledge);

                        receiveInformation = true;

                        exploreExhibition = true; // Après avoir reçu des informations, l'agent touriste veut explorer

                    } else {

                        block(); // Attendre les messages

                    }

                } else {

                    System.err.println("Erreur : Impossible de récupérer la référence de l'agent guide.");

                    doDelete(); // Terminer l'agent en cas d'erreur

                }

            } else if (exploreExhibition) {

                // L'agent touriste explore l'exposition

                System.out.println("Agent touriste : Exploration de l'exposition");



                // Vous pouvez ajouter la logique pour déterminer les mouvements dans l'exposition



                followGuideExplanations = true; // L'agent veut suivre les explications de l'agent guide

                exploreExhibition = false;

            } else if (followGuideExplanations) {

                // L'agent touriste suit les explications de l'agent guide

                System.out.println("Agent touriste : Je suis les explications de l'agent guide");

                // Vous pouvez ajouter la logique pour suivre les explications



                // Après avoir suivi les explications, l'agent peut avoir d'autres intentions

                satisfied = true;

                followGuideExplanations = false;

            }

        }



        public boolean done() {

            // L'agent touriste continue à agir jusqu'à ce que toutes ses intentions soient satisfaites

            return satisfied;

        }

    }



    // Méthode pour obtenir la référence de l'agent guide

    private AID getGuideAgent() {

        // Remplacez "AgentGuide" par le nom réel de votre agent guide

        AID guideAgent = new AID("AgentGuide", AID.ISLOCALNAME);

        guideAgent.addAddresses("http://localhost:1099/JADE");

        return guideAgent;

    }

}