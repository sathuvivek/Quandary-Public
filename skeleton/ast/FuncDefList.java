package ast;

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
}
