public class Variable {
    private float value = 0;

    public void assign() {
        setValue(0);
    }
    public void assign(float value) {
        setValue(value);
    }

    public void incr() {
        setValue(getValue() + 1);
    }

    public void decr() {
        setValue(getValue() - 1);
    }
    
    public void add(Variable var1, Variable var2) {
        setValue(var1.getValue()+var2.getValue());
    }

    public void subtract(Variable var1, Variable var2) {
        setValue(var1.getValue()-var2.getValue());
    }

    public void multiply(Variable var1, Variable var2) {
        setValue(var1.getValue()*var2.getValue());
    }

    public void divide(Variable var1, Variable var2) {
        setValue(var1.getValue()/var2.getValue());
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
