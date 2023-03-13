package ast;

import java.util.HashMap;

public class NilExpr extends Expr {
    @Override
    public void check(Context c) {
    }

    public NilExpr(Location loc) {
        super(loc);
    }

    @Override
    Type getStaticType(Context c) {
        return Type.REF;
    }
}
