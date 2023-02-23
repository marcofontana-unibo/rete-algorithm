import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        
        //tuple dentro rete (tuple non necessariamente da 3, basta che siano > 1)
        List<String> antecedent1 = Arrays.asList("TheDiamondAge", "is-written-by","NealSpehenson");
        List<String> antecedent2 = Arrays.asList("NealSpephenson","is-a","science-fiction-writer");
        List<String> antecedent3 = Arrays.asList("TheDiamongAge","is-a","book");
        List<List<String>> antecedents = Arrays.asList(antecedent1, antecedent2, antecedent3);
        List<String> antecedent4 = Arrays.asList("Neuromancer", "is-written-by","WilliamGibson");
        List<String> antecedent5 = Arrays.asList("WilliamGibson","is-a","science-fiction-writer");
        List<String> antecedent6 = Arrays.asList("TheDiamongAge","is-a","book");
        List<List<String>> antecedents2 = Arrays.asList(antecedent4, antecedent5, antecedent6);

        //creazione dei nodi
        System.out.println("-----RETE-----");
        long start = System.nanoTime();
        rete.updateRete(antecedents);
        long finish = System.nanoTime();
        System.out.println("BUILD 1: " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
        System.out.println();

        start = System.nanoTime();
        rete.updateRete(antecedents2);
        finish = System.nanoTime();
        System.out.println("BUILD 2: " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
        System.out.println();
        
        //mette in uscita tutte le tuple (pattern) tra quelle dentro rete che rispettano il match
        System.out.println("-----OUT-----");
        String pattern = "TheDiamondAge is-written-by NealSpehenson ; NealSpephenson is-a science-fiction-writer ; TheDiamongAge is-a book";
        List<String> patternList = Arrays.asList(pattern);  //lo metto in una lista in modo da poterlo iterare

        int i = 0;
        for (String currentPattern : patternList) {
            i++;
            System.out.println("S" + i + ":");
            start = System.nanoTime();
            rete.findMatch(currentPattern, "ID" + i, false);
            finish = System.nanoTime();
            System.out.println("TIME: " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
            System.out.println();
        }

        //controllo della rete
        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();
    }
}