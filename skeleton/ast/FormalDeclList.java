package ast;

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
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        varDecl.check(environmentFunctions, environmentVariable, isMutable, returnType);
        if(rest != null)
            return rest.check(environmentFunctions, environmentVariable, isMutable, returnType);
        return false;
    }
}
