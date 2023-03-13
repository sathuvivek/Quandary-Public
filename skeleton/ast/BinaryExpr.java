package ast;

import interpreter.Interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BinaryExpr extends Expr {

    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int TIMES = 3;
    public static final int DOT = 4;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public BinaryExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public int getOperator() {
        return operator;
    }
    
    public Expr getRightExpr() {
        return expr2;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case PLUS:  s = "+"; break;
            case MINUS: s = "-"; break;
            case TIMES: s = "*"; break;
            case DOT: s = "."; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }

    @Override
    public void check(Context c) {
        expr1.check(c);
        expr2.check(c);
        if(operator != DOT) {
            if(expr1.getStaticType(c) != Type.INT ||
                expr2.getStaticType(c) != Type.INT) {
                Interpreter.fatalError("binary expression types not int", Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
        }
    }

    @Override
    Type getStaticType(Context c) {
        if(operator != DOT) {
           return Type.INT;
        }
        return Type.REF;
    }
}
