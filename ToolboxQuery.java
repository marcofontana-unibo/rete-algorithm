import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolboxQuery {

    //restituisce true se la stringa in ingresso e' una variabile
    public boolean isVariable(String string) {
        return string.startsWith("?") || string.startsWith("$");
    }

    //come il metodo 'isVariable', ma controlla stringhe all'interno di liste di liste
    public boolean containsVariable(List<List<String>> list) {
        for (List<String> sublist : list) {
            for (String string : sublist) {
                if ((string.startsWith("?") || string.startsWith("$"))) {
                    return true;
                }
            }
        }
        return false;
    }

    //data una stringa che rappresenta una query mette in uscita una lista dove ogni elemento e' composto da soggetto predicato e oggetto
    public List<List<String>> queryToList(String string) {
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
    public String queryfy(String input) {
        String[] words = input.split("\\s+");
        StringBuilder out = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            out.append(words[i]);
            if ((i + 1) % 3 == 0 && i != words.length - 1) {
                out.append(" ; ");
            } else {
                out.append(" ");
            }
        }
        return out.toString();
    }

    //sostituisce le variabili di listWithVariables con le costanti di listWithConstants, mette in uscita la lista che aveva variabili sostituite con costanti (per effettuare la ricorsione del metodo 'findMatch' con solo costanti)
    public List<Object> replaceListVariablesWithConstants(List<?> listWithConstants, List<String> listWithVariables) {
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
    public List<List<Object>> replaceListOfListsVariablesWithConstants(List<?> listWithConstants, List<List<String>> listWithVariables) {
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
    public List<List<Object>> checkOutput(List<List<Object>> listToCheck, List<List<String>> inputList) {

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

}
