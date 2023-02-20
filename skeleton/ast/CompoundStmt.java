package ast;

import java.util.HashMap;

public class CompoundStmt extends Stmt {

    final StmtList stmtList;

    public CompoundStmt(StmtList stmtList, Location loc) {
        super(loc);
        this.stmtList = stmtList;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        return stmtList.toString();
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        return stmtList.check(environmentFunctions, (HashMap<String, VarDecl>) environmentVariable.clone(), isMutable, returnType);
    }
}
