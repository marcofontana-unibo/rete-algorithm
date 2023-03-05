import java.util.ArrayList;
import java.util.List;

public class ToolboxList {

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

    public List<String> toListOfStrings(List<Object> list) {
        List<String> out = new ArrayList<>();
        for (Object listElement : list) {
            out.add(listElement.toString());
        }
        return out;
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
}
