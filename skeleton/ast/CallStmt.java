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
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        String funcName = callExpr.getFuncName();
        //System.out.println("callStmt  ");
        ArrayList<String> builtInList = new ArrayList<>();
        builtInList.add("left");
        builtInList.add("right");
        builtInList.add("isAtom");
        builtInList.add("isNil");
        builtInList.add("setLeft");
        builtInList.add("setRight");
        builtInList.add("acq");
        builtInList.add("rel");
        builtInList.add("randomInt");

        if(!builtInList.contains(funcName)) {
            if (!environmentFunctions.containsKey(funcName)) {
                Interpreter.fatalError(funcName + " function is not defined at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
            boolean isCalleeMutable = environmentFunctions.get(funcName).getVarDecl().getIsMutable();
            if (!isCalleeMutable) {
                Interpreter.fatalError("Cannot call immutable function at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
        } else {
            if (!isMutable) {
                ArrayList<String> mutableList = new ArrayList<>();
                mutableList.add("acq");
                mutableList.add("rel");
                mutableList.add("setLeft");
                mutableList.add("setRight");
                if(mutableList.contains(funcName)) {
                    Interpreter.fatalError(" Cannot call mutable from immutable "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
                }
            }
        }

        return callExpr.check(environmentFunctions, environmentVariable, isMutable, returnType);
    }
}
