package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class CompCond extends Cond {

    public static final int GE = 1;
    public static final int LE = 2;
    public static final int GT = 3;
    public static final int LT = 4;
    public static final int EQ = 5;
    public static final int NE = 6;


    final Expr expr1;
    final int operator;
    final Expr expr2;

    public CompCond(Expr expr1, int operator, Expr expr2, Location loc) {
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
            case GE: s = ">="; break;
            case LE: s = "<="; break;
            case GT: s = ">"; break;
            case LT: s = "<"; break;
            case EQ: s = "=="; break;
            case NE: s = "!="; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
//        System.out.println("Expr 1 : " + expr1.toString());
//        System.out.println("Expr 1 type: " + expr1.getClass().getName());
//        System.out.println("Expr 2 : " + expr2.toString());
//        System.out.println("Expr 12 type: " + expr2.getClass().getName());
        expr1.check(environmentFunctions, environmentVariable, isMutable, returnType);
        //System.out.println("Class Name : " + expr2.getClass().getName());
        expr2.check(environmentFunctions, environmentVariable, isMutable, returnType);
       // System.out.println("checked expr2 : " + expr2.getStaticType());
        if(expr1.getStaticType() != expr2.getStaticType()) {
            Interpreter.fatalError(" Cannot compare different types of variables at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        return false;
    }
}
