package ast;

import java.util.HashMap;

public class UnaryMinusExpr extends Expr {

    final Expr expr;
    public UnaryMinusExpr(Expr expr1, Location loc) {
        super(loc);
        expr = expr1;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        String s = null;
        return expr.toString();
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        expr.check(environmentFunctions, environmentVariable, isMutable, returnType);
        return false;
    }

    @Override
    Type getStaticType() {
        return Type.INT;
    }
}
