import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Rete {
    //campi
    private List<AlphaNode> alphaNodesFullList;     //lista che contiene tutti i nodi alpha della rete
    private List<BetaNode> betaNodesFullList;       //lista che contiene tutti i nodi beta della rete
    private List<Integer> alphaNodesToSkip;         //lista in cui vengono salvati gli indici dei nodi alpha da evitare durante la ricerca del pattern, in modo da ottimizzare
    private Map<String, Integer> tokenMap;          //mappa per sampleID
    private ToolboxList listTB;                     //metodi per la modifica di liste
    //private ToolboxString stringTB;               //metodi per la modifica di stringhe
    private ToolboxQuery queryTB;                   //metodi per la modifica di query


    //costruttore
    public Rete() {
        this.alphaNodesFullList = new ArrayList<>();
        this.betaNodesFullList = new ArrayList<>();
        this.tokenMap = new HashMap<>();
        this.alphaNodesToSkip = new ArrayList<>();
        this.listTB = new ToolboxList();
        //this.stringTB = new ToolboxString();
        this.queryTB = new ToolboxQuery();
    
    }

    //genera / rimuove nodi alpha. Se addTriple == true, allora crea i nodi, se addTriple == false, li elimina 
    public void updateRete(List<String> triple, boolean addTriple) {

        //rimuove nodi alpha
        if (!addTriple) {

            //controlla se esiste gia' un alpha con lo stesso value (LHS), se esiste lo elimina
            AlphaNode alphaNode = findAlphaValue(triple);
            if (alphaNode != null) {
                alphaNode = null;   //metto l'oggetto a null in modo che possa essere raccolto dal garbage collector
            }

        //aggiunge nodi alpha
        } else {

            //controlla se esiste gia' un alpha con lo stesso value (LHS), se non esiste ne crea uno
            AlphaNode alphaNode = findAlphaValue(triple);
            if (alphaNode == null) {
                alphaNode = new AlphaNode(Arrays.asList(triple));
                alphaNodesFullList.add(alphaNode);
            }
        }
    }

    public List<List<Object>> findMatch(String pattern) {
        Object sampleID = tokenization(pattern);
        return checkOutput(listTB.listToListOfLists(findMatch(pattern, sampleID), 3), queryTB.queryToList(pattern));

        //togliendo il commento alla prossima riga posso far restituire al metodo una lita di si liste di stringhe, inveve che oggetti, ma aumenta il tempo che richiede per mettere in uscita l'output
        //return listTB.changeToListOfListsOfString(checkOutput(listTB.listToListOfLists(findMatch(pattern, sampleID), 3), queryTB.queryToList(pattern)));
    }

    //Cerca uno o piu' pattern all'interno della rete, se trovati li mette in output.
    private List<Object> findMatch(String pattern, Object sampleID) {
        List<List<Object>> updatedPattern = new ArrayList<>();
        List<Object> newPattern = new ArrayList<>();
        List<Object> betaOutputList = new ArrayList<>();
        List<Object> alphaOutputList = new ArrayList<>();
        List<Object> out = new ArrayList<>();

        //System.out.println("INPUT: " + pattern);

        //separa soggetto predicato oggetto dalla stringa e le inserisce in una lista
        List<List<String>> triples = queryTB.queryToList(pattern);

        //System.out.println("TRIPLE: " + triples);

        //CASO 1: caso in cui in ingresso ci sia una query con delle variabili
        if (queryTB.containsVariable(triples)) {

            //se il pattern in ingresso al metodo e' composto da una singola tripla
            if (triples.size() == 1) {
                for (List<String> currentTriple : triples) {
                    for (AlphaNode alphaNode : alphaNodesFullList) {
                        newPattern = replaceListVariablesWithConstants(listTB.listFlattener(alphaNode.getValue()), currentTriple);
                        out.add(findMatch(queryTB.queryfy(listTB.listToString(newPattern)), sampleID));
                    }
                }
            } else {

                //se il pattern in ingresso al metodo e' composto da n triple
                for (AlphaNode alphaNode : alphaNodesFullList) {
                    updatedPattern = replaceListOfListsVariablesWithConstants(listTB.listFlattener(alphaNode.getValue()), triples);
                    out.add(findMatch(queryTB.queryfy(listTB.listOfListsToString(updatedPattern)), sampleID));        
                }
            }
        } else {
            //CASO 2: caso in cui non ci siano variabili in ingresso (la ricorsione del caso con variabili converge qui)

            //NODI ALPHA
            for(List<String> currentTriple : triples) {
                for (AlphaNode alphaNode : alphaNodesFullList) {
                    
                    //assegnamento sampleID a memoria nodi alpha
                    if(alphaNode.getValue().contains(currentTriple)) {
                        if(!alphaNode.getMemory().contains(sampleID)) {
                            alphaNode.getMemory().add(sampleID);
                        }

                        //se e' arrivato al termine di questo ramo della rete restituisce il pattern trovato
                        if(alphaNode.getValue().size() == triples.size()) {
                            alphaOutputList.add(alphaNode.getValue());

                            return listTB.listFlattener(alphaOutputList);
                        }
                    }
                }
            }

            //NODI BETA
            int alphaIndex = -1, found = 0, betaIndex = -1;
            List<BetaNode> betaNodesCurrentList = new ArrayList<>();

            for (int i = 0; i < alphaNodesFullList.size(); i++) {

                //cerca il primo nodo alpha che contiene il sampleID in memoria
                if (found == 0 && alphaNodesFullList.get(i).getMemory().contains(sampleID) && !alphaNodesToSkip.contains(i)) {
                    alphaNodesToSkip.add(i);
                    alphaIndex = i;
                    found++;

                //cerca il secondo
                } else if (found == 1 && alphaNodesFullList.get(i).getMemory().contains(sampleID) && !alphaNodesToSkip.contains(i)) {

                    //creazione di un nodo beta con genitori due alpha
                    BetaNode betaNode = findBetaValue(Arrays.asList(alphaNodesFullList.get(alphaIndex).getValue(), alphaNodesFullList.get(i).getValue()));
                    if (betaNode == null) {
                        betaNode = new BetaNode(Arrays.asList(alphaNodesFullList.get(alphaIndex).getValue(), alphaNodesFullList.get(i).getValue()), alphaNodesFullList.get(alphaIndex), alphaNodesFullList.get(i));
                        betaNodesFullList.add(betaNode);
                    }
                    if (!betaNode.getMemory().contains(sampleID)) {
                        betaNode.getMemory().add(sampleID);

                    }
                    betaNodesCurrentList.add(betaNode);
                    alphaNodesToSkip.add(i);
                    betaIndex = 0;
                    found++;

                    //se e' arrivato al termine di questo ramo della rete restituisce il pattern trovato
                    if ((listTB.listFlattener(betaNode.getValue()).size() == triples.size()*3)) {
                        //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                        betaOutputList.addAll(betaNode.getParent1().getValue());
                        betaOutputList.addAll(betaNode.getParent2().getValue());
                        
                        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
                        deleteNodesMemory(sampleID.toString());

                        return listTB.listFlattener(betaOutputList);
                    }

                //cerca il terzo o oltre
                } else if (found > 1 && alphaNodesFullList.get(i).getMemory().contains(sampleID) && !alphaNodesToSkip.contains(i)) {

                    //creazione di un nodo beta con genitori un beta e un alpha
                    BetaNode betaNode = findBetaValue(Arrays.asList(betaNodesCurrentList.get(betaIndex).getValue(), alphaNodesFullList.get(i).getValue()));
                    if (betaNode == null) {
                        betaNode = new BetaNode(Arrays.asList(betaNodesCurrentList.get(betaIndex).getValue(), alphaNodesFullList.get(i).getValue()), betaNodesCurrentList.get(betaIndex), alphaNodesFullList.get(i));
                        betaNodesFullList.add(betaNode);
                    }
                    if (!betaNode.getMemory().contains(sampleID)) {
                        betaNode.getMemory().add(sampleID);
                    }
                    betaNodesCurrentList.add(betaNode);
                    alphaNodesToSkip.add(i);
                    betaIndex++;
                    found++;

                    //verifica che sia arrivato al termine di questo ramo della rete
                    if ((listTB.listFlattener(betaNode.getValue()).size() == triples.size()*3)) {
                        //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                        betaOutputList.addAll(betaNode.getParent1().getValue());
                        betaOutputList.addAll(betaNode.getParent2().getValue());
                        
                        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
                        deleteNodesMemory(sampleID.toString());

                        return listTB.listFlattener(betaOutputList);
                    }
                }
            }
        }

        //essendo questa variabile un campo della classe rete, non viene mai resettata, bisogna resettarla ogni volta che sta per terminare l'esecuzione del metodo 'findMatch'
        alphaNodesToSkip.clear();

        //inserisce nella variabile 'matchResult' tutti i risultati prodotti dalla ricerca all'interno di rete
        List<Object> matchResult = listTB.listFlattener(listTB.removeDoubles(out));
        
        return matchResult;
    }

    //esegue la regola corrispondente al match trovato
    //private List<Object> executeRule(List<Object> matchResult) {
        //TODO: vd. INSTANS
    //    return null;
    //}

    //sostituisce le variabili di listWithVariables con le costanti di listWithConstants, mette in uscita la lista che aveva variabili sostituite con costanti (per effettuare la ricorsione del metodo 'findMatch' con solo costanti)
    private List<Object> replaceListVariablesWithConstants(List<?> listWithConstants, List<String> listWithVariables) {
        List<Object> out = new ArrayList<>();
        Map<String, Object> variableMap = new HashMap<>();
        Map<Object, Object> constantMap = new HashMap<>();

        for (int i = 0; i < listWithConstants.size(); i++) {
            if (listWithVariables.get(i).startsWith("?") || listWithVariables.get(i).startsWith("$")) {
                String variableName = listWithVariables.get(i).substring(1);
                Object constantElement = listWithConstants.get(i);
                if (!variableMap.containsKey(variableName)) {
                    variableMap.put(variableName, constantElement);
                    if (!constantMap.containsKey(constantElement)) {
                        constantMap.put(constantElement, constantElement);
                        out.add(variableMap.get(variableName));
                    }
                } else {
                    out.add(constantMap.get(constantElement));
                }
            } else {
                out.add(listWithVariables.get(i));
            }
        }
        return out;
    }

    //sostituisce le variabili di listWithVariables con le costanti di listWithConstants, mette in uscita la lista che aveva variabili sostituite con costanti (per effettuare la ricorsione del metodo 'findMatch' con solo costanti)
    private List<List<Object>> replaceListOfListsVariablesWithConstants(List<?> listWithConstants, List<List<String>> listWithVariables) {
        List<List<Object>> out = new ArrayList<>();
        Map<String, Object> variableMap = new HashMap<>();

        for (List<String> variables : listWithVariables) {
            List<Object> outElement = new ArrayList<>();
            
            for (int i = 0; i < variables.size(); i++) {
                String variable = variables.get(i);
                if (variable.startsWith("?") || variable.startsWith("$")) {
                    if (!variableMap.containsKey(variable)) {
                        variableMap.put(variable, listWithConstants.get(i));
                    }
                    outElement.add(variableMap.get(variable));
                } else {
                    outElement.add(variable);
                }
            }
            out.add(outElement);
        }

        return out;
    }
    //controlla che la lista 'listToCheck' abbia le stringhe che non contengono variabili uguali e nelle stesse posizioni di quelle della lista 'inputList'. Se non e' rispettato restituisce una lista vuota, altrimenti restituisce la lista
    private List<List<Object>> checkOutput(List<List<Object>> listToCheck, List<List<String>> inputList) {

        if (listToCheck.isEmpty()) {
            return new ArrayList<>();
        }

        int i = -1, j = -1;
        for (List<Object> currentCheckList : listToCheck) {
            i++;
            if (i == inputList.size()) {
                i = 0;
            }
            for (Object currentCheckElement : currentCheckList) {
                j++;
                if (!(inputList.get(i).get(j).startsWith("?") || inputList.get(i).get(j).startsWith("$"))) {
                    if (!currentCheckElement.toString().equals(inputList.get(i).get(j))) {
                        //lista vuota
                        return new ArrayList<>();
                    }
                }
                if (j == 2) {
                    j = -1;
                }
            }
        }
        return listToCheck;
    }

    //genera un token unico da usare come ID nella memoria dei nodi
    private int tokenization(String string) {
        Random random = new Random();
        int token = 0;

        if (this.tokenMap.containsKey(string)) {
            return this.tokenMap.get(string);

        } else {

            do {
                token = random.nextInt(10_000_000);
            } while (tokenMap.containsValue(token));

            this.tokenMap.put(string, token);
        }
        //System.out.println(this.tokenMap);
        return this.tokenMap.get(string);
    }

    //restituisce il nodo che contiene il lhs specificato. Altrimenti restituisce null
    private AlphaNode findAlphaValue(Object value) {
        for (AlphaNode node : alphaNodesFullList) {
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

    //elimina il sampleID dai nodi che lo contengono
    private void deleteNodesMemory(String sampleID) {
        for (AlphaNode alphaNode : alphaNodesFullList) {
            alphaNode.deleteMemory(sampleID);

        }
        for (BetaNode betaNode : betaNodesFullList) {
            betaNode.deleteMemory(sampleID);
        }
    }

    //stampa a video i nodi della rete
    public void printRete() {
        System.out.println("alphaNodes: " + this.alphaNodesFullList.size());
        for (AlphaNode alphaNode : alphaNodesFullList) {
            System.out.print(" Value:" + alphaNode.getValue().toString());
            System.out.println();
        }
        System.out.println();
        System.out.println("betaNodes: " + this.betaNodesFullList.size());
        for (BetaNode betaNode : betaNodesFullList) {
            System.out.print(" Value:" + betaNode.getValue().toString());
            System.out.println();
        }
    }

    //selettori
    public int getNumAlphaNodes() {
        return this.alphaNodesFullList.size();
    }

    public int getNumBetaNodes() {
        return this.betaNodesFullList.size();
    }

    public List<AlphaNode> getAlphaNodesList() {
        return this.alphaNodesFullList;
    }

    public List<BetaNode> getBetaNodesList() {
        return this.betaNodesFullList;
    }
}