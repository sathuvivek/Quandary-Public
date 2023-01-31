package ast;

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
}
