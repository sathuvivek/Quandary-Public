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




    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        boolean hasReturn = first.check(environmentFunctions, environmentVariable, isMutable, returnType);
        if(hasReturn)
            return hasReturn;
        if(rest != null)
            return rest.check(environmentFunctions, environmentVariable, isMutable, returnType);
        return hasReturn;
    }
}
