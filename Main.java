import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*TODO LIST:
*   SISTEMARE CASO MAP VARIABILI DIVERSE PER N TRIPLE
*/

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        Test test = new Test();
        List<Object> reteOutput = new ArrayList<>();

        
        //dichiarazione antecedents
        List<String> antecedent1 = Arrays.asList("TheDiamondAge", "is-written-by","NealSpephenson");
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
        String pattern1 = "TheDiamondAge is-written-by NealSpephenson ; NealSpephenson is-a science-fiction-writer ; TheDiamondAge is-a book";
        List<Object> expectedOutput1 = new ArrayList<>(); expectedOutput1.add("TheDiamondAge, is-written-by, NealSpephenson, NealSpephenson, is-a, science-fiction-writer, TheDiamondAge, is-a, book");
        String pattern2 = "Neuromancer is-written-by WilliamGibson ; WilliamGibson is-a science-fiction-writer ; Neuromancer is-a book";
        List<Object> expectedOutput2 = new ArrayList<>(); expectedOutput2.add("Neuromancer, is-written-by, WilliamGibson, WilliamGibson, is-a, science-fiction-writer, Neuromancer, is-a, book");
        String pattern3 = "pattern not-inside rete";
        List<Object> expectedOutput3 = new ArrayList<>(); expectedOutput3.add("");
        String pattern4 = "TheDiamondAge is-written-by NealSpephenson ; NealSpephenson is-a science-fiction-writer";
        List<Object> expectedOutput4 = new ArrayList<>(); expectedOutput4.add("TheDiamondAge, is-written-by, NealSpephenson, NealSpephenson, is-a, science-fiction-writer");
        String pattern5 = "TheDiamondAge is-written-by NealSpephenson";
        List<Object> expectedOutput5 = new ArrayList<>(); expectedOutput5.add("TheDiamondAge, is-written-by, NealSpephenson");
        String pattern6 = "Neuromancer is-written-by WilliamGibson ; WilliamGibson is-a science-fiction-writer ; Neuromancer is-a book ; TheDiamondAge is-a book";
        List<Object> expectedOutput6 = new ArrayList<>(); expectedOutput6.add("Neuromancer, is-written-by, WilliamGibson, WilliamGibson, is-a, science-fiction-writer, Neuromancer, is-a, book, TheDiamondAge, is-a, book");
        String pattern7 = "?x ?y ?z";
        List<Object> expectedOutput7 = new ArrayList<>(); expectedOutput7.add("TheDiamondAge, is-written-by, NealSpephenson, NealSpephenson, is-a, science-fiction-writer, TheDiamondAge, is-a, book, Neuromancer, is-written-by, WilliamGibson, WilliamGibson, is-a, science-fiction-writer, Neuromancer, is-a, book, MazeOfDeath, is-written-by, PhilipKDick, PhilipKDick, is-a, science-fiction-writer, MazeOfDeath, is-a, book, PhilipKDick, is-a, book");
        String pattern8 = "?x ?y ?x";
        List<Object> expectedOutput8 = new ArrayList<>(); expectedOutput8.add("MazeOfDeath, is-a, MazeOfDeath, PhilipKDick, is-written-by, PhilipKDick, MazeOfDeath, is-a, MazeOfDeath");
        String pattern9 = "?x ?x ?x";
        List<Object> expectedOutput9 = new ArrayList<>(); expectedOutput9.add("");
        String pattern10 = "TheDiamondAge ?x ?y";
        List<Object> expectedOutput10 = new ArrayList<>(); expectedOutput10.add("TheDiamondAge, is-written-by, NealSpephenson, TheDiamondAge, is-a, book");
        String pattern11 = "?x pattern-not-inside-rete ?x";
        List<Object> expectedOutput11 = new ArrayList<>(); expectedOutput11.add("");
        String pattern12 = "?x pattern-not-inside-rete ?y";
        List<Object> expectedOutput12 = new ArrayList<>(); expectedOutput12.add("");
        String pattern13 = "?x is-written-by ?x";
        List<Object> expectedOutput13 = new ArrayList<>(); expectedOutput13.add("PhilipKDick, is-written-by, PhilipKDick");
        String pattern14 = "?x is-written-by ?y";
        List<Object> expectedOutput14 = new ArrayList<>(); expectedOutput14.add("TheDiamondAge, is-written-by, NealSpephenson, Neuromancer, is-written-by, WilliamGibson, MazeOfDeath, is-written-by, PhilipKDick");
        String pattern15 = "?x is-written-by WilliamGibson";
        List<Object> expectedOutput15 = new ArrayList<>(); expectedOutput15.add("Neuromancer, is-written-by, WilliamGibson");
        String pattern16 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?x is-a book";
        List<Object> expectedOutput16 = new ArrayList<>(); expectedOutput16.add("TheDiamondAge, is-written-by, NealSpephenson, NealSpephenson, is-a, science-fiction-writer, TheDiamondAge, is-a, book, Neuromancer, is-written-by, WilliamGibson, WilliamGibson, is-a, science-fiction-writer, Neuromancer, is-a, book, MazeOfDeath, is-written-by, PhilipKDick, PhilipKDick, is-a, science-fiction-writer, MazeOfDeath, is-a, book");
        String pattern17 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?x is-a movie";
        List<Object> expectedOutput17 = new ArrayList<>(); expectedOutput17.add("");
        String pattern18 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?z is-a book";
        List<Object> expectedOutput18 = new ArrayList<>(); expectedOutput18.add("da completare");

        List<String> patternList = Arrays.asList(pattern1, pattern2, pattern3, pattern4, pattern5, pattern6, pattern7, pattern8, pattern9, pattern10, pattern11, pattern12, pattern13, pattern14, pattern15, pattern16, pattern17, pattern18);
        List<List<Object>> expectedOutput = new ArrayList<>();
        expectedOutput.add(expectedOutput1);
        expectedOutput.add(expectedOutput2);
        expectedOutput.add(expectedOutput3);
        expectedOutput.add(expectedOutput4);
        expectedOutput.add(expectedOutput5);
        expectedOutput.add(expectedOutput6);
        expectedOutput.add(expectedOutput7);
        expectedOutput.add(expectedOutput8);
        expectedOutput.add(expectedOutput9);
        expectedOutput.add(expectedOutput10);
        expectedOutput.add(expectedOutput11);
        expectedOutput.add(expectedOutput12);
        expectedOutput.add(expectedOutput13);
        expectedOutput.add(expectedOutput14);
        expectedOutput.add(expectedOutput15);
        expectedOutput.add(expectedOutput16);
        expectedOutput.add(expectedOutput17);
        expectedOutput.add(expectedOutput18);

        i = 0;
        for (String currentPattern : patternList) {
            i++;
            System.out.println("S" + i + ":");
            System.out.println("INPUT: " + currentPattern);
            long start = System.nanoTime();
            reteOutput = rete.findMatch(currentPattern, "ID" + i, true);
            long finish = System.nanoTime();
            //System.out.println("DEBUG: " + expectedOutput.get(i));
            boolean testOk = test.checkOutput(reteOutput, expectedOutput.get(i-1));
            if (!testOk) {
                System.out.println("TEST FAILED:");
                System.out.println("OUTPUT: " + reteOutput);
                System.out.println("EXCPTD: " + expectedOutput.get(i-1));
            } else {
                System.out.println("TEST OK!");
                //System.out.println("OUTPUT: " + reteOutput);
                //System.out.println("EXCPTD: " + expectedOutput.get(i-1));
            }
            System.out.println("TIME: " + (finish - start)*Math.pow(10, -6)+"ms");
            System.out.println();
        }

        //controllo della rete
        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();
    }
}