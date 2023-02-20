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
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {
        first.check(environmentFunctions, environmentVariable, isMutable,returnType);
        if(rest != null)
            return rest.check(environmentFunctions, environmentVariable, isMutable, returnType);
        return false;
    }

    @Override
    Type getStaticType() {
        return first.getStaticType();
    }
}
