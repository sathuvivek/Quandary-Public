package ast;

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
        return varDecl.toString() + " \n " + rest.toString() ;
    }
}
