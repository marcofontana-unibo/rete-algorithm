import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Rete {

    //campi
    private List<AlphaNode> alphaNodesFullList;      //lista che contiene tutti i nodi alpha della rete
    private List<Object> everyLhsElement;
    private List<BetaNode> betaNodesFullList;        //lista che contiene tutti i nodi beta della rete

    //costruttore
    public Rete() {
        this.alphaNodesFullList = new ArrayList<>();
        this.everyLhsElement = new ArrayList<>();
        this.betaNodesFullList = new ArrayList<>();
    }

    //genera la rete di nodi a partire dagli lhs passati in ingresso (le condizioni della query)
    public void updateRete(List<List<String>> lhs) {
        List<AlphaNode> alphaNodesCurrentList = new ArrayList<>();      //lista che contiene solo i nodi alpha creati durante l'esecuzione di questo metodo 
        List<BetaNode> betaNodesCurrentList = new ArrayList<>();        //lista che contiene solo i nodi beta creati durante l'esecuzione di questo metodo

        //ogni elemento all'interno del lhs (soggetto, predicato, oggetto), lo salvo all'interno di una lista (sara' usato in 'findMatch', nel caso in cui in ingresso saranno passate variabili)
        for (List<String> currentLhsList : lhs) {
            for (String currentLhsElement : currentLhsList) {
                everyLhsElement.add(currentLhsElement);
            }
        }

        //per ogni lhs creo un nodo alpha
        for (int i = 0; i < lhs.size(); i++) {
            //se esiste gia' un nodo alpha con lo stesso lhs, non ne crea un altro
            AlphaNode alphaNode = findAlphaValue(alphaNodesFullList, lhs.get(i));
            if (alphaNode == null) {
                alphaNode = new AlphaNode(Arrays.asList(lhs.get(i)));
                alphaNodesFullList.add(alphaNode);

                //TODO: sempre per il caso variabili, crea dei sotto-nodi alpha nel caso in cui questo alpha conterra' come value una variabile
                //alphaNode.getChildren().add(new AlphaNode(null));
            }
            alphaNodesCurrentList.add(alphaNode);
        }

        //creo un beta con genitori i due alpha che contengono i primi due lhs passati in ingresso al metodo (se non esiste gia')
        BetaNode betaNode = findBetaValue(Arrays.asList(alphaNodesCurrentList.get(0).getValue().get(0), alphaNodesCurrentList.get(1).getValue().get(0)));
        if (betaNode == null) {
            betaNode = new BetaNode(Arrays.asList(alphaNodesCurrentList.get(0).getValue().get(0), alphaNodesCurrentList.get(1).getValue().get(0)), alphaNodesCurrentList.get(0), alphaNodesCurrentList.get(1));
            betaNodesFullList.add(betaNode);
        }
        betaNodesCurrentList.add(betaNode);
        
        //se i lhs sono > 2, per ogni lhs successivo, creo un beta (se non esiste gia') che ha come genitori un beta e un alpha
        if(alphaNodesCurrentList.size() > 2) {
            for (int i = 2; i < alphaNodesCurrentList.size(); i++) {
                betaNode = findBetaValue(Arrays.asList(betaNodesCurrentList.get(i-2).getValue(), alphaNodesCurrentList.get(i).getValue().get(0)));
                if (betaNode == null) {
                    betaNode = new BetaNode(Arrays.asList(betaNodesCurrentList.get(i-2).getValue(), alphaNodesCurrentList.get(i).getValue().get(0)), betaNodesCurrentList.get(i-2), alphaNodesCurrentList.get(i));   
                    betaNodesFullList.add(betaNode);
                }
                betaNodesCurrentList.add(betaNode);
            }
        }
    }

    //Cerca uno o piu' pattern all'interno della rete, se trovati li mette in output.
    public void findMatch(String pattern, Object sampleID, boolean deleteMemory) {
        List<Object> newPattern = new ArrayList<>();
        List<Object> outputList = new ArrayList<>();
        boolean matchFound = false;
        int i = -1;

        //System.out.println("INPUT: " + pattern);

        //separa le condizioni della query e le inserisce in una lista
        List<List<String>> fact = queryToList(pattern);

        //System.out.println("FATTI: " + fact);

        //per ogni elemento della tupla, metto lo stesso sampleID a tutti i nodi alpha che hanno lo stesso value del fatto 
        for(List<String> currentFact : fact) {
            for (AlphaNode alphaNode : alphaNodesFullList) {
                for (String factElement : currentFact) {
                    //TODO: sistema variabili
                    //se il fatto e' una variabile lancia ricorsivamente questo metodo con tutti i possibili pattern
                    if (isVariable(factElement)) {
                        if(!alphaNode.getMemory().contains(sampleID)) {
                            alphaNode.getMemory().add(sampleID);
                        }
                        if(alphaNode.getValue().size() == fact.size()) {
                            i++;
                            //TODO: PRENDI IL PATTERN CHE METTEREBBE IN USCITA E SOSTITUISCI LE NON-VARIABILI CON LE COSTANTI CHE ERANO IN INPUT
                            //newPattern = replaceConstants(stringToList(alphaNode.getValue().toString()), fact);
                            //System.out.println(newPattern);
                            
                            //System.out.println("S" + listToString(alphaNode.getValue()));
                            //System.out.println("A" + listFlattener(alphaNode.getValue()));
                            //System.out.println("F" + currentFact);
                            newPattern = replaceConstants(listFlattener(alphaNode.getValue()), currentFact);
                            //System.out.println("R" + newPattern);
                            findMatch(listToString(listFlattener(newPattern)), sampleID + "" + i, true);
                            
                            //ripeti lo stesso fatto, ma con alphaNodesFullList.get(j-k) dove c'è una variabile, k serve per selezionare value diversi per variabili diverse e viceversa
                            //newPattern = replaceVariables(fact, alphaNodesFullList);
                            //newPattern = replaceVariables(alphaNode.getValue(), alphaNodesFullList);
                            //System.out.println("OUTPUT: " + listFlattener(alphaNode.getValue()));
                        }
                    }
                }
                //se il fatto contiene una variabile, la ricorsione converge qui
                //se il fatto non e' una variabile, se viene trovato un match con il value del nodo, viene aggiunto il sampleID alla memoria del nodo
                if(alphaNode.getValue().contains(currentFact)) {
                    if(!alphaNode.getMemory().contains(sampleID)) {
                        alphaNode.getMemory().add(sampleID);
                    }
                    if(alphaNode.getValue().size() == fact.size()) {
                        System.out.println("OUTPUT: " + listFlattener(alphaNode.getValue()));
                        matchFound = true;
                    }
                }
            }
        }
        for (BetaNode betaNode : betaNodesFullList) {
            if(betaNode.getParent1().getMemory().contains(sampleID) && betaNode.getParent2().getMemory().contains(sampleID)) {
                if (!betaNode.getMemory().contains(sampleID)) {
                    betaNode.getMemory().add(sampleID);
                }
                if (listFlattener(betaNode.getValue()).size() == fact.size()*3) {
                    //mette in uscita una sola lista data dalla fusione delle due liste di valori dei nodi padre
                    outputList.addAll(betaNode.getParent1().getValue());
                    outputList.addAll(betaNode.getParent2().getValue());
                    System.out.println("OUTPUT: " + listFlattener(outputList));

                    //reset della lista
                    outputList.clear();
                }
            }
        }
        if (deleteMemory) {
            deleteNodesMemory(sampleID);
        }
    }

    //data una query in ingresso (solo le condizioni della query), genera una lista in cui ogni elemento e' un elemento della query. Se trova ";" genera sottoliste che hanno come elementi gli elementi di ogni query.
    private List<List<String>> queryToList(String string) {
        List<List<String>> outString = new ArrayList<>();
        List<String> fullList = Arrays.asList(string.split("\\s+"));
        for (int i = 1; i < fullList.size()+2; i++) {
            //TODO: sistemare
            //se non e' il carattere ";" (carattere che separa gli lhs), inserisce l'lhs nella lista (come sottolista)
            if (i%4 == 0) {
                outString.add(Arrays.asList(fullList.get(i-4), fullList.get(i-3), fullList.get(i-2)));
            }
        }
        return outString;
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

    //converte una stringa in lista
    private List<String> stringToList(String string) {
        return Arrays.asList(string.split("\\s+"));        
    }

    //sostituisce alle costanti, le costanti passate in ingresso al metodo 'findMatch' (pattern) per eseguire la ricorsione con solo le costanti cercate
    public static List<Object> replaceConstants(List<?> list1, List<String> list2) {
        List<Object> result = new ArrayList<>();
        String variableName = new String();

        for (int i = 0; i < list1.size(); i++) {
            if (list2.get(i).startsWith("?")) {
                result.add(list1.get(i));
            } else {
                result.add(list2.get(i));
            }
        }
        return result;
    }



    //sostituisce alle variabili
    public List<List<String>> replaceVariables(List<List<String>> list, Object value) {
        for (List<String> innerList : list) {
            for (int i = 0; i < innerList.size(); i++) {
                String element = innerList.get(i);
                if (isVariable(element)) {
                    innerList.set(i, value.toString());
                }
            }
        }

        return list;
    }

    //restituisce true se la stringa in ingresso e' una variabile
    private boolean isVariable(String string) {
        return string.startsWith("?") || string.startsWith("$");
    }

    //restituisce il nodo che contiene il lhs specificato. Altrimenti restituisce null
    private AlphaNode findAlphaValue(List<AlphaNode> nodeList, Object value) {
        for (AlphaNode node : nodeList) {
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
    private void deleteNodesMemory(Object sampleID) {
        for (AlphaNode alphaNode : alphaNodesFullList) {
            alphaNode.deleteMemory(sampleID);

        }
        for (BetaNode betaNode : betaNodesFullList) {
            betaNode.deleteMemory(sampleID);
        }
    }

    //data una lista di sottoliste restituisce una singola lista
    public List<?> listFlattener(List<?> list) {
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
        System.out.println("alphaNodes: " + getNumAlphaNodes());
        for (AlphaNode alphaNode : alphaNodesFullList) {
            System.out.print(" Value:" + alphaNode.getValue().toString());
            System.out.print(" Memory:" + alphaNode.getMemory().toString());
            System.out.println();
        }
        System.out.println();
        System.out.println("betaNodes: " + getNumBetaNodes());
        for (BetaNode betaNode : betaNodesFullList) {
            System.out.print(" Value:" + betaNode.getValue().toString());
            System.out.print(" Memory:" + betaNode.getMemory().toString());
            System.out.println();
        }
    }

    //selettori
    public int getNumAlphaNodes() {
        return this.alphaNodesFullList.size();  //non utilizzo 'alphaNodesFullList.size()' perche' ho pensato fosse piu' efficiente così. Stessa cosa per gli altri
    }

    public int getNumBetaNodes() {
        return this.betaNodesFullList.size();
    }

    public int getNumTotNodes() {
        return getNumAlphaNodes() + getNumBetaNodes();
    }

    public List<AlphaNode> getAlphaNodesList() {
        return this.alphaNodesFullList;
    }

    public List<BetaNode> getBetaNodesList() {
        return this.betaNodesFullList;
    }
}