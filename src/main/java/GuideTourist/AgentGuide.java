package GuideTourist;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.Agent;


public class AgentGuide extends Agent {
    //Croyances:
    private String positionAgentA; //position initiale de l'agent
    private String positionTourists; //position des touristes
    private int numTourists; //nombre de touristes
    private String expositionKnowledge; // connaissance sur l'exosition
    private String touristsRequests; //les demandes des touristes
    private String currentRoom;

    //Desires:
    private boolean visitAllRooms = false; // visiter toutes les salles
    private boolean provideDetailedExplanations = false; //fournir des explications détaillés
    private boolean satisfyTourists = false; //Satisfaction des touristes

    //Intensions
    private boolean waitForGroup = false; //attendre un groupe de touristes
    private boolean retrieveGroup = false; //récuperer les touristes
    private boolean presentPaintings = false; // présenter les tableaux
    private boolean determineNextRoom = false; //Detereminer les salles libres
    private boolean finishVisit = false; //finir la viste

    // Getters
    public String getPositionAgentA() {
        return positionAgentA;
    }

    public String getPositionTourists() {
        return positionTourists;
    }

    public int getNumTourists() {
        return numTourists;
    }

    public String getExpositionKnowledge() {
        return expositionKnowledge;
    }

    public String getTouristsRequests() {
        return touristsRequests;
    }

    public boolean isVisitAllRooms() {
        return visitAllRooms;
    }

    public boolean isProvideDetailedExplanations() {
        return provideDetailedExplanations;
    }

    public boolean isSatisfyTourists() {
        return satisfyTourists;
    }

    // Setters
    public void setPositionAgentA(String positionAgentA) {
        this.positionAgentA = positionAgentA;
    }

    public void setPositionTourists(String positionTourists) {
        this.positionTourists = positionTourists;
    }

    public void setNumTourists(int numTourists) {
        this.numTourists = numTourists;
    }

    public void setExpositionKnowledge(String expositionKnowledge) {
        this.expositionKnowledge = expositionKnowledge;
    }

    public void setTouristsRequests(String touristsRequests) {
        this.touristsRequests = touristsRequests;
    }

    public void setVisitAllRooms(boolean visitAllRooms) {
        this.visitAllRooms = visitAllRooms;
    }

    public void setProvideDetailedExplanations(boolean provideDetailedExplanations) {
        this.provideDetailedExplanations = provideDetailedExplanations;
    }

    public void setSatisfyTourists(boolean satisfyTourists) {
        this.satisfyTourists = satisfyTourists;
    }

    // Getters pour les intentions
    public boolean isWaitForGroup() {
        return waitForGroup;
    }

    public boolean isRetrieveGroup() {
        return retrieveGroup;
    }

    public boolean isPresentPaintings() {
        return presentPaintings;
    }

    public boolean isDetermineNextRoom() {
        return determineNextRoom;
    }

    public boolean isFinishVisit() {
        return finishVisit;
    }

    // Setters pour les intentions
    public void setWaitForGroup(boolean waitForGroup) {
        this.waitForGroup = waitForGroup;
    }

    public void setRetrieveGroup(boolean retrieveGroup) {
        this.retrieveGroup = retrieveGroup;
    }

    public void setPresentPaintings(boolean presentPaintings) {
        this.presentPaintings = presentPaintings;
    }

    public void setDetermineNextRoom(boolean determineNextRoom) {
        this.determineNextRoom = determineNextRoom;
    }

    public void setFinishVisit(boolean finishVisit) {
        this.finishVisit = finishVisit;
    }


    protected void setup() {
        // Initialiser les croyances ici
        positionAgentA = "PointA";
        positionTourists = "StartingPoint";
        numTourists = 5; // Exemple : 5 touristes
        expositionKnowledge = "Connaissance sur l'exposition";
        touristsRequests = "";
        currentRoom = ""; // Initialisez la salle actuelle à une valeur par défaut

        // Le comportement cyclique
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // Mettez en œuvre le cycle BDI ici
                if (numTourists > 0) {
                    // Il y a des touristes, donc l'agent peut commencer à agir
                    if (!visitAllRooms && !waitForGroup) {
                        // L'agent n'a pas encore visité toutes les salles
                        determineNextRoom();
                    } else if (!provideDetailedExplanations && !retrieveGroup) {
                        // L'agent doit fournir des explications détaillées
                        presentPaintings();
                    } else if (!satisfyTourists) {
                        // L'agent doit satisfaire les touristes
                        satisfyTourists();
                    } else if (finishVisit) {
                        // Fin du scénario
                        System.out.println("Fin du scénario. Les touristes sont satisfaits !");
                        doDelete(); // Terminer l'agent après la fin du scénario
                    }
                } else {
                    // Il n'y a pas de touristes
                    // Mettez à jour les intentions en conséquence
                    waitForGroup = false;
                    retrieveGroup = false;
                    presentPaintings = false;
                    determineNextRoom = false;
                    finishVisit = false;

                    //l'agent peut attendre (prendre un pause)
                    block();
                }
            }
        });
    }
    // Méthode pour déterminer la prochaine salle à visiter
    private void determineNextRoom() {
        // Logique pour déterminer la prochaine salle
        // Exemple : aller à la salle suivante
        currentRoom = "Salle1";
        System.out.println("Agent guide : Direction " + currentRoom);
        // Mettez à jour les croyances et les intentions
        determineNextRoom = false;
        provideDetailedExplanations = true;
    }

    // Méthode pour expliquer les tableaux dans une salle
    private void presentPaintings() {
        // Logique pour fournir des explications détaillées
        // Exemple : expliquer les tableaux dans la salle actuelle
        System.out.println("Agent guide : Explication des tableaux dans " + currentRoom);
        // Mettre à jour les croyances et les intentions
        presentPaintings = false;
        satisfyTourists = true;
    }

    // Méthode pour satisfaire les touristes
    private void satisfyTourists() {
        // Logique pour satisfaire les touristes
        // Satisfaire les touristes dans la salle actuelle
        System.out.println("Agent guide : Les touristes sont satisfaits dans " + currentRoom);
        // Mettre à jour les croyances et les intentions
        satisfyTourists = false;
        finishVisit = true;
    }
}