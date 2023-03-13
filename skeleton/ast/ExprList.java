package ast;

import java.util.HashMap;

public class ExprList extends Expr {

    final Expr first;
    final ExprList rest;

    public ExprList(Expr first, ExprList rest, Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public Expr getFirst() {
        return first;
    }

    public ExprList getRest() {
        return rest;
    }


    @Override
    public String toString() {
        return first.toString() + " \n " + (rest == null? "End of expressions " : rest.toString()) ;
    }

    @Override
    public void check(Context c) {
        first.check(c);
        if(rest != null)
            rest.check(c);
    }

    @Override
    Type getStaticType(Context c) {
        return first.getStaticType(c);
    }
}
