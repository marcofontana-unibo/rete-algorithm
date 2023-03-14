import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Rete {
    //campi
    private List<AlphaNode> alphaNodesFullList;     //lista che contiene tutti i nodi alpha della rete
    private List<BetaNode> betaNodesFullList;       //lista che contiene tutti i nodi beta della rete
    private List<Integer> alphaNodesToSkip;         //lista in cui vengono salvati gli indici dei nodi alpha da evitare durante la ricerca del pattern
    private Map<String, Integer> tokenMap;          //mappa per sampleID
    private Toolbox tb;                             //metodi generali di modifica liste, stringhe e query

    //costruttore
    public Rete() {
        this.alphaNodesFullList = new ArrayList<>();
        this.betaNodesFullList = new ArrayList<>();
        this.tokenMap = new HashMap<>();
        this.alphaNodesToSkip = new ArrayList<>();
        this.tb = new Toolbox();
    
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

    public Map<String, List<Object>> findMatch(String pattern) {
        Object sampleID = tb.tokenization(pattern, tokenMap);
        return tb.var(tb.listToListOfLists(findMatch(pattern, sampleID), 3), tb.queryToList(pattern));

        //togliendo il commento alla prossima riga posso far restituire al metodo una lita di liste di stringhe, inveve che oggetti, ma aumenta il tempo che richiede per mettere in uscita l'output
        //return tb.changeToListOfListsOfString(tb.ver(tb.listToListOfLists(findMatch(pattern, sampleID), 3), tb.queryToList(pattern)));
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
        List<List<String>> triples = tb.queryToList(pattern);

        //System.out.println("TRIPLE: " + triples);

        //CASO 1: caso in cui in ingresso ci sia una query con delle variabili
        if (tb.containsVariable(triples)) {

            //se il pattern in ingresso al metodo e' composto da una singola tripla
            if (triples.size() == 1) {
                for (List<String> currentTriple : triples) {
                    for (AlphaNode alphaNode : alphaNodesFullList) {
                        newPattern = tb.replaceListVariablesWithConstants(tb.listFlattener(alphaNode.getValue()), currentTriple);
                        out.add(findMatch(tb.queryfy(tb.listToString(newPattern)), sampleID));
                    }
                }
            } else {

                //se il pattern in ingresso al metodo e' composto da n triple
                for (AlphaNode alphaNode : alphaNodesFullList) {
                    updatedPattern = tb.replaceListOfListsVariablesWithConstants(tb.listFlattener(alphaNode.getValue()), triples);
                    out.add(findMatch(tb.queryfy(tb.listOfListsToString(updatedPattern)), sampleID));        
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

                            return tb.listFlattener(alphaOutputList);
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
                    if ((tb.listFlattener(betaNode.getValue()).size() == triples.size()*3)) {
                        //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                        betaOutputList.addAll(betaNode.getParent1().getValue());
                        betaOutputList.addAll(betaNode.getParent2().getValue());
                        
                        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
                        deleteNodesMemory(sampleID.toString());

                        return tb.listFlattener(betaOutputList);
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
                    if ((tb.listFlattener(betaNode.getValue()).size() == triples.size()*3)) {
                        //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                        betaOutputList.addAll(betaNode.getParent1().getValue());
                        betaOutputList.addAll(betaNode.getParent2().getValue());
                        
                        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
                        deleteNodesMemory(sampleID.toString());

                        return tb.listFlattener(betaOutputList);
                    }
                }
            }
        }

        //essendo questa variabile un campo della classe rete, non viene mai resettata, bisogna resettarla ogni volta che sta per terminare l'esecuzione del metodo 'findMatch'
        alphaNodesToSkip.clear();

        //inserisce nella variabile 'matchResult' tutti i risultati prodotti dalla ricerca all'interno di rete
        List<Object> matchResult = tb.listFlattener(tb.removeDoubles(out));
        
        return matchResult;
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
    public List<AlphaNode> getAlphaNodesList() {
        return this.alphaNodesFullList;
    }

    public List<BetaNode> getBetaNodesList() {
        return this.betaNodesFullList;
    }
}