package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

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
        return "calling : " + funcName + " with args : " + (args == null ? "" : args.toString());
    }

    @Override
    public void check(Context c) {
        FuncDef callee = null;
        if(c.funcMap.containsKey(funcName)) {
            callee = c.funcMap.get(funcName);
        } else {
            callee = getBuiltinFunc(funcName);
        }
        if(callee == null) {
            Interpreter.fatalError("undef function", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        if(args != null) {
            args.check(c);
        }
        FormalDeclList currParams = callee.getParams();
        ExprList currArgs = args;
        while(currParams != null && currArgs != null) {
            Context.checkTypes(currArgs.getFirst().getStaticType(c), currParams.getVarDecl().getType());
            currParams = currParams.getRest();
            currArgs = currArgs.getRest();
        }
        if(currParams != null || currArgs != null) {
            Interpreter.fatalError("Parameters not right in calling", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }

        if(callee.getVarDecl().getIsMutable() && !c.containingFuncDef.getVarDecl().getIsMutable()) {
            Interpreter.fatalError("Immutable variable write", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }

    }

    static FuncDef getBuiltinFunc(String funcName) {
        FuncDef callee = null;
        if(funcName.equals("isNil") || funcName.equals("isAtom")) {
            callee = new FuncDef(new VarDecl(Type.INT, false, null, null),
                    new FormalDeclList(new VarDecl(Type.Q, false, "NAME", null ), null, null),
                    null,
                    null);
        } else if(funcName.equals("left") || funcName.equals("right")) {
            callee = new FuncDef(new VarDecl(Type.Q, false, null, null),
                    new FormalDeclList(new VarDecl(Type.REF, false, "NAME", null ), null, null),
                    null,
                    null);
        } else if(funcName.equals("randomInt")) {
            callee = new FuncDef(new VarDecl(Type.INT, false, null, null),
                    new FormalDeclList(new VarDecl(Type.INT, false, "NAME", null ), null, null),
                    null,
                    null);
        } else if(funcName.equals("setLeft") || funcName.equals("setRight")) {
            callee = new FuncDef(new VarDecl(Type.INT, true, null, null),
                    new FormalDeclList(new VarDecl(Type.REF, false, "NAME", null ),
                            new FormalDeclList(new VarDecl(Type.Q, false, "y", null), null, null),
                            null),
                    null,
                    null);
        }
        return callee;
    }

    @Override
    Type getStaticType(Context c) {
        FuncDef callee = c.funcMap.get(funcName);
        if(callee == null) {
            callee = getBuiltinFunc(funcName);
        }
        return callee.getVarDecl().getType();
    }
}
