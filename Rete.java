import java.util.List;
import java.util.Map;
import java.util.UUID;  //libreria per generare token che sono garantiti essere unici 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Rete {
    //campi
    private List<AlphaNode> alphaNodesFullList;     //lista che contiene tutti i nodi alpha della rete
    private List<BetaNode> betaNodesFullList;       //lista che contiene tutti i nodi beta della rete
    private List<Integer> alphaNodesToSkip;         //lista in cui vengono salvati gli indici dei nodi alpha da evitare durante la ricerca del pattern, in modo da ottimizzare
    private Map<String, Object> tokenMap;           


    //costruttore
    public Rete() {
        this.alphaNodesFullList = new ArrayList<>();
        this.betaNodesFullList = new ArrayList<>();
        this.tokenMap = new HashMap<>();
        this.alphaNodesToSkip = new ArrayList<>();
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
        //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA" + queryToList(pattern).size());
        return checkOutput(listToListOfLists(findMatch(pattern, sampleID), 3), queryToList(pattern));
        //return listToListOfLists(findMatch(pattern, sampleID), 3);
        //TODO: mettere in altro ingresso che separa nella lista di liste i risultati (se produce piu' di un risultato)
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
        List<List<String>> triples = queryToList(pattern);

        //System.out.println("TRIPLE: " + triples);

        //CASO 1: caso in cui in ingresso ci sia una query con delle variabili
        if (containsVariable(triples)) {

            //se il pattern in ingresso al metodo e' composto da una singola tripla
            if (triples.size() == 1) {
                for (List<String> currentTriple : triples) {
                    for (AlphaNode alphaNode : alphaNodesFullList) {
                        newPattern = replaceListVariablesWithConstants(listFlattener(alphaNode.getValue()), currentTriple);
                        out.add(findMatch(queryfy(listToString(newPattern)), sampleID));
                    }
                }
            } else {

                //se il pattern in ingresso al metodo e' composto da n triple
                for (AlphaNode alphaNode : alphaNodesFullList) {
                    updatedPattern = replaceListOfListsVariablesWithConstants(listFlattener(alphaNode.getValue()), triples);
                    out.add(findMatch(queryfy(listOfListsToString(updatedPattern)), sampleID));        
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

                            return listFlattener(alphaOutputList);
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
                    if ((listFlattener(betaNode.getValue()).size() == triples.size()*3)) {
                        //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                        betaOutputList.addAll(betaNode.getParent1().getValue());
                        betaOutputList.addAll(betaNode.getParent2().getValue());
                        
                        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
                        deleteNodesMemory(sampleID.toString());

                        return listFlattener(betaOutputList);
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
                    if ((listFlattener(betaNode.getValue()).size() == triples.size()*3)) {
                        //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                        betaOutputList.addAll(betaNode.getParent1().getValue());
                        betaOutputList.addAll(betaNode.getParent2().getValue());
                        
                        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
                        deleteNodesMemory(sampleID.toString());

                        return listFlattener(betaOutputList);
                    }
                }
            }
        }

        alphaNodesToSkip.clear();
        //se arriva qui, ha trovato un match quindi si puo' eliminare la memoria dai nodi
        //deleteNodesMemory(sampleID.toString());

        //inserisce nella variabile 'matchResult' tutti i risultati prodotti dalla ricerca all'interno di rete
        List<Object> matchResult = listFlattener(removeDoubles(out));

        //executeRule(matchResult)
        
        return matchResult;
    }

    //esegue la regola corrispondente al match trovato
    //private List<Object> executeRule(List<Object> matchResult) {
        //TODO: vd. INSTANS
    //    return null;
    //}

    //data una stringa che rappresenta una query mette in uscita una lista dove ogni elemento e' composto da soggetto predicato e oggetto
    private List<List<String>> queryToList(String string) {
        List<String> fullList = Arrays.asList(string.split("\\s+"));
        List<List<String>> out = new ArrayList<>();
        int found = 0;

        //conta le occorrenze di ";" all'interno della stringa (vd. prossime righe)
        for (String currentString : fullList) {
            if (currentString.matches(";")) {
                found++;
            }
        }

        //se la query e' del tipo "a b c ; d e f ; ..."
        if (((found*4)+4) == fullList.size()+1) {
            for (int i = 1; i < fullList.size()+2; i++) {
                if (i%4 == 0) {
                    out.add(Arrays.asList(fullList.get(i-4), fullList.get(i-3), fullList.get(i-2)));
                }
            }
        } else {
        //se la query e' del tipo "a b c ; d e ; ... "
            for (int i = 1; i < fullList.size()+2; i++) {
                if ((i-1) % 3 == 0 && ((i-1) != 0)) {
                    if ((i-1) >= 6) {
                        out.add(Arrays.asList(fullList.get(0), fullList.get(i-3), fullList.get(i-2)));
                    } else {
                        out.add(Arrays.asList(fullList.get(i-4), fullList.get(i-3), fullList.get(i-2)));
                    }
                }
            }
        }
        return out;
    }
    
    //riscrive la stringa passata in input come una query
    private String queryfy(String input) {
        String[] words = input.split("\\s+");
        StringBuilder output = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            output.append(words[i]);
            if ((i + 1) % 3 == 0 && i != words.length - 1) {
                output.append(" ; ");
            } else {
                output.append(" ");
            }
        }
        return output.toString();
    }

    //converte una lista in una stringa
    private String listToString(List<?> list) {
        String out = new String();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                out += list.get(i);
            } else {
                out += " " + list.get(i);
            }
        }
        return out;
    }

    //converte una lista in una lista di liste dove ogni elemento sono 'num' elementi della lista di partenza
    private List<List<Object>> listToListOfLists(List<Object> list, int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("num must be > 0");
        }

        List<List<Object>> result = new ArrayList<>();
        List<Object> tmpList = new ArrayList<>();
        
        for (int i = 0; i < list.size(); i++) {
            tmpList.add(list.get(i));
            if (tmpList.size() == num) {
                result.add(tmpList);
                tmpList = new ArrayList<>();
            }
        }
        if (!tmpList.isEmpty()) {
            result.add(tmpList);
        }
        return result;
    }
    

    //converte una lista di liste in una stringa
    private String listOfListsToString(List<List<Object>> list) {
        StringBuilder out = new StringBuilder();
        for (List<?> sublist : list) {
            for (int i = 0; i < sublist.size(); i++) {
                if (i == 0) {
                    out.append(sublist.get(i));
                } else {
                    out.append(" ").append(sublist.get(i));
                }
            }
            out.append(" ");
        }
        return out.toString().trim();
    }

    //sostituisce le variabili di listWithVariables con le costanti di listWithConstants, mette in uscita la lista che aveva variabili sostituite con costanti (per effettuare la ricorsione del metodo 'findMatch' con solo costanti)
    private List<Object> replaceListVariablesWithConstants(List<?> listWithConstants, List<String> listWithVariables) {
        List<Object> out = new ArrayList<>();
        Map<String, Object> variableMap = new HashMap<>();
        Map<Object, Object> constantMap = new HashMap<>();
    
        for (int i = 0; i < listWithConstants.size(); i++) {
            if (isVariable(listWithVariables.get(i))) {
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
    
    //mette in uscita una lista che contiene solamente elementi che appaiono una unica volta nella lista in ingresso
    private List<Object> removeDoubles(List<Object> list) {
        List<Object> out = new ArrayList<>();

        //aggiunge alla lista di uscita elementi non gia' inseriti prima
        for (Object element : list) {
            if (!out.contains(element)) {
                out.add(element);
            }
        }

        return out;
    }

    //controlla che la lista in uscita abbia la stessa dimensione di quella in ingresso, se non e' così restituisce una lista vuota
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
                if (!(inputList.get(i).get(j).startsWith("?") || inputList.get(i).get(j).startsWith("?"))) {
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

    //restituisce true se la stringa in ingresso e' una variabile
    private boolean isVariable(String string) {
        return string.startsWith("?") || string.startsWith("$");
    }

    //come il metodo 'isVariable', ma controlla stringhe all'interno di liste di liste
    private boolean containsVariable(List<List<String>> list) {
        for (List<String> sublist : list) {
            for (String string : sublist) {
                if ((string.startsWith("?") || string.startsWith("$"))) {
                    return true;
                }
            }
        }
        return false;
    }

    //genera un token unico in base alla stringa in ingresso
    private Object tokenization(String string) {
        if (this.tokenMap.containsKey(string)) {
            return this.tokenMap.get(string);
        } else {
            //TODO: genera un token che e' garantito essere unico. Il token generato e' da 128 bit, 36 caratteri, esadecimale, essendo molto lungo potrebbe causare ritardi. Usare token piu' corti?
            UUID token = UUID.randomUUID();
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

    //data una lista di liste restituisce una singola lista
    private List<Object> listFlattener(List<?> list) {
        List<Object> flattenedList = new ArrayList<>();
    
        for (Object listElement : list) {
            if (listElement instanceof List) {
                flattenedList.addAll(listFlattener((List<?>) listElement));
            } else {
                flattenedList.add(listElement.toString());
            }
        }
        return flattenedList;
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