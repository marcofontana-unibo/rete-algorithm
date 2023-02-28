import java.util.ArrayList;
import java.util.List;

public class AlphaNode {
    private List<Object> value;                 //contenuto del nodo (LHS)
    private List<Object> memory;                //memoria del nodo (conterra' i sampleID)

    //costruttore
    public AlphaNode(List<Object> value) {
        this.value = value;
        this.memory = new ArrayList<>();
    }

    public void deleteMemory() {
        memory.clear();
    }

    //selettori
    public List<Object> getValue() {
        return this.value;
    }

    public List<Object> getMemory() {
        return this.memory;
    }
}