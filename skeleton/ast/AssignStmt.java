package ast;

import interpreter.Interpreter;

import javax.security.auth.login.CredentialNotFoundException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class AssignStmt extends Stmt {

    final Expr expr;
    final String varName;

    public AssignStmt(String varName, Expr expr, Location loc) {
        super(loc);
        this.varName = varName;
        this.expr = expr;
    }

    public String getVarName() {
        return varName;
    }
    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return varName + " " + expr.toString();
    }

    @Override
    public void check(Context c) {
        expr.check(c);
        if(!c.varMap.containsKey(varName)) {
            Interpreter.fatalError("var udnefined", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        VarDecl vd = c.varMap.get(varName);
        Context.checkTypes(expr.getStaticType(c), vd.getType());
        if(!vd.getIsMutable()) {
            Interpreter.fatalError("immutable variable write", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
    }
}
