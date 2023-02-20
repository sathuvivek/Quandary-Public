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

    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        if(!environmentVariable.containsKey(varName)) {
            Interpreter.fatalError(varName + " not defined at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            return false;
        }
        VarDecl decl = environmentVariable.get(varName);
        if(!decl.getIsMutable()) {
            Interpreter.fatalError(varName + " is not mutable at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            return false;
        }
        boolean hasReturn = expr.check(environmentFunctions, environmentVariable, isMutable, returnType);
        //if( !(expr.getStaticType() == Type.Q  || expr.getStaticType() == decl.getType())) {
       // System.out.println("Left : " + decl.getType() + " | right : " + expr.getStaticType());
        boolean hasQLeft = (decl.getType() == Type.Q );
        boolean hasSameType = (expr.getStaticType() == decl.getType());
        boolean val = (hasQLeft || hasSameType);
        //System.out.println("qLeft  : " +hasQLeft + "  | hasSameTye : " + hasSameType + " | Output : " + (!val)) ;
        if( !val) {
                Interpreter.fatalError(  "Assignment Typecast error at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
                return false;
        }
        return hasReturn;
    }
}
