package ast;

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
}
