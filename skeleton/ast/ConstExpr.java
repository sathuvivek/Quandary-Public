package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class ConstExpr extends Expr {

    final Object value;

    public ConstExpr(long value, Location loc) {
        super(loc);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public void check(Context c) {

    }

    @Override
    Type getStaticType(Context c) {
        return Type.INT;
    }
}
