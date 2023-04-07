package interpreter;

import java.io.*;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;
import parser.ParserWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

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

   // public static List<Long> tempRoots = new ArrayList<Long>();

    static private Interpreter interpreter;

    static private WeakHashMap<String, WeakHashMap> mapper = new WeakHashMap<>();
    //static private WeakHashMap<List<QVal>, QVal> memoizer = new WeakHashMap<>();
    static private boolean hasMemoization;
    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static MemoryManager memoryManager;

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
           // System.out.println("args.length : " + args.length);
            for (; i < args.length; i++) {
                String arg = args[i];
//                System.out.println("Arg is " + arg);
//                System.out.println("i val is " + i);
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else if (arg.equals("-et")) {
                        IsExtended.setValue(true);
                    } else if (arg.equals("-memoize")) {
                       // memoizeName = args[i + 1];
                        hasMemoization = true;
                        mapper.put(args[i + 1], new WeakHashMap<CustomArrayList<QVal>, QVal>());
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
            System.out.println(ex);
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
     //   astRoot.println(System.out);
        astRoot.check(Context.newContext());
        memoryManager = MemoryManager.getInstance(heapBytes, gcType);
        memoryManager.setGC();
        interpreter = new Interpreter(astRoot);
       // interpreter.initMemoryManager(gcType, heapBytes);
        QVal returnVal = interpreter.executeRoot(astRoot, quandaryArg);
//        System.out.println("Program has returned something");
//        System.out.println("Return is int? : " + (returnVal instanceof QInt));
//        System.out.println("Return is Ref? : " + (returnVal instanceof QRef));
//        if(returnVal instanceof QRef) {
////            System.out.println("Addr of return value : " + ((QRef)returnVal).getAddress());
////            memoryManager.printFullBlock(((QRef)returnVal).getAddress());
//        }
        String returnValueAsString = returnVal.toString();
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
        mainEnv.put(mainFuncDef.getParams().getVarDecl().getName(), new QInt(arg));

        return execute(mainFuncDef.getBody(), mainEnv);
    }

    QVal execute(StmtList sl, HashMap<String,QVal> env)  {
        QVal returnValue = execute(sl.getFirst(), env);
        if( returnValue != null)
            return returnValue;
        if(sl.getRest() != null)
            return execute(sl.getRest(), env);
        return null;
    }

    QVal execute(Stmt stmt, HashMap<String,QVal> env) {
        if (stmt instanceof AssignStmt ) {
            AssignStmt assignStmt = (AssignStmt) stmt;
            env.put(assignStmt.getVarName(), evaluate(assignStmt.getExpr(), env));
            return null;
        } else if (stmt instanceof DeclStmt) {
            DeclStmt declStmt = (DeclStmt) stmt;
            env.put(declStmt.getVarDecl().getName(), evaluate(declStmt.getExpr(), env));
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
        } else if(stmt instanceof CompoundStmt) {
            CompoundStmt compoundStmt = (CompoundStmt) stmt;
            return execute(compoundStmt.getStmtList(), env);
        }
        else if(stmt instanceof FreeStmt) {
            FreeStmt freeStmt = (FreeStmt) stmt;
            QVal val = evaluate(freeStmt.getExpr(), env);
//            System.out.println("Free stmt with expr with val :  " + val ) ;
//            System.out.println("FreeList start : " + memoryManager.freeListPointer);
            if(val instanceof QRef) {
                long addr = ((QRef) val).getAddress();
                if((addr != -1 ) && addr != Long.MIN_VALUE) {
//                        System.out.println("Freeing called for : " + addr + " | at " + freeStmt.getLoc().toString());
//                    System.out.println("Pointer before free " + memoryManager.freeListPointer);
//                    memoryManager.printBlock(memoryManager.freeListPointer);
//                    System.out.println("Freelist before: ");
//                    memoryManager.printFreeList();
                    memoryManager.freeRoot(env, ((QRef) val).getAddress());
//                    System.out.println("Freelist after: ");
//                    memoryManager.printFreeList();
//                    System.out.println("Pointer after free " + memoryManager.freeListPointer);
//                    memoryManager.printBlock(memoryManager.freeListPointer);
                }
            }
 //           System.out.println("FreeList at end : " + memoryManager.freeListPointer);


            return null;
        }
        else {
            throw new RuntimeException("Unhandled Smt time");
        }
    }

    QVal evaluate(Expr expr, HashMap<String,QVal> env) {
        if (expr instanceof NilExpr) {
            if(IsExtended.getValue())
                return new QList(null);
            else
                return new QRef(Long.MIN_VALUE);
               // return new QRef(0L);
        } else if (expr instanceof ConstExpr) {
            return new QInt((long)((ConstExpr) expr).getValue());
        } else if(expr instanceof IdentExpr) {
            return env.get(((IdentExpr)expr).getVarName());
        } else if (expr instanceof UnaryMinusExpr) {
            UnaryMinusExpr ume = (UnaryMinusExpr) expr;
            QInt v = (QInt)evaluate(ume.getExpr(), env);
            return new QInt(-v.value);
        } else if (expr instanceof  CastExpr) {
            CastExpr castExpr = (CastExpr) expr;
            QVal value = evaluate(castExpr.getExpr(), env);
            if(castExpr.getType() == Type.REF && ! (value instanceof QRef) ||
                castExpr.getType() == Type.INT && ! (value instanceof QInt))   {
//                System.out.println(castExpr.toString());
//                System.out.println(" castExpr type : " + castExpr.getType());
//                System.out.println(" castExpr value : " + value);
//                System.out.println("Expr inside cast Expr : " + castExpr.getExpr().toString());
//                System.out.println("Error at : " + castExpr.getLoc());
                Interpreter.fatalError("Failed dynamic typecast traditional", Interpreter.EXIT_DYNAMIC_TYPE_ERROR);
            }
            if(IsExtended.getValue()) {
               // System.out.println("Extended : trying to cast castExpr : " + castExpr.getType() + " | value : " + value.getClass().getName());
//                if(castExpr.getType() == Type.NONEMPTYLIST) {
//                    System.out.println("is List " + (value instanceof QList));
//                    System.out.println("what does it have? : " + value.toString());
//                    System.out.println("Is it empty : " + !((QList)value).isNonEmpty);
//                    //System.out.println("castExpr.getType() == Type.NONEMPTYLIST && !(value instanceof QList) && !((QList)value).getIsNonEmpty() : " +  (castExpr.getType() == Type.NONEMPTYLIST && !(value instanceof QList) && !((QList)value).getIsNonEmpty()));
//                }
                if(((castExpr.getType() == Type.LIST) && !(value instanceof QList)) ||
                        (castExpr.getType() == Type.NONNILREF && !(value instanceof QRef)) ||
                        (castExpr.getType() == Type.NONEMPTYLIST && (!(value instanceof QList) || !((QList)value).getIsNonEmpty()))
                )
                {
                    Interpreter.fatalError("Failed dynamic typecast for extended : castExpr : " + castExpr.getType() + " | value : " + value.getClass().getName(), Interpreter.EXIT_DYNAMIC_TYPE_ERROR);
                }
            }
            return value;
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
                checkRef(r);
                long address = r.getAddress();
                if(address == -1 || address == Long.MIN_VALUE)
                    return new QRef(-1);
                return memoryManager.getLeft(address);
               // return r.referent.left;
            } else if (callExpr.getFuncName().equals("right")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                checkRef(r);
                long address = r.getAddress();
                if(address == -1 || address == Long.MIN_VALUE)
                    return new QRef(-1);
                return memoryManager.getRight(address);
                // return r.referent.right;
            } else if (callExpr.getFuncName().equals("setLeft")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                QVal val = evaluate(callExpr.getArgs().getRest().getFirst(), env);
                checkRef(r);
                long address = r.getAddress();
                memoryManager.setLeft(address, val);
               // r.referent.left = val;
                return new QInt(1);
            } else if (callExpr.getFuncName().equals("setRight")) {
                QRef r = (QRef) evaluate(callExpr.getArgs().getFirst(), env);
                QVal val = evaluate(callExpr.getArgs().getRest().getFirst(), env);
                checkRef(r);
                long address = r.getAddress();
                memoryManager.setRight(address, val);
               // r.referent.right = val;
                return new QInt(1);
            } else if (callExpr.getFuncName().equals("isAtom")) {
                QVal r =  evaluate(callExpr.getArgs().getFirst(), env);
                if(r instanceof QInt || (r instanceof QRef && ((((QRef)r).getAddress() == Long.MIN_VALUE) || (((QRef)r).getAddress() == -1)))) { //((QRef)r).referent == null)
                    return new QInt(1);
                }
                return new QInt(0);
            } else if (callExpr.getFuncName().equals("isNil")) {
                QVal r = evaluate(callExpr.getArgs().getFirst(), env);

                if(r instanceof QRef && ((((QRef)r).getAddress() == Long.MIN_VALUE) || (((QRef)r).getAddress() == -1))) { //((QRef)r).referent == null
                    return new QInt(1);
                }
                return new QInt(0);
            }

            if(IsExtended.getValue()) {
                if( callExpr.getFuncName().equals("first")) {
                    QList r = (QList) evaluate(callExpr.getArgs().getFirst(), env);
                    if(!r.isNonNil()) {
                        Interpreter.fatalError("Failed dynamic typecast extended", Interpreter.EXIT_DYNAMIC_TYPE_ERROR);
                    }
                    //checkRef(r);
                    return r.referent.left;
                } else if(callExpr.getFuncName().equals("rest")) {
                    QList r = (QList) evaluate(callExpr.getArgs().getFirst(), env);
                    if(!r.isNonNil()) {
                        Interpreter.fatalError("Failed dynamic typecast extended", Interpreter.EXIT_DYNAMIC_TYPE_ERROR);
                    }
                    return r.referent.right;
                }
            }
           FuncDef callee =  astRoot.getFuncDefList().lookupFuncDef(callExpr.getFuncName());
           HashMap<String, QVal> calleeEnv = new HashMap<String, QVal>();
           FormalDeclList currentFormalDeclList = callee.getParams();
           ExprList currentExprList = callExpr.getArgs();
           CustomArrayList<QVal> argumentList = new CustomArrayList<>();
           List<Long> tempFuncRoots = new ArrayList<Long>();
           while(currentFormalDeclList != null) {
               QVal exprVal = evaluate(currentExprList.getFirst(),env);
               argumentList.add(exprVal);
               if(exprVal instanceof QRef) {
                   long tempAddress = ((QRef)exprVal).getAddress();
                   if(tempAddress != -1 && tempAddress != Long.MIN_VALUE) {
//                       System.out.println("Putting memory as temp key in env : " + tempAddress);
                       env.put(Long.toString(tempAddress), exprVal);
                       tempFuncRoots.add(tempAddress);
                   }
               }
               calleeEnv.put(currentFormalDeclList.getVarDecl().getName(), exprVal);
               currentFormalDeclList = currentFormalDeclList.getRest();
               currentExprList = currentExprList.getRest();
           }
           for(Long root : tempFuncRoots) {
               env.remove(Long.toString(root));
           }
           String funcName = callExpr.getFuncName();
           if(hasMemoization && mapper.containsKey(funcName)) {
             //  System.out.println("Called func : " + callExpr.getFuncName());
               WeakHashMap<CustomArrayList<QVal>, QVal> memTable = mapper.get(funcName);
             //  System.out.println("Has resultant memtable : " + memTable.toString());
              // System.out.println("argument sent : " + argumentList.toString());
           //    System.out.println("computing hashcode : "  );
//               int hashCode = argumentList.hashCode();
//               System.out.println(" hashcode : " + hashCode );
               QVal cachedResult = memTable.get(argumentList);
               if (cachedResult != null) {
                //   System.out.println("Got cached result:  " + cachedResult.toString()  ); //+ cachedResult
                   return cachedResult;
               } else {
                   //System.out.println("No cache results ");
               }
           }

//           if(funcName.equals("fibMemHog"))
//                System.out.println("Calling function : " + funcName + " | with arg : " + argumentList.toString());
            GCRoots.prepRoots(env);
           QVal output =  execute(callee.getBody(), calleeEnv);
            GCRoots.unPrepRoots(env);
//           if(tempRootsDup.size() > 0) {
//
////               System.out.println("calling remove temp roots for the scope : " + tempRootsDup.toString());
//               memoryManager.removeTempRoots(tempRootsDup);
//           }
           if(hasMemoization && mapper.containsKey(funcName)) {
               WeakHashMap<CustomArrayList<QVal>, QVal> memTable = mapper.get(funcName);
               memTable.put(argumentList, output);
               mapper.put(funcName, memTable);
           //    System.out.println("Storing value  : "  + output.toString() ); //" | " + output.toString()
              // System.out.println("New MemTable " + memTable.toString());
           }

           return output;
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), env)).value + ((QInt)evaluate(binaryExpr.getRightExpr(), env)).value);
                case BinaryExpr.MINUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), env)).value - ((QInt)evaluate(binaryExpr.getRightExpr(), env)).value);
                case BinaryExpr.TIMES: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), env)).value * ((QInt)evaluate(binaryExpr.getRightExpr(), env)).value);
                case BinaryExpr.DOT:



//                    GCRoots.push(rootAddress);

//                    System.out.println("Adding to remove : " + rootAddress);
//                    System.out.println("Binary expression allocated at  : " + rootAddress  ); //+  " for expr : "  + binaryExpr.toString()
//                    System.out.println("evaluating left for : ");
//                    System.out.println("LEFT : " + binaryExpr.getLeftExpr().toString());
                    QVal left = evaluate(binaryExpr.getLeftExpr(), env);

//                    System.out.println(" left val : " + left.toString() + " | at : " + binaryExpr.getLoc().toString());
                    if(left instanceof QRef) {
                        long leftTempAddress = ((QRef)left).getAddress();
                        if(leftTempAddress != -1 && leftTempAddress != Long.MIN_VALUE) {
//                            System.out.println("Putting memory as key in env left : " + leftTempAddress);
                            env.put(Long.toString(leftTempAddress), left);
                        }
                    }
//                    GCRoots.pop(rootAddress);
//                    GCRoots.push(rootAddress);

//                    System.out.println("evaluating right for " + rootAddress);
//                   System.out.println("RIGHT : " + binaryExpr.getRightExpr().toString());
                    QVal right = evaluate(binaryExpr.getRightExpr(), env);

//                    System.out.println(" right val : " + right.toString() + " | at : " + binaryExpr.getLoc().toString());
                    if(right instanceof QRef) {
                        long rightTempAddress = ((QRef)right).getAddress();
                        if(rightTempAddress != -1 && rightTempAddress != Long.MIN_VALUE) {
//                            System.out.println("Putting memory as key in env right : " + rightTempAddress);
                            env.put(Long.toString(rightTempAddress), right);
                        }
                    }
//                    System.out.println("for " + rootAddress + " right val : " + right.toString());

//                    memoryManager.printBlock(rootAddress);
//                    if(left instanceof QRef) {
//                        GCRoots.pop(((QRef)left).getAddress());
//                    }
//                    GCRoots.pop(rootAddress);
                    long rootAddress = memoryManager.allocate(env);
//                    System.out.println("Allocate called for : " + rootAddress + " | at : " + binaryExpr.getLoc().toString());
                    memoryManager.setRight(rootAddress, right);
                    memoryManager.setLeft(rootAddress, left);

                    if(left instanceof QRef) {
                        long leftTempAddress = ((QRef)left).getAddress();
                        if(leftTempAddress != -1 && leftTempAddress != Long.MIN_VALUE) {
//                            System.out.println("Removing memory as key in left : " + leftTempAddress);
                            env.remove(Long.toString(leftTempAddress));
                        }
                    }
                    if(right instanceof QRef) {
                        long rightTempAddress = ((QRef)right).getAddress();
                        if(rightTempAddress != -1 && rightTempAddress != Long.MIN_VALUE) {
//                            System.out.println("Removing memory as key in right : " + rightTempAddress);
                            env.remove(Long.toString(rightTempAddress));
                        }
                    }
                   // tempRoots.add(rootAddress);
                    if(right instanceof QList) {
                        return new QList( new QObj(left, right));
                    } else {
                        QVal returnVal = new QRef(rootAddress);
                        //GCRoots.push(rootAddress);
                       // env.put(Long.toString(rootAddress), returnVal);
                        return returnVal;
                    }
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

    void checkRef(QRef value) {
        if(value.getAddress() == 0) {
            Interpreter.fatalError("Nil dereference", Interpreter.EXIT_NIL_REF_ERROR);
        }
//        if(value.referent == null && !IsExtended.getValue()) {
//            Interpreter.fatalError("Nil dereference", Interpreter.EXIT_NIL_REF_ERROR);
//        }
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
