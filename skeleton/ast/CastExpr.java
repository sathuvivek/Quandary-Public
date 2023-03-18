package ast;

import interpreter.Interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public void check(Context c) {
        expr.check(c);
        if(type == Type.INT && expr.getStaticType(c) == Type.REF ||
                type == Type.REF && expr.getStaticType(c) == Type.INT) {
            Interpreter.fatalError("Cast Expr error", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        if(IsExtended.getValue()) {
            if((type == Type.LIST && expr.getStaticType(c) == Type.NONNILREF) ||
                    (type == Type.NONNILREF && expr.getStaticType(c) == Type.LIST)) {
                Interpreter.fatalError("Cast Expr error", Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
        }
    }

    @Override
    boolean isList(Context c) {
        if(IsExtended.getValue() && (type == Type.LIST || type == Type.NONEMPTYLIST))
            return true;
        return false;
    }

    @Override
    Type getStaticType(Context c) {
        return type;
    }
}
