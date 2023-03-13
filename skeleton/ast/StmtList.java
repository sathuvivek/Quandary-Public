package ast;

import java.util.HashMap;

public class StmtList extends ASTNode {

    final Stmt first;
    final StmtList rest;

    public StmtList(Stmt first, StmtList rest , Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public Stmt getFirst() {
        return first;
    }

    public StmtList getRest() {return rest;}

    @Override
    public String toString() {
        return first.toString() + "\n" + (rest == null? "Empty rest" :  rest.toString());
    }

    @Override
    public void check(Context c) {
        first.check(c);
        if(rest != null)
            rest.check(c);
    }
}
