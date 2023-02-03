public class Execute {

    //esecuzione delle azioni contenute nei nodi terminal
    public void execute(Object RHS) {
        if (RHS.equals("action_1")) {
            System.out.println("OUTPUT: action 1");
        } else if (RHS.equals("action_2")) {
            System.out.println("OUTPUT: action 2");
        } else if (RHS.equals("action_3")) {
            System.out.println("OUTPUT: action 3");
        } //else if ... ecc...ecc...
    }
}