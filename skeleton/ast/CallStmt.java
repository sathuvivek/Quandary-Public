package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

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

    @Override
    public void check(Context c) {
        callExpr.check(c);
        FuncDef callee = c.funcMap.get(callExpr.getFuncName());
        if(callee == null) {
            callee = callExpr.getBuiltinFunc(callExpr.getFuncName());
        }
        if(!callee.getVarDecl().getIsMutable()) {
            Interpreter.fatalError("Function is not mutable caller : " + c.containingFuncDef.getVarDecl().getName() + " | callee : " + callExpr.getFuncName(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
    }
}
