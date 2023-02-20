package ast;

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
}
