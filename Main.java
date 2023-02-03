import java.lang.Math;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Rete rete = new Rete();
        
        //fatti
        List<Object> facts1 = Arrays.asList("A", "B");
        List<Object> facts2 = Arrays.asList("A", "B", "D");
        List<Object> facts3 = Arrays.asList("A",0,"C","K","E",5.9,"P","H","999");
        List<Object> facts4 = Arrays.asList("H", "Z", "J", "A");
        List<Object> facts5 = Arrays.asList(1, 2, 3);
        List<Object> facts6 = Arrays.asList("Z", "K", "Y","U");
        List<Object> RHS1 = Arrays.asList("action_1");
        List<Object> RHS2 = Arrays.asList("action_2","action_3");

        //aggiunge una regola (LHS, RHS)
        rete.buildNetwork(facts1, RHS1);
        rete.buildNetwork(facts2, RHS1);
        rete.buildNetwork(facts3, RHS2);
        rete.buildNetwork(facts4, RHS2);
        rete.buildNetwork(facts5, RHS1);
        rete.buildNetwork(facts6, RHS1);
        
        //test
        long start = System.nanoTime();
        rete.match(facts1);
        long finish = System.nanoTime();
        System.out.println("EXPECTED: action_1");

        rete.match(facts2);
        System.out.println("EXPECTED: action_1");

        rete.match(facts3);
        System.out.println("EXPECTED: action_2, action_3");

        rete.match(facts4);
        System.out.println("EXPECTED: action_2, action_3");

        rete.match(facts5);
        System.out.println("EXPECTED: action_1");

        rete.match(facts6);
        System.out.println("EXPECTED: action_1");



        System.out.println("-----TIME-----");
        System.out.println("time elapsed (first match only): " + (finish - start)/(Math.pow(10,6)) + "ms");

        System.out.println("-----NETWORK-----");
        rete.printNetwork();
    }
}