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
    public void check(Context c) {
        if(!c.varMap.containsKey(varName)) {
            Interpreter.fatalError("Undeclared var", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
    }

    @Override
    Type getStaticType(Context c) {
        return c.varMap.get(varName).getType();
    }
}
