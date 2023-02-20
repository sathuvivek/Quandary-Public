package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class LogicalCond extends Cond {

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;


    final Cond cond1;
    final int operator;
    final Cond cond2;

    public LogicalCond(Cond cond1, int operator, Cond cond2, Location loc) {
        super(loc);
        this.cond1 = cond1;
        this.operator = operator;
        this.cond2 = cond2;
    }

    public Cond getLeftCond() {
        return cond1;
    }

    public int getOperator() {
        return operator;
    }
    
    public Cond getRightCond() {
        return cond2;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case AND: s = "&&"; break;
            case OR: s = "||"; break;
            case NOT: s = "!"; break;
        }

        if(cond2 == null)
            return  s + cond1;
        else
            return "(" + cond1 + " " + s + " " + cond2 + ")";
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        cond1.check(environmentFunctions, environmentVariable, isMutable, returnType);
        cond2.check(environmentFunctions, environmentVariable, isMutable, returnType);
        return false;
    }
}
