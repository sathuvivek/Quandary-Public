package ast;

import interpreter.Interpreter;

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
    public void check(Context c) {
        expr.check(c);
        if(expr.getStaticType(c) != Type.INT) {
            Interpreter.fatalError("Unary Minus wrong expression type", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }

    }
    @Override
    Type getStaticType(Context c) {
        return Type.INT;
    }
}
