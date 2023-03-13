package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class FuncDef extends ASTNode {

    final VarDecl varDecl;
    final FormalDeclList params;
    final StmtList body;
    public FuncDef(VarDecl varDecl, FormalDeclList params, StmtList body, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.params = params;
        this.body = body;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public FormalDeclList getParams() {
        return params;
    }

    public StmtList getBody() {
        return body;
    }

    @Override
    public String toString() {
        return varDecl.toString() + " ( " +  (params == null ? " ": params.toString()) + " ) " + " { \n" + body.toString() + "\n}";
    }
    @Override
    public void check(Context c) {
       // varDecl.check(c);
        if(params != null)
            params.check(c);
        StmtList currStmtList = body;
        while(currStmtList != null && currStmtList.rest != null) {
            currStmtList = currStmtList.rest;
        }
        if(currStmtList == null || !(currStmtList.getFirst() instanceof  ReturnStmt)) {
            Interpreter.fatalError("Last stmt was not return", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        body.check(c);
    }

}
