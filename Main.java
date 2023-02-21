import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        
        //tuple dentro rete (tuple non necessariamente da 3, basta che siano > 1)
        List<Object> tuple1 = Arrays.asList("test:email", "test:a", "'marco'");
        List<Object> tuple2 = Arrays.asList("test:email", "test:a", "'gregorio'");
        List<Object> tuple3 = Arrays.asList("test:email", "test:a", "'gianluca'");
        List<Object> tuple4 = Arrays.asList("test:email", "test:verso", "'marco'");
        List<Object> tuple5 = Arrays.asList("test:email", "test:verso", "'gregorio'");
        List<Object> tuple6 = Arrays.asList("test:posta", "test:verso", "'gianluca'");
        List<Object> tuple7 = Arrays.asList("test:posta", "test:verso", "'marco'");
        List<List<Object>> tuples = Arrays.asList(tuple1, tuple2, tuple3, tuple4, tuple5, tuple6, tuple7);

        //creazione dei nodi
        System.out.println("-----RETE-----");
        int i = 0;
        for (List<Object> tuple : tuples) {
            i++;
            long start = System.nanoTime();
            rete.updateRete(tuple);
            long finish = System.nanoTime();
            System.out.println("BUILD" + i + ": " + Math.round((finish - start)*Math.pow(10, -6))+"ms");
        }
        System.out.println();
        
        //mette in uscita tutte le tuple (pattern) tra quelle dentro rete che rispettano il match
        System.out.println("-----OUT-----");
        String patternQuery1 = "test:email test:a 'marco'";
        String patternQuery2 = "test:email test:verso 'marco'";
        String patternQuery3 = "test:email ?y 'marco'";
        String patternQuery4 = "?x ?y ?z";
        String patternQuery5 = "pattern non presente all'interno di rete";
        List<String> patterns = Arrays.asList(patternQuery1, patternQuery2, patternQuery3, patternQuery4, patternQuery5);

        i = 0;
        for (String pattern : patterns) {
            i++;
            System.out.println("S" + i + ":");
            long start = System.nanoTime();
            rete.findMatch(pattern, "ID" + i, false);
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