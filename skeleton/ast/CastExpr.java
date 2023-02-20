package ast;

public class CastExpr extends Expr {

    final Expr expr;
    final Type type;
    public CastExpr(Type type, Expr expr1, Location loc) {
        super(loc);
        this.expr = expr1;
        this.type = type;
    }

    public Expr getExpr() {
        return expr;
    }

    public Type getType() { return type;};

    @Override
    public String toString() {
        String s = null;
        return type.toString() + " " + expr.toString() ;
    }
}
