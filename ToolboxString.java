import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolboxString {

    //converte una stringa in lista
    public List<String> stringToList(String string) {
        return Arrays.asList(string.split("\\s+"));
    }

    //converte una stringa in una lista di liste dove ogni elemento e' composto da 'num' parole
    public static List<List<String>> stringToListOfLists(String string, int num) {
        List<List<String>> out = new ArrayList<>();

        if (num <= 0) {
            throw new IllegalArgumentException("num must be > 0");
        }

        String[] words = string.split("\\s+");
        int index = 0;
        while (index < words.length) {
            List<String> subList = new ArrayList<>();
            for (int i = 0; i < num && index < words.length; i++) {
                subList.add(words[index++]);
            }
            out.add(subList);
        }

        return out;
    }
}
