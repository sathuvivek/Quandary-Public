package ast;

public class StmtList extends Stmt {

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
}
