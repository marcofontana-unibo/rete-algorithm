import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        
        //dichiarazione antecedents
        List<String> antecedent1 = Arrays.asList("TheDiamondAge", "is-written-by","NealSpehenson");
        List<String> antecedent2 = Arrays.asList("NealSpephenson","is-a","science-fiction-writer");
        List<String> antecedent3 = Arrays.asList("TheDiamondAge","is-a","book");
        List<List<String>> antecedents1 = Arrays.asList(antecedent1, antecedent2, antecedent3);
        List<String> antecedent4 = Arrays.asList("Neuromancer", "is-written-by","WilliamGibson");
        List<String> antecedent5 = Arrays.asList("WilliamGibson","is-a","science-fiction-writer");
        List<String> antecedent6 = Arrays.asList("Neuromancer","is-a","book");
        List<String> antecedent7= Arrays.asList("TheDiamondAge","is-a","book");
        List<List<String>> antecedents2 = Arrays.asList(antecedent4, antecedent5, antecedent6, antecedent7);
        List<String> antecedent8 = Arrays.asList("MazeOfDeath", "is-written-by","PhilipKDick");
        List<String> antecedent9 = Arrays.asList("PhilipKDick","is-a","science-fiction-writer");
        List<String> antecedent10 = Arrays.asList("MazeOfDeath","is-a","book");
        List<List<String>> antecedents3 = Arrays.asList(antecedent8, antecedent9, antecedent10);
        List<String> antecedent11 = Arrays.asList("PhilipKDick", "is-written-by","PhilipKDick");
        List<String> antecedent12 = Arrays.asList("MazeOfDeath","is-a","MazeOfDeath");
        List<String> antecedent13 = Arrays.asList("PhilipKDick","is-a","book");
        List<List<String>> antecedents4 = Arrays.asList(antecedent11, antecedent12, antecedent13);
        List<List<List<String>>> antecedents = Arrays.asList(antecedents1, antecedents2, antecedents3, antecedents4);
        
        //creazione dei nodi
        System.out.println();
        System.out.println("-----RETE-----");
        int i = 0;
        for (List<List<String>> antecedent : antecedents) {
            i++;
            long start = System.nanoTime();
            rete.updateRete(antecedent);
            long finish = System.nanoTime();
            System.out.println("RETE" + i + ": " + (finish - start)*Math.pow(10, -6)+"ms");
        }
        System.out.println();
        
        //mette in uscita tutte le tuple (pattern) tra quelle dentro rete che rispettano il match
        System.out.println("-----TEST-----");
        String pattern1 = "TheDiamondAge is-written-by NealSpehenson ; NealSpephenson is-a science-fiction-writer ; TheDiamondAge is-a book";
        String pattern2 = "Neuromancer is-written-by WilliamGibson ; WilliamGibson is-a science-fiction-writer ; Neuromancer is-a book";
        String pattern3 = "pattern not-inside rete";
        String pattern4 = "TheDiamondAge is-written-by NealSpehenson ; NealSpephenson is-a science-fiction-writer";
        String pattern5 = "TheDiamondAge is-written-by NealSpehenson";
        String pattern6 = "Neuromancer is-written-by WilliamGibson ; WilliamGibson is-a science-fiction-writer ; Neuromancer is-a book ; TheDiamondAge is-a book";
        String pattern7 = "?x ?y ?z";
        String pattern8 = "?x ?y ?x";
        String pattern9 = "?x ?x ?x";
        String pattern10 = "?x pattern-not-inside-rete ?x";
        String pattern11 = "?x pattern-not-inside-rete ?y";
        String pattern12 = "?x is-written-by ?x";
        String pattern13 = "?x is-written-by ?y";
        String pattern14 = "?x is-written-by ?y ; ?x ?k ?w";
        String pattern15 = "?x is-written-by ?y ; ?t ?k ?w";
        String pattern16 = "?x is-written-by WilliamGibson";
        String pattern17 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?x is-a book";
        String pattern18 = "?x is-written-by ?y ; is-written-by ?y";

        List<String> patternList = Arrays.asList(pattern1, pattern2, pattern3, pattern4, pattern5, pattern6, pattern7, pattern8, pattern9, pattern10, pattern11, pattern12, pattern13, pattern14, pattern15, pattern16, pattern17, pattern18);

        i = 0;
        for (String currentPattern : patternList) {
            i++;
            System.out.println("S" + i + ":");
            System.out.println("INPUT: " + currentPattern);
            long start = System.nanoTime();
            rete.findMatch(currentPattern, "ID" + i, true);
            long finish = System.nanoTime();
            System.out.println("TIME: " + (finish - start)*Math.pow(10, -6)+"ms");
            System.out.println();
        }

        //controllo della rete
        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();
    }
}