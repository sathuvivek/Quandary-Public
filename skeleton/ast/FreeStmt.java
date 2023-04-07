package ast;

public class FreeStmt extends Stmt{
    final Expr expr;

    public FreeStmt(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "Freeing : " + expr.toString();
    }

    @Override
    public void check(Context c) {
        expr.check(c);
    }

}
