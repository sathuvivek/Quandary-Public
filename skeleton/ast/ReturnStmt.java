package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class ReturnStmt extends Stmt {

    final Expr expr;

    public ReturnStmt(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return expr.toString();
    }




    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        //System.out.println("Return statement called with value : " + this.toString());
        expr.check(environmentFunctions, environmentVariable, isMutable, returnType);
       // System.out.println("Return Done: " + this.toString());
        ArrayList<Type> list = new ArrayList<>();
        list.add(Type.Q);
        list.add(expr.getStaticType());
        if(!list.contains(returnType )) {
            Interpreter.fatalError(  "Return type doesnt match " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        return true;
    }
}
