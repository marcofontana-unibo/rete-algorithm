import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String type;            //"alpha","beta","gamma","terminal","root" a seconda del tipo di nodo
    private List<Object> value;           //contenuto del nodo, se "alpha","beta","gamma": conterrà LHS; se "terminal": RHS
    private List<Node> children;    //lista di tutti i nodi figli di questo nodo
    private List<Object> memory;

    //costruttore
    public Node(String type, List<Object> value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
        this.memory = new ArrayList<>();
    }

    //selettori
    public String getType() {
        return this.type;
    }

    public List<Object> getValue() {
        return this.value;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public List<Object> getMemory() {
        return this.memory;
    }
}