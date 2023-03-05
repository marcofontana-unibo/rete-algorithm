import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //colori usati per vedere piu' facilmente se i test sono ok o falliti
        final String ANSI_RED = "\u001B[31m";       //colora l'output del terminale di rosso
        final String ANSI_GREEN = "\u001B[32m";     //colora l'output del terminale di verde
        final String ANSI_CYAN = "\u001B[36m";     //colora l'output del terminale di azzurro
        final String ANSI_RESET = "\u001B[0m";      //resetta il colore dell'output
    
        Rete rete = new Rete();
        Test test = new Test();
        
        //nuovi dati di prova
        List<String> insertData1 = Arrays.asList("TheDiamondAge", "is-written-by","NealStephenson");
        List<String> insertData2 = Arrays.asList("NealStephenson","is-a","science-fiction-writer");
        List<String> insertData3 = Arrays.asList("TheDiamondAge","is-a","book");
        List<String> insertData4 = Arrays.asList("Neuromancer", "is-written-by","WilliamGibson");
        List<String> insertData5 = Arrays.asList("WilliamGibson","is-a","science-fiction-writer");
        List<String> insertData6 = Arrays.asList("Neuromancer","is-a","book");
        List<String> insertData7 = Arrays.asList("MazeOfDeath", "is-written-by","PhilipKDick");
        List<String> insertData8 = Arrays.asList("PhilipKDick","is-a","science-fiction-writer");
        List<String> insertData9 = Arrays.asList("MazeOfDeath","is-a","book");
        List<String> insertData10 = Arrays.asList("Apple","is-a","Apple");
        List<String> insertData11 = Arrays.asList("Apple","is-a","Fruit");
        List<String> insertData12 = Arrays.asList("TheLordOfTheRings","is-directed-by","PeterJackson");
        List<String> insertData13 = Arrays.asList("TheLordOfTheRings","is-also-a","movie");
        List<List<String>> insertDataList = Arrays.asList(insertData1, insertData2, insertData3, insertData4, insertData5, insertData6, insertData7, insertData8, insertData9, insertData10, insertData11, insertData12, insertData13);
        
        //creazione rete
        System.out.println();
        System.out.println("-----RETE-----");
        int i = 0;
        for (List<String> antecedent : insertDataList) {
            i++;
            long start = System.nanoTime();
            rete.updateRete(antecedent, true);
            long finish = System.nanoTime();
            System.out.println("RETE" + i + ": " + (finish - start)*Math.pow(10, -6)+"ms");
        }
        System.out.println();
        
        //dichiarazione test
        System.out.println("-----TEST-----");
        String                  query1 = "TheDiamondAge is-written-by NealStephenson ; NealStephenson is-a science-fiction-writer ; TheDiamondAge is-a book";
        List<Object>    expectedOutput1 = new ArrayList<>(); 
                        expectedOutput1.add("[TheDiamondAge, is-written-by, NealStephenson], [NealStephenson, is-a, science-fiction-writer], [TheDiamondAge, is-a, book]");
        String                  query2 = "Neuromancer is-written-by WilliamGibson ; WilliamGibson is-a science-fiction-writer ; Neuromancer is-a book";
        List<Object>    expectedOutput2 = new ArrayList<>(); 
                        expectedOutput2.add("[Neuromancer, is-written-by, WilliamGibson], [WilliamGibson, is-a, science-fiction-writer], [Neuromancer, is-a, book]");
        String                  query3 = "pattern not-inside rete";
        List<Object>    expectedOutput3 = new ArrayList<>(); 
                        expectedOutput3.add("");
        String                  query4 = "TheDiamondAge is-written-by NealStephenson ; NealStephenson is-a science-fiction-writer";
        List<Object>    expectedOutput4 = new ArrayList<>(); 
                        expectedOutput4.add("[TheDiamondAge, is-written-by, NealStephenson], [NealStephenson, is-a, science-fiction-writer]");
        String                  query5 = "TheDiamondAge is-written-by NealStephenson";
        List<Object>    expectedOutput5 = new ArrayList<>(); 
                        expectedOutput5.add("[TheDiamondAge, is-written-by, NealStephenson]");
        String                  query6 = "Neuromancer is-written-by WilliamGibson ; WilliamGibson is-a science-fiction-writer ; Neuromancer is-a book";
        List<Object>    expectedOutput6 = new ArrayList<>(); 
                        expectedOutput6.add("[Neuromancer, is-written-by, WilliamGibson], [WilliamGibson, is-a, science-fiction-writer], [Neuromancer, is-a, book]");
        String                  query7 = "?x ?y ?z";
        List<Object>    expectedOutput7 = new ArrayList<>(); 
                        expectedOutput7.add("[TheDiamondAge, is-written-by, NealStephenson], [NealStephenson, is-a, science-fiction-writer], [TheDiamondAge, is-a, book], [Neuromancer, is-written-by, WilliamGibson], [WilliamGibson, is-a, science-fiction-writer], [Neuromancer, is-a, book], [MazeOfDeath, is-written-by, PhilipKDick], [PhilipKDick, is-a, science-fiction-writer], [MazeOfDeath, is-a, book], [Apple, is-a, Fruit], [TheLordOfTheRings, is-directed-by, PeterJackson], [TheLordOfTheRings, is-also-a, movie]");
        String                  query8 = "?x ?y ?x";
        List<Object>    expectedOutput8 = new ArrayList<>(); 
                        expectedOutput8.add("[Apple, is-a, Apple]");
        String                  query9 = "?x ?x ?x";
        List<Object>    expectedOutput9 = new ArrayList<>(); 
                        expectedOutput9.add("");
        String                  query10 = "TheDiamondAge ?x ?y";
        List<Object>    expectedOutput10 = new ArrayList<>(); 
                        expectedOutput10.add("[TheDiamondAge, is-written-by, NealStephenson], [TheDiamondAge, is-a, book]");
        String                  query11 = "?x pattern-not-inside-rete ?x";
        List<Object>    expectedOutput11 = new ArrayList<>(); 
                        expectedOutput11.add("");
        String                  query12 = "?x pattern-not-inside-rete ?y";
        List<Object>    expectedOutput12 = new ArrayList<>(); 
                        expectedOutput12.add("");
        String                  query13 = "?x is-a ?x";
        List<Object>    expectedOutput13 = new ArrayList<>(); 
                        expectedOutput13.add("[Apple, is-a, Apple]");
        String                  query14 = "?x is-written-by ?y";
        List<Object>    expectedOutput14 = new ArrayList<>(); 
                        expectedOutput14.add("[TheDiamondAge, is-written-by, NealStephenson], [Neuromancer, is-written-by, WilliamGibson], [MazeOfDeath, is-written-by, PhilipKDick]");
        String                  query15 = "?x is-written-by WilliamGibson";
        List<Object>    expectedOutput15 = new ArrayList<>(); 
                        expectedOutput15.add("[Neuromancer, is-written-by, WilliamGibson]");
        String                  query16 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?x is-a book";
        List<Object>    expectedOutput16 = new ArrayList<>(); 
                        expectedOutput16.add("[TheDiamondAge, is-written-by, NealStephenson], [NealStephenson, is-a, science-fiction-writer], [TheDiamondAge, is-a, book], [Neuromancer, is-written-by, WilliamGibson], [WilliamGibson, is-a, science-fiction-writer], [Neuromancer, is-a, book], [MazeOfDeath, is-written-by, PhilipKDick], [PhilipKDick, is-a, science-fiction-writer], [MazeOfDeath, is-a, book]");
        String                  query17 = "?x is-written-by ?y ; ?y is-a science-fiction-writer ; ?x is-a movie";
        List<Object>    expectedOutput17 = new ArrayList<>(); 
                        expectedOutput17.add("");
        String                  query18 = "TheLordOfTheRings is-directed-by PeterJackson ; is-also-a movie";
        List<Object>    expectedOutput18 = new ArrayList<>(); 
                        expectedOutput18.add("[TheLordOfTheRings, is-directed-by, PeterJackson], [TheLordOfTheRings, is-also-a, movie]");
        String                  query19 = "?x is-directed-by PeterJackson ; ?y movie";
        List<Object>    expectedOutput19 = new ArrayList<>(); 
                        expectedOutput19.add("[TheLordOfTheRings, is-directed-by, PeterJackson], [TheLordOfTheRings, is-also-a, movie]");
        String                  query20 = "?x is-directed-by PeterJackson ; ?x movie";
        List<Object>    expectedOutput20 = new ArrayList<>(); 
                        expectedOutput20.add("");
        
        List<String> queryList = Arrays.asList(query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11, query12, query13, query14, query15, query16, query17, query18, query19, query20);
        List<List<Object>> expectedOutput = Arrays.asList(expectedOutput1, expectedOutput2, expectedOutput3, expectedOutput4, expectedOutput5, expectedOutput6, expectedOutput7, expectedOutput8, expectedOutput9, expectedOutput10, expectedOutput11, expectedOutput12, expectedOutput13, expectedOutput14, expectedOutput15, expectedOutput16, expectedOutput17, expectedOutput18, expectedOutput19, expectedOutput20);

        //esecuzione test
        List<List<Object>> reteOutput = new ArrayList<>();
        int numOk = 0;
        int numFailed = 0;
        i = 0;
        for (String currentPattern : queryList) {
            i++;
            long start = System.nanoTime();
            reteOutput = rete.findMatch(currentPattern);
            long finish = System.nanoTime();
            //System.out.println("DEBUG: " + expectedOutput.get(i));
            boolean testOk = test.testOutput(reteOutput, expectedOutput.get(i-1));
            if (!testOk) {
                System.out.println(ANSI_RED + "TEST FAILED (S" + i + "):" + ANSI_RESET);
                System.out.println("INPUT: " + currentPattern);
                System.out.println("OUTPUT: " + reteOutput);
                System.out.println("EXPCTD: " + expectedOutput.get(i-1));
                numFailed++;
            } else {
                System.out.println(ANSI_GREEN + "TEST OK! (S" + i + "):" + ANSI_RESET);
                System.out.println("INPUT: " + currentPattern);
                //System.out.println("OUTPUT: " + reteOutput);
                //System.out.println("EXCPTD: " + expectedOutput.get(i-1));
                numOk++;
            }
            System.out.println("TIME: " + (finish - start)*Math.pow(10, -6)+"ms");
            System.out.println();
        }
        System.out.println(ANSI_CYAN + "RESULT: " + numOk + "/" + (numOk + numFailed) + ANSI_RESET);
        System.out.println();

        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();
        
    }
}