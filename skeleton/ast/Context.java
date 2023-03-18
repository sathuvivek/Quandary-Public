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

    static public void checkTypes(Type src, Type dest, Location loc) {
        if(IsExtended.getValue()) {
            if((dest == Type.REF && (src != Type.NONEMPTYLIST && src != Type.LIST && src != Type.NONNILREF && src != Type.REF)) ||
                    (dest == Type.LIST && src != Type.LIST && src != Type.NONEMPTYLIST ) || // && src != Type.REF
                    (dest == Type.NONNILREF && src != Type.NONNILREF && src != Type.NONEMPTYLIST ) || // && src != Type.REF
                    (dest == Type.NONEMPTYLIST && src != Type.NONEMPTYLIST ) || // && src != Type.REF
                    (dest == Type.INT && src != Type.INT)) {
                Interpreter.fatalError("Incompatible types extended src : " + src.toString() + " | dest : " + dest.toString() + " at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
//            if( (src == Type.LIST && (dest != Type.LIST && dest != Type.Q && dest != Type.REF)) || (src == Type.NONNILREF && (dest != Type.NONNILREF && dest != Type.REF && dest != Type.Q)) || (src == Type.INT && dest != Type.INT && dest != Type.Q) ||
//                    (src == Type.REF && dest != Type.REF && dest != Type.Q)) {
////                System.out.println("(src == Type.LIST && (dest != Type.LIST && dest != Type.Q && dest != Type.REF)) : " + (src == Type.LIST && (dest != Type.LIST && dest != Type.Q && dest != Type.REF)));
////                System.out.println("(src == Type.NONNILREF && (dest != Type.NONNILREF && dest != Type.REF && dest != Type.Q)) : " + (src == Type.NONNILREF && (dest != Type.NONNILREF && dest != Type.REF && dest != Type.Q)));
//                Interpreter.fatalError("Incompatible types extended src : " + src.toString() + " | dest : " + dest.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
//            }
        } else {
            if(dest == Type.INT && src != Type.INT ||
                    dest == Type.REF && src != Type.REF) {
                Interpreter.fatalError("Incompatible types : src : " + src.toString() + " | dest : " + dest.toString() + " at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
        }

    }

}
