import java.util.List;

public class BetaNode extends Node {
    private Node parent1;
    private Node parent2;

    public BetaNode(int positionInsideTuple, List<Object> ParentsValue, Node parent1, Node parent2) {
        super(ParentsValue);
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public Node getParent1() {
        return this.parent1;
    }

    public Node getParent2() {
        return this.parent2;
    }
}
