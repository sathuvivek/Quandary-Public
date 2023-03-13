package ast;

import interpreter.Interpreter;

import java.util.HashMap;

public class Context {
    final HashMap<String, VarDecl> varMap;
    final HashMap<String, FuncDef> funcMap;
    final FuncDef containingFuncDef;

    public static Context newContext() {
        return new Context(new HashMap<String, FuncDef>(), new HashMap<String, VarDecl>(), null);
    }

    public Context(HashMap<String, FuncDef> funcMap, HashMap<String, VarDecl> varMap, FuncDef containingFuncDef) {

        this.varMap = varMap;
        this.funcMap = funcMap;
        this.containingFuncDef = containingFuncDef;
    }
    Context duplicate(FuncDef containingFuncDef) {
        return new Context(funcMap,(HashMap<String, VarDecl>) this.varMap.clone(), containingFuncDef);
    }

    Context duplicate() {
        return new Context(funcMap,(HashMap<String, VarDecl>) this.varMap.clone(), this.containingFuncDef);
    }

    static public void checkTypes(Type src, Type dest) {
        if(dest == Type.INT && src != Type.INT ||
                dest == Type.REF && src != Type.REF) {
            Interpreter.fatalError("Incompatible types", Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }

    }

}
