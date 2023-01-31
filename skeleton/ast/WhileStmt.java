package ast;

public class WhileStmt extends Stmt {

    final Cond cond;
    final Stmt body;


    public WhileStmt(Cond cond, Stmt body, Location loc) {
        super(loc);
        this.cond = cond;
        this.body = body;
    }

    public Cond getCond() {
        return cond;
    }
    public Stmt getBody() {
        return body;
    }


    @Override
    public String toString() {
        return "If condition : " + cond.toString() + " \n \t" + body.toString() + " \n \t";
    }
}
