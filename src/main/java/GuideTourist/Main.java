package GuideTourist;



import jade.core.Profile;

import jade.core.ProfileImpl;

import jade.wrapper.AgentContainer;

import jade.wrapper.AgentController;



public class Main {



    public static void main(String[] args) {

        // Créer un conteneur JADE 

        jade.core.Runtime runtime = jade.core.Runtime.instance();

        Profile profile = new ProfileImpl();

        AgentContainer mainContainer = runtime.createMainContainer(profile);



        try {

            // Démarrer l'agent guide 

            AgentController guideController = mainContainer.createNewAgent("AgentGuide", AgentGuide.class.getName(), null);

            guideController.start();



            // Attendre un court instant pour laisser l'agent guide initialiser 

            Thread.sleep(1000);



            // Démarrer l'agent touriste 

            AgentController touristController = mainContainer.createNewAgent("AgentTouristique", AgentTouristique.class.getName(), null);

            touristController.start();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

} 