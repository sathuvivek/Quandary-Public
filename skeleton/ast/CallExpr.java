package ast;

import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class CallExpr extends Expr {

    final String funcName;
    final ExprList args;

    Type staticType;

    public CallExpr(String funcName, ExprList args, Location loc) {
        super(loc);
        this.funcName = funcName;
        this.args = args;
    }

    public String getFuncName() {
        return funcName;
    }

    public ExprList getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "calling : " + funcName + " with args : " + (args == null ? "" : args.toString());
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable, boolean isMutable, Type returnType) {

        ArrayList<String> builtInList = new ArrayList<>();
        builtInList.add("left");
        builtInList.add("right");
        builtInList.add("isAtom");
        builtInList.add("isNil");
        builtInList.add("setLeft");
        builtInList.add("setRight");
        builtInList.add("acq");
        builtInList.add("rel");
        builtInList.add("randomInt");

        if (builtInList.contains(funcName)) {
            if(funcName.equals("left") || funcName.equals("right"))
                staticType = Type.Q;
            else
                staticType = Type.INT;
            if (!isMutable) {
                ArrayList<String> mutableList = new ArrayList<>();
                mutableList.add("acq");
                mutableList.add("rel");
                mutableList.add("setLeft");
                mutableList.add("setRight");
                if(mutableList.contains(funcName)) {
                    Interpreter.fatalError(" Cannot call mutable from immutable "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
                }
            }
            if(args != null)
                args.check(environmentFunctions, environmentVariable, isMutable, returnType);
            //System.out.println("Checked Args : " + funcName);
            if (funcName.equals("left") || funcName.equals("right")) {
               // System.out.println("Left Function : " + args.getFirst().getStaticType());
                if (args.getFirst().getStaticType() != Type.REF) {
                    Interpreter.fatalError(" Incorrect Argument Type for function "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
                }
            } else if (funcName.equals("isAtom") || funcName.equals("isNil")) {
//                if (args.getFirst().getStaticType() != Type.Q) {
//                    Interpreter.fatalError(" Incorrect Argument Type for function "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
//                }
            } else if(funcName.equals("randomInt")) {
                if (args.getFirst().getStaticType() != Type.INT) {
                    Interpreter.fatalError(" Incorrect Argument Type for function "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
                }
            }
        } else {
            if (!environmentFunctions.containsKey(funcName)) {
                Interpreter.fatalError(funcName + " function is not defined at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }
            int expectedCount = 0;
            FuncDef funcDef = environmentFunctions.get(funcName);

            staticType = funcDef.getVarDecl().getType();
            FormalDeclList declList = funcDef.getParams();

            while (declList != null) {
                expectedCount++;
                declList = declList.getRest();
            }

            int actualCount = 0;
            ExprList list = args;
            while (list != null) {
                actualCount++;
                list = list.getRest();
            }
          //  System.out.println("Call expression called ");
            if (expectedCount != actualCount) {
                Interpreter.fatalError("Illegal function call, wrong number of parameters at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            }

            if(args != null)
                args.check(environmentFunctions, environmentVariable, isMutable, returnType);
           // System.out.println("Parameters checked for call expressed ");

            if (!isMutable) {
                boolean isCalleeMutable = funcDef.getVarDecl().getIsMutable();
                String calleeName = funcDef.getVarDecl().getName();
                if (isCalleeMutable || calleeName.equals("setLeft") || calleeName.equals("setRight")) {
                    Interpreter.fatalError("Immutable cannot call mutable functions at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
                }
            }
            checkArgumentType(funcDef.getParams(), args);;

        }
        //System.out.println("callExpr  " + staticType);

        return false;
    }

    private void checkArgumentType(FormalDeclList declList, ExprList exprList) {
      //  System.out.println("Checking arguments for callExpr expected :  " + declList.toString() + " | actual : " + exprList);
        while(exprList != null && declList != null) {
            Type actualType = exprList.getFirst().getStaticType();
            Type expectedType = declList.getVarDecl().getType();
         //   System.out.println("Expected : " + expectedType + " | actual : " + actualType);
            boolean hasQLeft = (expectedType == Type.Q );
            boolean hasSameType = (actualType == expectedType);
            boolean val = (hasQLeft || hasSameType);
            //System.out.println("qLeft  : " +hasQLeft + "  | hasSameTye : " + hasSameType + " | Output : " + (!val)) ;
            if( !val) {
                Interpreter.fatalError(  "CallExpr Typecast error at " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);

            }

            exprList = exprList.getRest();
            declList = declList.getRest();
        }
        if(exprList != null || declList != null) {
            Interpreter.fatalError(  "CallExpr arguments dont match " + loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);

        }

    }

    @Override
    Type getStaticType() {
        return staticType;
    }
}
