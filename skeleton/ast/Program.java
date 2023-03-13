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
    public void check(Context c) {
        //Add funcDefs to map
        FuncDefList  curList = funcDefList;
        while(curList != null) {
            FuncDef funcDef = curList.getFirst();
            if(c.funcMap.containsKey(funcDef.varDecl.getName()) ||
                CallExpr.getBuiltinFunc(funcDef.getVarDecl().getName()) != null) {
                Interpreter.fatalError("duplicate function : " + funcDef.getVarDecl().getName() , Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
            c.funcMap.put(funcDef.varDecl.getName(), funcDef);
            curList = curList.getRest();
        }
        //Check that main exists
        FuncDef funcDef = c.funcMap.get("main");
        if(!(funcDef != null &&
                funcDef.getParams().getVarDecl().type == Type.INT  &&
                funcDef.getParams().getRest() == null)) {
            Interpreter.fatalError("main", Interpreter.EXIT_STATIC_CHECKING_ERROR );
        }
        //Check all function definitions
        funcDefList.check(c);
    }

}
