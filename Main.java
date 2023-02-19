import java.lang.Math;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        
        //fatti (tuple non necessariamente da 3, basta che siano > 1)
        List<Object> lhs1 = Arrays.asList("email", "a", "marco");
        List<Object> lhs2 = Arrays.asList("email", "a", "gregorio");
        List<Object> lhs3 = Arrays.asList("email", "a", "gianluca");
        List<Object> lhs4 = Arrays.asList("email", "verso", "marco");
        List<Object> lhs5 = Arrays.asList("email", "verso", "gregorio");
        List<Object> lhs6 = Arrays.asList("posta", "verso", "gianluca");
        List<Object> lhs7 = Arrays.asList("posta", "verso", "marco");

        //creazione dei nodi
        rete.buildRete(lhs1);
        rete.buildRete(lhs2);
        rete.buildRete(lhs3);
        rete.buildRete(lhs4);
        rete.buildRete(lhs5);
        rete.buildRete(lhs6);
        rete.buildRete(lhs7);
        
        //test match
        System.out.println("-----OUT-----");
        long start = System.nanoTime();
        System.out.print("S1: "); System.out.println(); rete.findMatch(Arrays.asList("email", "a","marco"), "sample1", true); System.out.println();
        long finish = System.nanoTime();
        System.out.print("S2: "); System.out.println(); rete.findMatch(Arrays.asList("email", "verso","marco"), "sample2", true); System.out.println();
        System.out.print("S3: "); System.out.println(); rete.findMatch(Arrays.asList("email", null, "marco"), "sample3", false); System.out.println();
        System.out.print("S4: "); System.out.println(); rete.findMatch(Arrays.asList(null, null, null), "sample4", true); System.out.println();
        System.out.print("S5: "); System.out.println(); rete.findMatch(Arrays.asList("email", "a","gregorio"), "sample5", false); System.out.println();
        System.out.print("S6: "); System.out.println(); rete.findMatch(Arrays.asList("posta", "verso","gianluca"), "sample6", true); System.out.println();
        System.out.print("S7: "); System.out.println(); rete.findMatch(Arrays.asList("tupla", "non dentro","rete"), "sample7", false); System.out.println();
        System.out.print("S8: "); System.out.println(); rete.findMatch(Arrays.asList(null, null, "marco"), "sample8", true); System.out.println();
        System.out.print("S9: "); System.out.println(); rete.findMatch(Arrays.asList("email", null, null), "sample9", false); System.out.println();
        System.out.print("S10: "); System.out.println(); rete.findMatch(Arrays.asList(null, "a", "marco"), "sample10", false); System.out.println();
        System.out.print("S11: "); System.out.println(); rete.findMatch(Arrays.asList(null, "a", null), "sample11", true); System.out.println();
        System.out.print("S12: "); System.out.println(); rete.findMatch(Arrays.asList("posta", "a", null), "sample12", true); System.out.println();
        System.out.print("S13: "); System.out.println(); rete.findMatch(Arrays.asList("posta", "verso", null), "sample13", true); System.out.println();

        //tempo
        System.out.println("-----TIME-----");
        System.out.println("time elapsed (first match only): " + (finish - start)/(Math.pow(10,6)) + "ms");
        System.out.println();

        //controllo della rete
        System.out.println("-----RETE-----");
        rete.printRete();
        System.out.println();

        System.out.println("-----SAMPLES & TOKENS-----");
        rete.printSamplesAndTokens();
        System.out.println();
    }
}