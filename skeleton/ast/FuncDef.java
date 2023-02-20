package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

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

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        varDecl.check(environmentFunctions, (HashMap<String, VarDecl>) environmentVariable.clone(), isMutable, returnType);
        //System.out.println("Actual Variables : " + environmentVariable.keySet().toString());
        HashMap<String, VarDecl> clonedVariables = (HashMap<String, VarDecl>) environmentVariable.clone();
        //System.out.println("ClonedVariables before Param check : " + clonedVariables.keySet().toString());
        if(params != null)
            params.check(environmentFunctions,clonedVariables, isMutable, returnType);
        //environmentFunctions.put(varDecl.getName(), this);
        //System.out.println("ClonedVariables sending to within Function : " + clonedVariables.keySet().toString());
        return body.check(environmentFunctions, clonedVariables, varDecl.getIsMutable(), varDecl.getType());
    }
}
