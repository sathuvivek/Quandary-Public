package ast;

import java.util.HashMap;

public class NilExpr extends Expr {

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        return false;
    }

    public NilExpr(Location loc) {
        super(loc);
    }

    @Override
    Type getStaticType() {
        return Type.REF;
    }
}
