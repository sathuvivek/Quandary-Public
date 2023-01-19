package ast;

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
}
