package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class FormalDeclList extends ASTNode {

    final VarDecl varDecl;
    final FormalDeclList rest;

    public FormalDeclList(VarDecl varDecl, FormalDeclList rest, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.rest = rest;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public FormalDeclList getRest() {
        return rest;
    }


    @Override
    public String toString() {
        return varDecl.toString() + " \n " + ((rest == null) ? "" : rest.toString()) ;
    }

    @Override
    public void check(Context c) {
        if(c.varMap.containsKey(varDecl.getName())) {
            Interpreter.fatalError("Variable already defined" , Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        c.varMap.put(varDecl.getName(),varDecl);
       // varDecl.check(c);
        if(rest != null)
            rest.check(c);
    }
}
