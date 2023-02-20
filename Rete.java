import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Rete {

    //campi
    private Node root;                          //nodo da cui si dirama tutta la rete
    private List<Node> alphaNodesFullList;      //lista che contiene tutti i nodi alpha della rete
    private List<BetaNode> betaNodesFullList;   //lista che contiene tutti i nodi beta della rete
    private List<Node> terminalNodesFullList;   //lista che contiene tutti i nodi terminal della rete
    private List<Object> tokensFullList;        //lista che contiene tutti i token della rete
    private List<Object> sampleIDFullList;      //lista che contiene tutti i samples della rete

    //costruttore
    public Rete() {
        this.root = new Node(-1,null);
        this.alphaNodesFullList = new ArrayList<>();
        this.betaNodesFullList = new ArrayList<>();
        this.terminalNodesFullList = new ArrayList<>();
        this.tokensFullList = new ArrayList<>();
        this.sampleIDFullList = new ArrayList<>();
    }

    //genera la rete di nodi a partire dagli lhs passati in ingresso (le condizioni della query)
    public void updateRete(List<Object> lhs) {
        List<Node> alphaNodesCurrentList = new ArrayList<>();       //lista che contiene solo i nodi alpha creati durante l'esecuzione di questo metodo 
        List<BetaNode> betaNodesCurrentList = new ArrayList<>();    //lista che contiene solo i nodi beta creati durante l'esecuzione di questo metodo

        //per ogni lhs creo un nodo alpha
        for (int i = 0; i < lhs.size(); i++) {

            //se esiste gia' un nodo alpha con lo stesso lhs, non ne crea un altro
            Node alphaNode = findAlphaValue(alphaNodesFullList, lhs.get(i));
            if (alphaNode == null) {
                alphaNode = new Node(i, Arrays.asList(lhs.get(i)));
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
                Node terminalNode = new Node(-1, null);
                terminalNodesFullList.add(terminalNode);
                betaNode.getChildren().add(terminalNode);    
            }
        } else {
            //se le gli lhs sono > 2, per ogni condizione successiva, creo un beta (se non esiste gia') che ha come genitori un beta e un alpha
            for (int i = 2; i < alphaNodesCurrentList.size(); i++) {
                betaNode = findBetaValue(Arrays.asList(betaNodesCurrentList.get(i-2).getValue().get(0), betaNodesCurrentList.get(i-2).getValue().get(1), alphaNodesCurrentList.get(i).getValue().get(0)));
                if (betaNode == null) {
                    betaNode = new BetaNode(-1, Arrays.asList(betaNodesCurrentList.get(i-2).getValue().get(0), betaNodesCurrentList.get(i-2).getValue().get(1), alphaNodesCurrentList.get(i).getValue().get(0)), betaNodesCurrentList.get(i-2), alphaNodesCurrentList.get(i));   
                    betaNodesFullList.add(betaNode);
                }
                betaNodesCurrentList.add(betaNode);

                //aggiungo ai figli del nodo alpha il nodo beta appena creato
                alphaNodesCurrentList.get(i).getChildren().add(betaNode);

                //quando arrivo all'ultimo beta, aggiungo alla lista dei suoi figli il nodo terminal
                if (i == alphaNodesCurrentList.size()-1) {
                    if (betaNode.getChildren().isEmpty()) {
                        Node terminalNode = new Node(-1, null);
                        terminalNodesFullList.add(terminalNode);
                        betaNode.getChildren().add(terminalNode);    
                    }
                }
            }
        }
    }

    //Cerca uno o pie' pattern all'interno della rete, se trovati li mette in output.
    public void findMatch(String queryConditions, Object sampleID, boolean deleteMemory) {
        List<Object> outputList = new ArrayList<>();
        boolean matchFound = false;
        int i = -1;

        System.out.println("INPUT: " + queryConditions);

        //genera un token unico per il sampleID passato in ingresso
        Object token = tokenization(sampleID);

        //separa le condizioni della query e le inserisce in una lista
        List<List<String>> fact = queryToList(queryConditions);

        //per ogni elemento della tupla, metto lo stesso token a tutti i nodi alpha che hanno lo stesso value del fatto 
        for (List<String> currentTuple : fact) {
            for(String currentFact : currentTuple) {
                i++;    //'i' e' usato come indice per memorizzare la posizione del fatto nella tupla 'fact'
                for (Node alphaNode : alphaNodesFullList) {
                    //se il fatto corrisponde ad una variabile, allora ogni nodo che e' stato generato dalla tupla lhs che si trova alla stessa posizione in cui si trova la variabile nella tupla fact, deve avere in memoria il token
                    if(currentFact.contains("?") && alphaNode.getPositionInsideTuple() == i) {
                        alphaNode.getMemory().add(token);
                    //se il fatto non e' una variabile, se viene trovato un match con il value del nodo, viene aggiunto il token alla memoria del nodo
                    } else if(alphaNode.getValue().contains(currentFact)) {
                        if(!alphaNode.getMemory().contains(token)) {
                            alphaNode.getMemory().add(token);
                        }       
                    }
                }
            }
        }
        for (BetaNode betaNode : betaNodesFullList) {
            if(betaNode.getParent1().getMemory().contains(token) && betaNode.getParent2().getMemory().contains(token)) {
                if (!betaNode.getMemory().contains(token)) {
                    betaNode.getMemory().add(token);
                }
                //gli unici nodi figli di un beta sono i nodi terminal, quindi se il beta ha un figlio lo esegue (perche' vuol dire che siamo arrivati in fondo)
                if (!betaNode.getChildren().isEmpty()) {

                    //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                    outputList.addAll(betaNode.getParent1().getValue());
                    outputList.addAll(betaNode.getParent2().getValue());
                    System.out.println("OUTPUT: " + outputList);
                    matchFound = true;

                    //reset della lista
                    outputList.clear();
                }
            }
        }
        if (deleteMemory) {
            deleteNodesMemory(token);
        }
        if (!matchFound) {
            System.out.println("match not found");
        }
    }

    //data una query in ingresso (solo le condizioni della query), genera una lista in cui ogni elemento e' un elemento della query. Se trova ";" genera sottoliste che hanno come elementi gli elementi di ogni query.
    private List<List<String>> queryToList(String queryConditions) {
        List<String> queryList = Arrays.asList(queryConditions.split("\\s+"));
        List<List<String>> outputList = new ArrayList<>();
        List<String> sublist = new ArrayList<>();

        for (String str : queryList) {
            if (str.contains(";")) {
                int index = str.indexOf(";");
                sublist.add(str.substring(0, index));
                outputList.add(new ArrayList<>(sublist));

                sublist.clear();
                sublist.add(str.substring(0, index));
                sublist.addAll(List.of(str.substring(index + 1).split("\\s*,\\s*")));
            } else {
                sublist.add(str);
            }
        }

        if (!sublist.isEmpty()) {
            outputList.add(sublist);
        }

        return outputList;
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

    //prende in ingresso un sampleID che identifica la tupla passata in ingresso all'algoritmo e gli associa un token unico. Restituisce il token.
    private Object tokenization(Object sampleID) {
        //genera un token che verra' usato al posto del sampleID (esempio: sampleID:Ventilatore, token:193109)
        int sampleIDIndex;
        int token;

        //se in memoria non e' gia' presente questo sampleID, lo aggiunge alla lista dei sampleID e gli associa un token unico
        if (!sampleIDFullList.contains(sampleID)) {
            sampleIDFullList.add(sampleID);
            sampleIDIndex = sampleIDFullList.indexOf(sampleID);

            //genero un token unico da associare al sampleID
            Random random = new Random();
            do {
                token = random.nextInt(1000000);    //genera un token casuale da associare al sampleID tra 0 e 999999 (compresi)
            } while (this.tokensFullList.contains(token));
            this.tokensFullList.add(sampleIDIndex, token);
            return token;

        } else {
            //altrimenti prende il suo indice dalla lista dei sampleID e restituisce il corrispondente token (i token della lista 'tokensFullList' sono posizionati nelle stesse posizioni dei sample nella lista 'samplesFullList' per poter risalire al token dal sample e viceversa)
            sampleIDIndex = sampleIDFullList.indexOf(sampleID);
            return tokensFullList.get(sampleIDIndex);
        }
    }

    //elimina la memoria (token) dai nodi
    private void deleteNodesMemory(Object token) {
        int index = -1;
        if (tokensFullList.contains(token)) {
            index = tokensFullList.indexOf(token);
            tokensFullList.remove(token);
            sampleIDFullList.remove(index);
        }
        for (Node alphaNode : alphaNodesFullList) {
            alphaNode.deleteMemory(token);

        }
        for (BetaNode betaNode : betaNodesFullList) {
            betaNode.deleteMemory(token);
        }
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
        System.out.println("every possible match (in this specific order)");
        for (BetaNode betaNode : betaNodesFullList) {
            if (!betaNode.getChildren().isEmpty()) {
                List<Object> CombinationsToRhs = new ArrayList<>();
                CombinationsToRhs.addAll(betaNode.getParent1().getValue());
                CombinationsToRhs.addAll(betaNode.getParent2().getValue());
                System.out.println(CombinationsToRhs);
                CombinationsToRhs = new ArrayList<>();
            }
        }
    }

    //stampa a video i samplesID e i rispettivi token
    //da utilizzare per debug PRIMA di 'deleteNodesMemory' all'interno di 'findMatch', altrimenti mostra 'lists are empty', perche' la memoria e' stata cancellata
    public void printSamplesAndTokens() {
        if (tokensFullList.isEmpty()) {
            System.out.println("lists are empty");
        } else {
            for (Object sampleID : sampleIDFullList) {
                System.out.println("S:" + sampleID + " T:" + tokensFullList.get(sampleIDFullList.indexOf(sampleID)));
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

    public List<Object> getTokensList() {
        return this.tokensFullList;
    }

    public List<Object> getSamplesIDList() {
        return this.sampleIDFullList;
    }
}