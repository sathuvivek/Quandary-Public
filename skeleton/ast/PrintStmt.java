package ast;

import interpreter.Interpreter;

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
    public void check(Context c) {
        expr.check(c);
    }
}
