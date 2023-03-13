package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class VarDecl extends ASTNode {

    final String name;

    final boolean isMutable;

    final Type type;

    public VarDecl( Type type, boolean isMutable, String name, Location loc) {
        super(loc);
        this.name = name;
        this.isMutable = isMutable;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean getIsMutable() {
        return isMutable;
    }

    public Type getType() { return type;};

    @Override
    public String toString() {
        return (isMutable? "Mutable" : "immutable") + " " + type.toString() + "  " + name.toString() ;
    }

    @Override
    public void check(Context c) {

    }

}
