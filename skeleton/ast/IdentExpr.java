package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class IdentExpr extends Expr {

    final String varName;

    Type staticType;

    public IdentExpr(String varName, Location loc) {
        super(loc);
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public String toString() {
        return varName.toString();
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        if(!environmentVariable.containsKey(varName)) {
            Interpreter.fatalError(varName + " variable not defined at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        staticType = environmentVariable.get(varName).getType();
        return false;
    }

    @Override
    Type getStaticType() {
        return staticType;
    }
}
