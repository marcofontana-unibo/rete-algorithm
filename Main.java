import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*TODO LIST:
* RICONTROLLA EXPECTED NEI TEST 18 A 20
* PER S16, RICONTROLLA LE COSTANTI CHE PASSI AL METODO PER SOSTITUIRLE, OPPURE CONTROLLA SE CI SONO ERRORI CON IL METODO (DUBITO)
*/

public class Main {
    public static void main(String[] args) {
        //colori usati per vedere piu' facilmente se i test sono ok o falliti
        final String ANSI_RED = "\u001B[31m";       //colora l'output del terminale di rosso
        final String ANSI_GREEN = "\u001B[32m";     //colora l'output del terminale di verde
        final String ANSI_RESET = "\u001B[0m";      //resetta il colore dell'output
    
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
        List<String> antecedent14 = Arrays.asList("TheLordOfTheRings","is-directed-by","PeterJackson");
        List<String> antecedent15 = Arrays.asList("TheLordOfTheRings","is-also-a","movie");
        List<List<String>> antecedents5 = Arrays.asList(antecedent14, antecedent15);
        List<List<List<String>>> antecedents = Arrays.asList(antecedents1, antecedents2, antecedents3, antecedents4, antecedents5);
        
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
        List<Object> expectedOutput7 = new ArrayList<>(); expectedOutput7.add("TheDiamondAge, is-written-by, NealSpephenson, NealSpephenson, is-a, science-fiction-writer, TheDiamondAge, is-a, book, Neuromancer, is-written-by, WilliamGibson, WilliamGibson, is-a, science-fiction-writer, Neuromancer, is-a, book, MazeOfDeath, is-written-by, PhilipKDick, PhilipKDick, is-a, science-fiction-writer, MazeOfDeath, is-a, book, PhilipKDick, is-a, book, TheLordOfTheRings, is-directed-by, PeterJackson, TheLordOfTheRings, is-also-a, movie");
        String pattern8 = "?x ?y ?x";
        List<Object> expectedOutput8 = new ArrayList<>(); expectedOutput8.add("PhilipKDick, is-written-by, PhilipKDick, MazeOfDeath, is-a, MazeOfDeath");
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
        String pattern18 = "TheLordOfTheRings is-directed-by PeterJackson ; is-also-a movie";
        List<Object> expectedOutput18 = new ArrayList<>(); expectedOutput18.add("TheLordOfTheRings, is-directed-by, PeterJackson, TheLordOfTheRings, is-also-a, movie");
        String pattern19 = "?x is-directed-by PeterJackson ; ?y movie";
        List<Object> expectedOutput19 = new ArrayList<>(); expectedOutput19.add("TheLordOfTheRings, is-directed-by, PeterJackson, TheLordOfTheRings, is-also-a, movie");
        String pattern20 = "?x is-directed-by PeterJackson ; ?x movie";
        List<Object> expectedOutput20 = new ArrayList<>(); expectedOutput20.add("");
        String pattern21 = "?x ?y ?z ; ?t ?k";
        List<Object> expectedOutput21 = new ArrayList<>(); expectedOutput21.add("TheDiamondAge, is-written-by, NealSpephenson, NealSpephenson, is-a, science-fiction-writer, TheDiamondAge, is-a, book, Neuromancer, is-written-by, WilliamGibson, TheLordOfTheRings, is-directed-by, PeterJackson, TheLordOfTheRings, is-also-a, movie");
        String pattern22 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?z is-a book";
        List<Object> expectedOutput22 = new ArrayList<>(); expectedOutput22.add("");

        List<String> patternList = Arrays.asList(pattern1, pattern2, pattern3, pattern4, pattern5, pattern6, pattern7, pattern8, pattern9, pattern10, pattern11, pattern12, pattern13, pattern14, pattern15, pattern16, pattern17, pattern18, pattern19, pattern20, pattern21, pattern22);
        List<List<Object>> expectedOutput = Arrays.asList(expectedOutput1, expectedOutput2, expectedOutput3, expectedOutput4, expectedOutput5, expectedOutput6, expectedOutput7, expectedOutput8, expectedOutput9, expectedOutput10, expectedOutput11, expectedOutput12, expectedOutput13, expectedOutput14, expectedOutput15, expectedOutput16, expectedOutput17, expectedOutput18, expectedOutput19, expectedOutput20, expectedOutput21, expectedOutput22);

        i = 0;
        for (String currentPattern : patternList) {
            i++;
            long start = System.nanoTime();
            reteOutput = rete.findMatch(currentPattern, "ID" + i);
            long finish = System.nanoTime();
            //System.out.println("DEBUG: " + expectedOutput.get(i));
            boolean testOk = test.checkOutput(reteOutput, expectedOutput.get(i-1));
            if (!testOk) {
                System.out.println(ANSI_RED + "TEST FAILED (S" + i + "):" + ANSI_RESET);
                System.out.println("INPUT: " + currentPattern);
                System.out.println("OUTPUT: " + reteOutput);
                System.out.println("EXPCTD: " + expectedOutput.get(i-1));
            } else {
                System.out.println(ANSI_GREEN + "TEST OK! (S" + i + "):" + ANSI_RESET);
                System.out.println("INPUT: " + currentPattern);
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