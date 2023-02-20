package ast;

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
        return varDecl.toString() + " ( " + params.toString() + " ) " + " { \n" + body.toString() + "\n}";
    }
}
