package ast;

import java.util.HashMap;

public abstract class ASTNode {

    abstract boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType);

    final Location loc;

    ASTNode(Location loc) {
        this.loc = loc;
    }
}
