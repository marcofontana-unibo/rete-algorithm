import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//PROVARE CON DEI HASHSET INVECE DI LISTE PER EVITARE DUPLICATI

public class TestMaster {
    public static void main(String[] args) {
        //colori usati per vedere piu' facilmente se i test sono ok o falliti
        final String ANSI_RED = "\u001B[31m";       //colora l'output del terminale di rosso
        final String ANSI_GREEN = "\u001B[32m";     //colora l'output del terminale di verde
        final String ANSI_CYAN = "\u001B[36m";      //colora l'output del terminale di azzurro
        final String ANSI_RESET = "\u001B[0m";      //resetta il colore dell'output

        Rete rete = new Rete();
        
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
            //System.out.println("TIME" + i + ": " + (finish - start)*Math.pow(10, -6)+"ms");
            System.out.println("TIME UPDATE " + i + ": " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
        }
        System.out.println();
        
        //dichiarazione test
        System.out.println("-----TEST-----");
        
        String query1 = "?sub ?pred ?obj";
        String expectedOutput1 = "{sub=[TheDiamondAge, NealStephenson, TheDiamondAge, Neuromancer, WilliamGibson, Neuromancer, MazeOfDeath, PhilipKDick, MazeOfDeath, Apple, TheLordOfTheRings, TheLordOfTheRings], pred=[is-written-by, is-a, is-a, is-written-by, is-a, is-a, is-written-by, is-a, is-a, is-a, is-directed-by, is-also-a], obj=[NealStephenson, science-fiction-writer, book, WilliamGibson, science-fiction-writer, book, PhilipKDick, science-fiction-writer, book, Fruit, PeterJackson, movie]}";
        
        String query2 = "?x ?y ?x";
        String expectedOutput2 = "{x=[Apple, Apple], y=[is-a]}";
        
        String query3 = "?x ?x ?x";
        String expectedOutput3 = "{}";
        
        String query4 = "TheDiamondAge ?x ?y";
        String expectedOutput4 = "{x=[is-written-by, is-a], y=[NealStephenson, book]}";
        
        String query5 = "?x pattern-not-inside-rete ?x";
        String expectedOutput5 = "{}";
        
        String query6 = "?x pattern-not-inside-rete ?y";
        String expectedOutput6 = "{}";
        
        String query7 = "?x is-a ?x";
        String expectedOutput7 = "{x=[Apple, Apple]}";
        
        String query8 = "?x is-written-by ?y";
        String expectedOutput8 = "{x=[TheDiamondAge, Neuromancer, MazeOfDeath], y=[NealStephenson, WilliamGibson, PhilipKDick]}";
        
        String query9 = "?x is-written-by WilliamGibson";
        String expectedOutput9 = "{x=[Neuromancer]}";
        
        String query10 = "?x is-written-by ?y . ?y is-a science-fiction-writer . ?x is-a book";
        String expectedOutput10 = "{x=[TheDiamondAge, TheDiamondAge, Neuromancer, Neuromancer, MazeOfDeath, MazeOfDeath], y=[NealStephenson, NealStephenson, WilliamGibson, WilliamGibson, PhilipKDick, PhilipKDick]}";
        
        String query11 = "?x is-written-by ?y . ?y is-a science-fiction-writer . ?x is-a movie";
        String expectedOutput11 = "{}";
                
        String query12 = "?x is-directed-by PeterJackson ; ?y movie";
        String expectedOutput12 = "{x=[TheLordOfTheRings, TheLordOfTheRings], y=[is-also-a]}";
        
        String query13 = "?x is-directed-by PeterJackson ; ?x movie";
        String expectedOutput13 = "{}";
        
        List<String> queryList = Arrays.asList(query1, query2, query3, query4, query5, query6, query7, query8, query9, query10, query11, query12, query13);
        List<Object> expectedOutput = Arrays.asList(expectedOutput1, expectedOutput2, expectedOutput3, expectedOutput4, expectedOutput5, expectedOutput6, expectedOutput7, expectedOutput8, expectedOutput9, expectedOutput10, expectedOutput11, expectedOutput12, expectedOutput13);

        //esecuzione test
        Map<String, List<Object>> reteOutput = new HashMap<>();
        int numOk = 0;
        int numFailed = 0;
        i = 0;
        for (String currentPattern : queryList) {
            i++;
            long start = System.nanoTime();
            reteOutput = rete.findMatch(currentPattern);
            long finish = System.nanoTime();
            //System.out.println("DEBUG: " + expectedOutput.get(i));
            boolean testOk = reteOutput.toString().equals(expectedOutput.get(i-1).toString());
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
            System.out.println("TIME: " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
            //togliere commento alla prossima riga per valore preciso
            //System.out.println("TIME: " + (finish - start)*Math.pow(10, -6)+"ms");
            System.out.println();
        }
        System.out.println(ANSI_CYAN + "RESULT: " + numOk + "/" + (numOk + numFailed) + ANSI_RESET);
        System.out.println();
        
        /*
        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();
        */
    }
}

