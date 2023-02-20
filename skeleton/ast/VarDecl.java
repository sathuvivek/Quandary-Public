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
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariables, boolean isMutable, Type returnType) {
        if(environmentVariables.containsKey(name)) {
            Interpreter.fatalError(name + " already defined at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            return false;
        }
        environmentVariables.put(name, this);
        return false;
    }
}
