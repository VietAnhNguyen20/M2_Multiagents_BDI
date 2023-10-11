package firstAgent;
import jade.core.*;

public class HelloAgent extends Agent {
    protected void setup() {
        System.out.println("Hello world, I'm " + this.getLocalName());
//
//        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
//        inform.setContent("contenu");
//        inform.setProtocol("information");
    }
}