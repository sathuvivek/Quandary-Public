package ast;

public abstract class Expr extends ASTNode {

    Expr(Location loc) {
        super(loc);
    }

    abstract boolean isList(Context c);

    abstract Type getStaticType(Context c);
}
