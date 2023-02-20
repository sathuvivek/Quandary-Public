package ast;

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
}
