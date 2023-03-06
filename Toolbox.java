import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Toolbox {
    
    /* METODI PER MODIFICARE LISTE */

    //converte una lista in una stringa
    public String listToString(List<?> list) {
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
    public List<List<Object>> listToListOfLists(List<Object> list, int num) {
        
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
    public String listOfListsToString(List<List<Object>> list) {
        StringBuilder out = new StringBuilder();
        for (List<?> currentList : list) {
            for (int i = 0; i < currentList.size(); i++) {
                if (i == 0) {
                    out.append(currentList.get(i));
                } else {
                    out.append(" ").append(currentList.get(i));
                }
            }
            out.append(" ");
        }
        return out.toString().trim();
    }
    
    //mette in uscita una lista che contiene solamente elementi che appaiono una unica volta nella lista in ingresso
    public List<Object> removeDoubles(List<Object> list) {
        List<Object> out = new ArrayList<>();

        //aggiunge alla lista di uscita elementi non gia' inseriti prima
        for (Object element : list) {
            if (!out.contains(element)) {
                out.add(element);
            }
        }

        return out;
    }

    //data una lista di liste restituisce una singola lista
    public List<Object> listFlattener(List<?> list) {
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

    //cambia classe della lista da oggetto a stringa
    public List<String> changeToListOfString(List<Object> list) {
        List<String> out = new ArrayList<>();

        for (Object listElement : list) {
            out.add(listElement.toString());
        }
        
        return out;
    }

    //cambia classe della lista di liste da oggetto a stringa
    public List<List<String>> changeToListOfListsOfString(List<List<Object>> list) {
        List<List<String>> out = new ArrayList<>();
    
        for (List<Object> currentList : list) {

            //reset della lista ad ogni iterazione
            List<String> tmp = new ArrayList<>();

            for (Object currentElement : currentList) {
                tmp.add(currentElement.toString());
            }
            
            out.add(tmp);
        }
    
        return out;
    }

    /* METODI PER GESTIRE QUERY */

    //restituisce true se la stringa in ingresso e' una variabile
    public boolean isVariable(String string) {
        return string.startsWith("?") || string.startsWith("$");
    }

    //come il metodo 'isVariable', ma controlla stringhe all'interno di liste di liste
    public boolean containsVariable(List<List<String>> list) {
        for (List<String> currentList : list) {
            for (String currentElement : currentList) {
                if ((currentElement.startsWith("?") || currentElement.startsWith("$"))) {
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
    public String queryfy(String string) {
        String[] words = string.split("\\s+");
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
    public List<List<Object>> ver(List<List<Object>> listToCheck, List<List<String>> inputList) {

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

    /* METODI PER MODIFICARE STRINGHE */

    //converte una stringa in lista
    public List<String> stringToList(String string) {
        return Arrays.asList(string.split("\\s+"));
    }

    //genera un token unico associato alla stringa in ingresso, usa una mappa con key le stringhe passate in precedenza e i value precedentemente associati, se esiste gia' una key con la stessa stringa restituisce il value associato
    public int tokenization(String string, Map<String, Integer> map) {
        Random random = new Random();
        int token = 0;

        if (map.containsKey(string)) {
            return map.get(string);

        } else {

            do {
                token = random.nextInt(Integer.MAX_VALUE-1);
            } while (map.containsValue(token));

            map.put(string, token);
        }
        //System.out.println(this.tokenMap);
        return map.get(string);
    }
}
