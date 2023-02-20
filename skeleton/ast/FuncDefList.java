package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class FuncDefList extends ASTNode {

    final FuncDef first;
    final FuncDefList rest;


    public FuncDefList(FuncDef first, FuncDefList rest , Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public FuncDef getFirst() {
        return first;
    }

    public FuncDefList getRest() {return rest;}

    public FuncDef lookupFuncDef(String name) {
        if(first.getVarDecl().getName().equals(name))
            return first;
        return rest.lookupFuncDef(name);
    }

    @Override
    public String toString() {
        return first.toString() + "\n" + (rest == null? "Empty rest" :  rest.toString());
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        var hasReturn = first.check(environmentFunctions, (HashMap<String, VarDecl>) environmentVariable.clone(), first.varDecl.getIsMutable(), returnType);
        if(!hasReturn) {
            Interpreter.fatalError(first.getVarDecl().getName() + " has no return at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        if(rest != null)
            rest.check(environmentFunctions, environmentVariable, isMutable,returnType);
        return false;
    }


}
