import java.util.List;

public class Test {

    //restituisce true solo se le due liste sono uguali (conta l'ordine!)
    public boolean checkOutput(List<Object> reteOutput, List<Object> expectedOutput) {
        return reteOutput.toString().equals(expectedOutput.toString());
    }
    
}