package ast;

import interpreter.Interpreter;
import netscape.javascript.JSObject;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Program extends ASTNode {

    final FuncDefList funcDefList;

    public Program(FuncDefList funcDefList, Location loc) {
        super(loc);
        this.funcDefList = funcDefList;
    }

    public FuncDefList getFuncDefList() {
        return funcDefList;
    }

    public void println(PrintStream ps) {
        ps.println(funcDefList);
    }

    @Override
    public boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {
        environmentFunctions = getFunctionMap();
        //System.out.println("Precalculated functions : " + environmentFunctions.keySet().toString());
        if(!environmentFunctions.containsKey("main") || environmentFunctions.get("main").getParams().getVarDecl().getType() != Type.INT) {
            Interpreter.fatalError("Code has no valid main function at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
        }
        return funcDefList.check(environmentFunctions, environmentVariable, isMutable, returnType);
    }

    private HashMap<String, FuncDef> getFunctionMap() {
        HashMap<String, FuncDef> environmentFunctions = new HashMap<>();
        FuncDefList dup = funcDefList;
        ArrayList<String> list = new ArrayList<>();
        list.add("left");
        list.add("right");
        list.add("isAtom");
        list.add("isNil");
        list.add("setLeft");
        list.add("setRight");
        list.add("acq");
        list.add("rel");

        while(dup != null) {
            String funcName = dup.getFirst().getVarDecl().getName();
            if(list.contains(funcName)) {
                Interpreter.fatalError(  "Function name cant be same as in build function at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
            if(environmentFunctions.containsKey(funcName)) {
                Interpreter.fatalError(  "Duplicate function declaration : " + funcName + " at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
            environmentFunctions.put(funcName, dup.getFirst());
            dup = dup.getRest();
        }
        return environmentFunctions;
    }
}
