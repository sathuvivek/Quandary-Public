package ast;

public class FormalDeclList extends ASTNode {

    final String first;
    final FormalDeclList rest;

    public FormalDeclList(String first, FormalDeclList rest, Location loc) {
        super(loc);
        this.first = first;
        this.rest = rest;
    }

    public String getFirst() {
        return first;
    }

    public FormalDeclList getRest() {
        return rest;
    }


    @Override
    public String toString() {
        return first + " \n " + rest.toString() ;
    }
}
