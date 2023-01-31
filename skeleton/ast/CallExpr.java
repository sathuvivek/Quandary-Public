package ast;

public class CallExpr extends Expr {

    final String funcName;
    final ExprList args;

    public CallExpr(String funcName, ExprList args, Location loc) {
        super(loc);
        this.funcName = funcName;
        this.args = args;
    }

    public String getFuncName() {
        return funcName;
    }

    public ExprList getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "calling : " + funcName + " with args : " + args.toString();
    }
}
