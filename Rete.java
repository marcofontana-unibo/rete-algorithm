import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Rete {

    //campi
    private Node root;                      //nodo di root da cui si dirama tutta la rete di nodi
    private int numAlphaNodes;
    private int numBetaNodes;
    private int numTerminalNodes;
    private int numRootNodes;
    private Execute execute;                //utilizzato per eseguire le azioni (vd. classe Execute)

    //costruttore
    public Rete() {
        this.root = new Node("root", null);
        this.execute = new Execute();
        this.numAlphaNodes = 0;
        this.numBetaNodes = 0;
        this.numTerminalNodes = 0;
        this.numRootNodes = 1;
    }

    //aggiunge una regola
    public void buildNetwork(List<Object> conditions, List<Object> actions) {
        List<Node> alphaNodesCurrentList = new ArrayList<>();
        List<Node> betaNodesCurrentList = new ArrayList<>();

        //per ogni condizione creo un nodo alpha
        for (int i = 0; i < conditions.size(); i++) {
            Node alphaNode = new Node("alpha", Arrays.asList(conditions.get(i)));

            //aggiungo il nodo alpha al nodo root padre
            this.root.getChildren().add(alphaNode);
            alphaNodesCurrentList.add(alphaNode);
            numAlphaNodes++;
        }

        //unisco la prima e seconda condizione per formare un nodo beta
        Node betaNode = new Node("beta", Arrays.asList(conditions.get(0), conditions.get(1)));
        betaNodesCurrentList.add(betaNode);
        numBetaNodes++;

        //aggiungo il nodo beta appena creato ai rispettivi nodi alpha padre
        alphaNodesCurrentList.get(0).getChildren().add(betaNode);
        alphaNodesCurrentList.get(1).getChildren().add(betaNode);
        
        //se le condizioni sono 2, allora questo nodo beta dovra' avere come nodo figlio il nodo con l'azione (o i nodi con le azioni)
        if(conditions.size() == 2) {
            Node terminalNode = new Node("terminal", actions);
            betaNode.getChildren().add(terminalNode);
            numTerminalNodes++;
        } else {

            //se le condizioni sono > 2, per ogni condizione successiva ripeto, unisco un beta e un alpha
            for (int i = 2; i < conditions.size(); i++) {
                List<Object> betaValue = new ArrayList<>();
                betaValue.addAll((List<Object>)betaNode.getValue());    //prendo il contenuto del nodo beta padre (LHS)
                betaValue.add(conditions.get(i));                       //aggiungo il nuovo LHS
                betaNode = new Node("beta", betaValue);           //creo un nodo beta figlio con tutti gli LHS
                betaNodesCurrentList.add(betaNode);

                //aggiungo il nodo appena creato ai rispettivi nodi padre (un alpha e un beta)
                betaNodesCurrentList.add(betaNode);
                alphaNodesCurrentList.get(i).getChildren().add(betaNode);
                numBetaNodes++;

                //aggiungo il nodo terminal (di esecuzione)
                Node terminalNode = new Node("terminal", actions);
                betaNode.getChildren().add(terminalNode);
                numTerminalNodes++;
            }
        }
    }

    //cerca una corrispondenza tra il contenuto dei nodi e i fatti passati in ingresso, se trovata esegue il RHS corrispondente
    public void match(List<Object> fact) {
        for (Node alphaNode : root.getChildren()) {
            for (Node betaNode : alphaNode.getChildren()) {
                if (betaNode.getValue().containsAll(fact)) {
                    for(Object RHS : betaNode.getChildren().get(0).getValue()) {
                        execute.execute(RHS);
                    }
                    return; //termina l'iterazione in anticipo se trova un match
                }
            }
        }
        System.out.println("match not found");
    }

    //stampa a video i nodi della rete
    public void printNetwork() {
        System.out.println("root: " + this.numRootNodes);
        System.out.println("alphaNodes: " + getNumAlphaNodes());
        System.out.println("betaNodes: " + getNumBetaNodes());
        System.out.println("terminalNodes: " + getNumTerminalNodes());
        System.out.println("total network size: " + getNodesCount());
    }

    //selettori
    public Node getRoot() {
        return this.root;
    }

    public int getNumAlphaNodes() {
        return this.numAlphaNodes;
    }

    public int getNumBetaNodes() {
        return this.numBetaNodes;
    }

    public int getNumTerminalNodes() {
        return numTerminalNodes;
    }

    public int getNodesCount() {
        return this.numTerminalNodes + this.numAlphaNodes + this.numBetaNodes + this.numRootNodes;
    }
}