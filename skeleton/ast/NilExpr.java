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
    boolean isList(Context c) {
        if(IsExtended.getValue())
            return true;
        return false;
    }

    @Override
    Type getStaticType(Context c) {
        if(IsExtended.getValue())
            return Type.LIST;
        return Type.REF;
    }
}
