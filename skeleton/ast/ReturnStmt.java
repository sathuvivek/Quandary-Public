package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class ReturnStmt extends Stmt {

    final Expr expr;

    public ReturnStmt(Expr expr, Location loc) {
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
    public void check(Context c) {
        expr.check(c);
        Context.checkTypes(expr.getStaticType(c), c.containingFuncDef.getVarDecl().getType());
    }

}
