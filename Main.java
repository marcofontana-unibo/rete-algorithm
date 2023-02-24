import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        
        //tuple dentro rete (tuple non necessariamente da 3, basta che siano > 1)
        List<String> antecedent1 = Arrays.asList("TheDiamondAge", "is-written-by","NealSpehenson");
        List<String> antecedent2 = Arrays.asList("NealSpephenson","is-a","science-fiction-writer");
        List<String> antecedent3 = Arrays.asList("TheDiamongAge","is-a","book");
        List<List<String>> antecedents1 = Arrays.asList(antecedent1, antecedent2, antecedent3);
        List<String> antecedent4 = Arrays.asList("Neuromancer", "is-written-by","WilliamGibson");
        List<String> antecedent5 = Arrays.asList("WilliamGibson","is-a","science-fiction-writer");
        List<String> antecedent6 = Arrays.asList("TheDiamongAge","is-a","book");
        List<List<String>> antecedents2 = Arrays.asList(antecedent4, antecedent5, antecedent6);
        List<String> antecedent7 = Arrays.asList("MazeOfDeath", "is-written-by","PhilipKDick");
        List<String> antecedent8 = Arrays.asList("PhilipKDick","is-a","science-fiction-writer");
        List<String> antecedent9 = Arrays.asList("MazeOfDeath","is-a","book");
        List<List<String>> antecedents3 = Arrays.asList(antecedent7, antecedent8, antecedent9);
        List<List<List<String>>> antecedents = Arrays.asList(antecedents1, antecedents2, antecedents3);
        
        //creazione dei nodi
        System.out.println("-----RETE-----");
        int i = 0;
        for (List<List<String>> antecedent : antecedents) {
            i++;
            long start = System.nanoTime();
            rete.updateRete(antecedent);
            long finish = System.nanoTime();
            System.out.println("BUILD " + i + ":" + Math.round((finish - start)*Math.pow(10, -6))+"ms");
            System.out.println();
        }
        
        //mette in uscita tutte le tuple (pattern) tra quelle dentro rete che rispettano il match
        System.out.println("-----OUT-----");
        String pattern = "TheDiamondAge is-written-by NealSpehenson ; NealSpephenson is-a science-fiction-writer ; TheDiamongAge is-a book";
        //match con pattern2 ancora non funziona
        //String pattern2 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?x is-a book";
        List<String> patternList = Arrays.asList(pattern/*, pattern2*/);

        i = 0;
        for (String currentPattern : patternList) {
            i++;
            System.out.println("S" + i + ":");
            long start = System.nanoTime();
            rete.findMatch(currentPattern, "ID" + i, false);
            long finish = System.nanoTime();
            System.out.println("TIME: " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
            System.out.println();
        }

        //controllo della rete
        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();
    }
}