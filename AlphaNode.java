import java.util.ArrayList;
import java.util.List;

public class AlphaNode {
    private final List<Object> value;           //contenuto del nodo (LHS)
    private List<AlphaNode> children;           //lista di tutti i nodi figli di questo nodo
    private List<Object> memory;                //memoria del nodo (conterra' i token)

    //costruttore
    public AlphaNode(List<Object> value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.memory = new ArrayList<>();
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

    public List<AlphaNode> getChildren() {
        return this.children;
    }

    public List<Object> getMemory() {
        return this.memory;
    }
}