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
    public void check(Context c) {
        expr.check(c);
        Context.checkTypes(expr.getStaticType(c), varDecl.getType());
        if(c.varMap.containsKey(varDecl.getName())) {
            Interpreter.fatalError("Duplicate variable", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        c.varMap.put(varDecl.getName(), varDecl);
    }
}
