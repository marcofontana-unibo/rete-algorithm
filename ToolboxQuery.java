import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
