package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class DeclStmt extends Stmt {

    final Expr expr;
    final VarDecl varDecl;

    public DeclStmt(VarDecl varDecl, Expr expr, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.expr = expr;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }
    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return varDecl.toString() + " " + expr.toString();
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
       // System.out.println("Before EnvironmentVariables " + environmentVariable.keySet().toString());
        boolean hasReturn = expr.check(environmentFunctions, environmentVariable, isMutable, returnType);
       // System.out.println("After Expr EnvironmentVariables " + environmentVariable.keySet().toString());
        varDecl.check(environmentFunctions, environmentVariable, isMutable, returnType);
        ArrayList<Type> list = new ArrayList<>();

        list.add(Type.Q);
        list.add(expr.getStaticType());
        if(!list.contains(varDecl.getType())) {
            Interpreter.fatalError(  "Declaration Typecast error at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            return false;
        }
        return hasReturn;
    }
}
