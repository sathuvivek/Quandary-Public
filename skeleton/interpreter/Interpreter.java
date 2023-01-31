package interpreter;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        //astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    QVal executeRoot(Program astRoot, long arg) {
        FuncDef mainFuncDef = astRoot.getFuncDefList().lookupFuncDef("main");
        HashMap<String, QVal> mainEnv = new HashMap<String, QVal>();
        mainEnv.put(mainFuncDef.getParams().getFirst(), new QInt(arg));
        return execute(mainFuncDef.getBody(), mainEnv);
    }

    QVal execute(Stmt stmt, HashMap<String,QVal> env) {
        if(stmt instanceof StmtList) {
            StmtList sl = (StmtList) stmt;
            QVal returnValue = execute(sl.getFirst(), env);
            if( returnValue != null)
                return returnValue;
            if(sl.getRest() != null)
                return execute(sl.getRest(), env);
            return null;
        } else if (stmt instanceof DeclStmt) {
            DeclStmt declStmt = (DeclStmt) stmt;
            env.put(declStmt.getVarName(), evaluate(declStmt.getExpr(), env));
            return null;
        } else if( stmt instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt)stmt;
            if(evaluate(ifStmt.getCond(), env)) {
                return execute(ifStmt.getThenStmt(), env);
            } else if (ifStmt.getElseStmt() != null) {
                return execute(ifStmt.getElseStmt(), env);
            }
            return null;
        } else if(stmt instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) stmt;
            while(evaluate(whileStmt.getCond(),env)) {
                QVal returnValue = execute(whileStmt.getBody(), env);
                if(returnValue !=  null)
                    return returnValue;
            }
            return null;
        } else if(stmt instanceof CallStmt) {
            CallStmt callStmt = (CallStmt)stmt;
            evaluate(callStmt.getCallExpr(), env);
            return null;
        } else if(stmt instanceof PrintStmt) {
           System.out.println(evaluate(((PrintStmt)stmt).getExpr(), env));
           return null;
        } else if(stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) stmt;
            return evaluate(returnStmt.getExpr(), env);
        } else {
            throw new RuntimeException("Unhandled Smt time");
        }
    }

    QVal evaluate(Expr expr, HashMap<String,QVal> env) {
        if (expr instanceof NilExpr) {
            return new QRef(null);
        } else if (expr instanceof ConstExpr) {
            return new QInt((long)((ConstExpr) expr).getValue());
        } else if(expr instanceof IdentExpr) {
//            System.out.println("Got into calling a expression ");
//            System.out.println("Expr :  " + ( (IdentExpr)expr).toString());
            return env.get(((IdentExpr)expr).getVarName());
        } else if (expr instanceof UnaryMinusExpr) {
            UnaryMinusExpr ume = (UnaryMinusExpr) expr;
            QInt v = (QInt)evaluate(ume.getExpr(), env);
            return new QInt(-v.value);
        } else if(expr instanceof CallExpr) {
           // System.out.println("Got into calling a expression ");
            CallExpr callExpr = (CallExpr)expr;
           // System.out.println("Expr :  " + callExpr.toString());
            if( callExpr.getFuncName().equals("randomInt")) {
                long num = ((QInt) evaluate(callExpr.getArgs().getFirst(), env)).value;
                long result = Math.abs(random.nextLong()) % num;
                return new QInt(result);
            } else if(callExpr.getFuncName().equals("left")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                return r.referent.left;
            } else if (callExpr.getFuncName().equals("right")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                return r.referent.right;
            } else if (callExpr.getFuncName().equals("setLeft")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                QVal val = evaluate(callExpr.getArgs().getRest().getFirst(), env);
                r.referent.left = val;
                return new QInt(1);
            } else if (callExpr.getFuncName().equals("setRight")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                QVal val = evaluate(callExpr.getArgs().getRest().getFirst(), env);
                r.referent.right = val;
                return new QInt(1);
            } else if (callExpr.getFuncName().equals("isAtom")) {
                QVal r =  evaluate(callExpr.getArgs().getFirst(), env);
                if(r instanceof QInt || (r instanceof QRef && ((QRef)r).referent == null)) {
                    return new QInt(1);
                }
                return new QInt(0);
            } else if (callExpr.getFuncName().equals("isNil")) {
                QVal r = evaluate(callExpr.getArgs().getFirst(), env);
                if(r instanceof QRef && ((QRef)r).referent == null) {
                    return new QInt(1);
                }
                return new QInt(0);
            }
           FuncDef callee =  astRoot.getFuncDefList().lookupFuncDef(callExpr.getFuncName());
           HashMap<String, QVal> calleeEnv = new HashMap<String, QVal>();
           FormalDeclList currentFormalDeclList = callee.getParams();
           ExprList currentExprList = callExpr.getArgs();
           while(currentFormalDeclList != null) {
               calleeEnv.put(currentFormalDeclList.getFirst(), evaluate(currentExprList.getFirst(),env));
               currentFormalDeclList = currentFormalDeclList.getRest();
               currentExprList = currentExprList.getRest();
           }
           return execute(callee.getBody(), calleeEnv);
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), env)).value + ((QInt)evaluate(binaryExpr.getRightExpr(), env)).value);
                case BinaryExpr.MINUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), env)).value - ((QInt)evaluate(binaryExpr.getRightExpr(), env)).value);
                case BinaryExpr.TIMES: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), env)).value * ((QInt)evaluate(binaryExpr.getRightExpr(), env)).value);
                case BinaryExpr.DOT: return new QRef(new QObj(evaluate(binaryExpr.getLeftExpr(), env), evaluate(binaryExpr.getRightExpr(), env)));
                default: throw new RuntimeException("Unhandled operator");
            }
        }
        else if(expr instanceof ExprList) {
            ExprList exprList = (ExprList)expr;
            QVal returnValue = evaluate(exprList.getFirst(), env);
            if(returnValue != null)
                return returnValue;

            if(exprList.getRest() != null)
                return evaluate(exprList.getRest(), env);
            return null;
        }
        else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

    boolean evaluate(Cond cond, HashMap<String,QVal> env) {
        if(cond instanceof CompCond) {
            CompCond compCond = (CompCond) cond;
            switch(compCond.getOperator()) {
                case CompCond.EQ : return  ((QInt)evaluate(compCond.getLeftExpr(), env)).value == ((QInt)evaluate(compCond.getRightExpr(), env)).value;
                case CompCond.NE : return ((QInt)evaluate(compCond.getLeftExpr(), env)).value != ((QInt)evaluate(compCond.getRightExpr(), env)).value;
                case CompCond.LT : return  ((QInt)evaluate(compCond.getLeftExpr(), env)).value < ((QInt)evaluate(compCond.getRightExpr(), env)).value;
                case CompCond.GT : return  ((QInt)evaluate(compCond.getLeftExpr(), env)).value > ((QInt)evaluate(compCond.getRightExpr(), env)).value;
                case CompCond.LE : return ((QInt)evaluate(compCond.getLeftExpr(), env)).value <= ((QInt)evaluate(compCond.getRightExpr(), env)).value;
                case CompCond.GE : return ((QInt)evaluate(compCond.getLeftExpr(), env)).value >= ((QInt)evaluate(compCond.getRightExpr(), env)).value;
            }
        } else if(cond instanceof LogicalCond) {
            LogicalCond logicalCond = (LogicalCond) cond;
            switch(logicalCond.getOperator()) {
                case LogicalCond.AND : return  evaluate(logicalCond.getLeftCond(), env) && evaluate(logicalCond.getRightCond(), env);
                case LogicalCond.OR : return evaluate(logicalCond.getLeftCond(), env) || evaluate(logicalCond.getRightCond(), env);
                case LogicalCond.NOT : return  !evaluate(logicalCond.getLeftCond(), env);
            }
        } else {
            throw new RuntimeException("Unknown condition type");
        }
        return false;
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
