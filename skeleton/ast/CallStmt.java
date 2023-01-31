package ast;

public class CallStmt extends Stmt {
    final CallExpr callExpr;

    public CallStmt(String funcName, ExprList args, Location loc) {
        super(loc);
        this.callExpr = new CallExpr(funcName, args, loc);
    }

    public CallExpr getCallExpr() {
        return callExpr;
    }

    @Override
    public String toString() {
        return callExpr.toString();
    }
}
