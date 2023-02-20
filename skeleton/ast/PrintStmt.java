package ast;

import java.util.HashMap;

public class PrintStmt extends Stmt {

    final Expr expr;

    public PrintStmt(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return expr.toString();
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        return expr.check(environmentFunctions, environmentVariable, isMutable, returnType);
    }
}
