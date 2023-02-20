package ast;

import java.util.HashMap;

public class IfStmt extends Stmt {

    final Cond cond;
    final Stmt thenStmt;
    final Stmt elseStmt;

    public IfStmt(Cond cond, Stmt thenStmt, Stmt elseStmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public Cond getCond() {
        return cond;
    }
    public Stmt getThenStmt() {
        return thenStmt;
    }

    public Stmt getElseStmt() {
        return elseStmt;
    }


    @Override
    public String toString() {
        return "If condition : " + cond.toString() + " \n \t" + thenStmt.toString() + " \n \t" + (elseStmt == null ? "No else" :  elseStmt.toString());
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        cond.check(environmentFunctions, environmentVariable, isMutable, returnType);
        boolean thenReturn = false;
        boolean elseReturn = false;
        thenReturn = thenStmt.check(environmentFunctions, (HashMap<String, VarDecl>) environmentVariable.clone(), isMutable, returnType);
       // System.out.println("checked then statement with output : " + thenReturn + "for stmt : " + this.toString()) ;
        if(elseStmt != null) {
            elseReturn = elseStmt.check(environmentFunctions, (HashMap<String, VarDecl>) environmentVariable.clone(), isMutable, returnType);
           // System.out.println("checked else statement with output : " + elseReturn);
            return thenReturn && elseReturn;
        }
        return false;
    }
}
