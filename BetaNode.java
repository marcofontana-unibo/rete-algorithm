import java.util.List;

public class BetaNode extends AlphaNode {
    private AlphaNode parent1;
    private AlphaNode parent2;

    public BetaNode(List<Object> ParentsValue, AlphaNode parent1, AlphaNode parent2) {
        super(ParentsValue);
        this.parent1 = parent1;
        this.parent2 = parent2;
    }

    public AlphaNode getParent1() {
        return this.parent1;
    }

    public AlphaNode getParent2() {
        return this.parent2;
    }
}
