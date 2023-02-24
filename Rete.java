import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Rete {

    //campi
    private Node root;                          //nodo da cui si dirama tutta la rete
    private List<Node> alphaNodesFullList;      //lista che contiene tutti i nodi alpha della rete
    private List<BetaNode> betaNodesFullList;   //lista che contiene tutti i nodi beta della rete
    private List<Node> terminalNodesFullList;   //lista che contiene tutti i nodi terminal della rete

    //costruttore
    public Rete() {
        this.root = new Node(null);
        this.alphaNodesFullList = new ArrayList<>();
        this.betaNodesFullList = new ArrayList<>();
        this.terminalNodesFullList = new ArrayList<>();
    }

    //genera la rete di nodi a partire dagli lhs passati in ingresso (le condizioni della query)
    public void updateRete(List<List<String>> lhs) {
        List<Node> alphaNodesCurrentList = new ArrayList<>();       //lista che contiene solo i nodi alpha creati durante l'esecuzione di questo metodo 
        List<BetaNode> betaNodesCurrentList = new ArrayList<>();    //lista che contiene solo i nodi beta creati durante l'esecuzione di questo metodo

        //per ogni lhs creo un nodo alpha
        for (int i = 0; i < lhs.size(); i++) {

            //se esiste gia' un nodo alpha con lo stesso lhs, non ne crea un altro
            Node alphaNode = findAlphaValue(alphaNodesFullList, lhs.get(i));
            if (alphaNode == null) {
                alphaNode = new Node(Arrays.asList(lhs.get(i)));
                this.root.getChildren().add(alphaNode);
                alphaNodesFullList.add(alphaNode);
            }

            //aggiungo il nodo alpha al nodo root padre
            alphaNodesCurrentList.add(alphaNode);
        }

        //creo un beta con genitori i due alpha che contengono i primi due lhs passati in ingresso al metodo
        BetaNode betaNode = findBetaValue(Arrays.asList(alphaNodesCurrentList.get(0).getValue().get(0), alphaNodesCurrentList.get(1).getValue().get(0)));
        if (betaNode == null) {
            betaNode = new BetaNode(-1, Arrays.asList(alphaNodesCurrentList.get(0).getValue().get(0), alphaNodesCurrentList.get(1).getValue().get(0)), alphaNodesCurrentList.get(0), alphaNodesCurrentList.get(1));
            betaNodesFullList.add(betaNode);
        }
        betaNodesCurrentList.add(betaNode);
        
        //se gli lhs sono 2, allora questo nodo beta e' l'ultimo, genero un nodo terminal figlio
        if(alphaNodesCurrentList.size() == 2) {
            if (betaNode.getChildren().isEmpty()) {
                Node terminalNode = new Node(null);
                terminalNodesFullList.add(terminalNode);
                betaNode.getChildren().add(terminalNode);    
            }
        } else {
            //se gli lhs sono > 2, per ogni lhs successivo, creo un beta (se non esiste gia') che ha come genitori un beta e un alpha
            for (int i = 2; i < alphaNodesCurrentList.size(); i++) {
                betaNode = findBetaValue(Arrays.asList(betaNodesCurrentList.get(i-2).getValue(), alphaNodesCurrentList.get(i).getValue().get(0)));
                if (betaNode == null) {
                    betaNode = new BetaNode(-1, Arrays.asList(betaNodesCurrentList.get(i-2).getValue(), alphaNodesCurrentList.get(i).getValue().get(0)), betaNodesCurrentList.get(i-2), alphaNodesCurrentList.get(i));   
                    betaNodesFullList.add(betaNode);
                }
                betaNodesCurrentList.add(betaNode);

                //aggiungo ai figli del nodo alpha il nodo beta appena creato
                alphaNodesCurrentList.get(i).getChildren().add(betaNode);

                //quando arrivo all'ultimo beta, aggiungo alla lista dei suoi figli il nodo terminal
                if (i == alphaNodesCurrentList.size()-1) {
                    if (betaNode.getChildren().isEmpty()) {
                        Node terminalNode = new Node(null);
                        terminalNodesFullList.add(terminalNode);
                        betaNode.getChildren().add(terminalNode);    
                    }
                }
            }
        }
    }

    //Cerca uno o piu' pattern all'interno della rete, se trovati li mette in output.
    public void findMatch(String pattern, Object sampleID, boolean deleteMemory) {
        List<Object> outputList = new ArrayList<>();
        boolean matchFound = false;

        System.out.println("INPUT: " + pattern);

        //separa le condizioni della query e le inserisce in una lista
        List<List<String>> fact = stringToList(pattern);

        //per ogni elemento della tupla, metto lo stesso token a tutti i nodi alpha che hanno lo stesso value del fatto 
        for(List<String> currentFact : fact) {
            for (Node alphaNode : alphaNodesFullList) {
                //se il fatto corrisponde ad una variabile, allora ogni nodo che e' stato generato dalla tupla lhs che si trova alla stessa posizione in cui si trova la variabile nella tupla fact, deve avere in memoria il token
                //if(currentFact.contains("?")) {
                    //TODO: al momento non controlla il nome della variabile, associa il token a qualunque variabile indipendentemente da quale sia
                //    alphaNode.getMemory().add(sampleID);
                //se il fatto non e' una variabile, se viene trovato un match con il value del nodo, viene aggiunto il token alla memoria del nodo
                /*} else*/ if(alphaNode.getValue().contains(currentFact)) {
                    if(!alphaNode.getMemory().contains(sampleID)) {
                        alphaNode.getMemory().add(sampleID);
                    }       
                }
            }
        }
        for (BetaNode betaNode : betaNodesFullList) {
            if(betaNode.getParent1().getMemory().contains(sampleID) && betaNode.getParent2().getMemory().contains(sampleID)) {
                if (!betaNode.getMemory().contains(sampleID)) {
                    betaNode.getMemory().add(sampleID);
                }
                //gli unici nodi figli di un beta sono i nodi terminal, quindi se il beta ha un figlio lo esegue (perche' vuol dire che siamo arrivati in fondo)
                if (!betaNode.getChildren().isEmpty()) {

                    //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                    outputList.addAll(betaNode.getParent1().getValue());
                    outputList.addAll(betaNode.getParent2().getValue());
                    System.out.println("OUTPUT: " + listFlattener(outputList));
                    matchFound = true;

                    //reset della lista
                    outputList.clear();
                }
            }
        }
        if (deleteMemory) {
            deleteNodesMemory(sampleID);
        }
        if (!matchFound) {
            System.out.println("OUTPUT: match not found");
        }
    }

    //data una query in ingresso (solo le condizioni della query), genera una lista in cui ogni elemento e' un elemento della query. Se trova ";" genera sottoliste che hanno come elementi gli elementi di ogni query.
    private List<List<String>> stringToList(String string) {
        List<List<String>> outString = new ArrayList<>();
        List<String> fullList = Arrays.asList(string.split("\\s+"));
        for (int i = 1; i < fullList.size()+2; i++) {
            //se non e' il carattere ";" che separa gli lhs, inserisce l'lhs nella lista (come sottolista)
            if (i%4 == 0) {
                outString.add(Arrays.asList(fullList.get(i-4), fullList.get(i-3), fullList.get(i-2)));
            }
        }
        return outString;
    }

    //restituisce il nodo che contiene il lhs specificato. Altrimenti restituisce null
    private Node findAlphaValue(List<Node> nodeList, Object value) {
        for (Node node : nodeList) {
            if (node.getValue().contains(value)) {
                return node;
            }
        }
        return null;
    }

    //restituisce il nodo beta che contiene tutti i lhs specificati. Altrimenti restituisce null
    private BetaNode findBetaValue(List<Object> value) {
        for (BetaNode betaNode : betaNodesFullList) {
            if (betaNode.getValue().containsAll(value)) {
                return betaNode;
            }
        }
        return null;
    }

    //elimina la memoria (token) dai nodi
    private void deleteNodesMemory(Object token) {
        for (Node alphaNode : alphaNodesFullList) {
            alphaNode.deleteMemory(token);

        }
        for (BetaNode betaNode : betaNodesFullList) {
            betaNode.deleteMemory(token);
        }
    }

    public static List<?> listFlattener(List<?> list) {
        List<Object> flattenedList = new ArrayList<>();
    
        for (Object listElement : list) {
            if (listElement instanceof List) {
                flattenedList.addAll(listFlattener((List<?>) listElement));
            } else {
                flattenedList.add(listElement);
            }
        }
        return flattenedList;
    }

    //stampa a video i nodi della rete
    public void printRete() {
        System.out.println("root: " + 1);
        System.out.println();

        System.out.println("alphaNodes: " + getNumAlphaNodes());
        for (Node alphaNode : alphaNodesFullList) {
            System.out.print(" Value:" + alphaNode.getValue().toString());
            System.out.print(" Token:" + alphaNode.getMemory().toString());
            System.out.println();
        }
        System.out.println();
        System.out.println("betaNodes: " + getNumBetaNodes());
        for (BetaNode betaNode : betaNodesFullList) {
            System.out.print(" Value:" + betaNode.getValue().toString());
            System.out.print(" Token:" + betaNode.getMemory().toString());
            System.out.println();
        }
        System.out.println();
        System.out.println("terminalNodes: " + getNumTerminalNodes());
        System.out.println();
        System.out.println("total rete size: " + getNumTotNodes());
        
        System.out.println();
        System.out.println("every possible match");
        for (BetaNode betaNode : betaNodesFullList) {
            if (!betaNode.getChildren().isEmpty()) {
                List<Object> CombinationsToRhs = new ArrayList<>();
                CombinationsToRhs.addAll(betaNode.getParent1().getValue());
                CombinationsToRhs.addAll(betaNode.getParent2().getValue());
                System.out.println(listFlattener(CombinationsToRhs));
                CombinationsToRhs.clear();
            }
        }
    }

    //selettori
    public Node getRoot() {
        return this.root;
    }

    public int getNumAlphaNodes() {
        return this.alphaNodesFullList.size();  //non utilizzo 'alphaNodesFullList.size()' perche' ho pensato fosse piu' efficiente così. Stessa cosa per gli altri
    }

    public int getNumBetaNodes() {
        return this.betaNodesFullList.size();
    }

    public int getNumTerminalNodes() {
        return this.terminalNodesFullList.size();
    }

    public int getNumTotNodes() {
        return getNumAlphaNodes() + getNumBetaNodes() + getNumTerminalNodes() + 1;  //+1 per il nodo di root
    }

    public List<Node> getAlphaNodesList() {
        return this.alphaNodesFullList;
    }

    public List<BetaNode> getBetaNodesList() {
        return this.betaNodesFullList;
    }
}