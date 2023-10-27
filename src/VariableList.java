import java.util.HashMap;
import java.util.Map;

public class VariableList {
    private final Map<String, Variable> varList = new HashMap<>();

    public void addVar(String name, Variable var) {
        getVarList().put(name, var);
    }

    public boolean noVarCheck(String name) {
        return !getVarList().containsKey(name);
    }

    public void assign(String name) {
        Variable var = getVarList().get(name);
        var.assign();
    }

    public void assign(String name, float value) {
        Variable var = getVarList().get(name);
        var.assign(value);
    }

    public void assign(String name, String value) {
        Variable var = getVarList().get(name);
        Variable var2 = getVarList().get(value);
        var.assign(var2.getValue());
    }

    public void incr(String name) {
        Variable var = getVarList().get(name);
        var.incr();
    }

    public void decr(String name) {
        Variable var = getVarList().get(name);
        var.decr();
    }
    
    public void add(String name1, String name2, String nameOut) {
        Variable var1 = getVarList().get(name1);
        Variable var2 = getVarList().get(name2);
        Variable varOut = getVarList().get(nameOut);
        varOut.add(var1,var2);
    }

    public void subtract(String name1, String name2, String nameOut) {
        Variable var1 = getVarList().get(name1);
        Variable var2 = getVarList().get(name2);
        Variable varOut = getVarList().get(nameOut);
        varOut.subtract(var1,var2);
    }

    public void multiply(String name1, String name2, String nameOut) {
        Variable var1 = getVarList().get(name1);
        Variable var2 = getVarList().get(name2);
        Variable varOut = getVarList().get(nameOut);
        varOut.multiply(var1,var2);
    }

    public void divide(String name1, String name2, String nameOut) {
        Variable var1 = getVarList().get(name1);
        Variable var2 = getVarList().get(name2);
        Variable varOut = getVarList().get(nameOut);
        varOut.divide(var1,var2);
    }

    public float getVarValue(String name) {
        Variable var = getVarList().get(name);
        float varVal = var.getValue();
        int varInt = (int) varVal;
        if(varInt-varVal==0.0) return varInt;
        return varVal;
    }

    public void printAllVars() {
        for(String name : getVarList().keySet()) {
            if (!name.isEmpty()) {
                System.out.println(name + ": " + getVarValue(name));
            }
        }
    }

    public Map<String, Variable> getVarList() {
        return varList;
    }
}
