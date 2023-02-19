import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Object> value;           //contenuto del nodo (LHS)
    private List<Node> children;                //lista di tutti i nodi figli di questo nodo
    private List<Object> memory;                //memoria del nodo (conterra' i token)
    private final int position;                 //posizione nella tupla del value passato in ingresso

    //costruttore
    public Node(int positionInsideTuple, List<Object> value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.memory = new ArrayList<>();
        this.position = positionInsideTuple;
    }

    public void deleteMemory(Object token) {
        if (this.memory.contains(token)) {
            this.memory.remove(token);
        }
    }

    //selettori
    public List<Object> getValue() {
        return this.value;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public List<Object> getMemory() {
        return this.memory;
    }

    public int getPositionInsideTuple() {
        return this.position;
    }
}