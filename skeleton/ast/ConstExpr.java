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
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
       // System.out.println("Value : " + value + " | class : " + value.getClass().getName());
        if(value == null)
            Interpreter.fatalError( "null is not defined"+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        return false;
    }

    @Override
    Type getStaticType() {
        return Type.INT;
    }
}
