package ast;

import interpreter.Interpreter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CastExpr extends Expr {

    final Expr expr;
    final Type type;
    public CastExpr(Type type, Expr expr1, Location loc) {
        super(loc);
        this.expr = expr1;
        this.type = type;
    }

    public Expr getExpr() {
        return expr;
    }

    public Type getType() { return type;};

    @Override
    public String toString() {
        String s = null;
        return type.toString() + " " + expr.toString() ;
    }

    @Override
    boolean check(HashMap<String, FuncDef> environmentFunctions, HashMap<String, VarDecl> environmentVariable,boolean isMutable, Type returnType) {

        expr.check(environmentFunctions, environmentVariable, isMutable, returnType);
        Type expressionType = expr.getStaticType();
        Type[] downCast = {Type.Q, type};
        List<Type> downCastList = Arrays.asList(downCast);
        Type[] upCast = {Type.Q, expressionType};
        List<Type> upCastList = Arrays.asList(upCast);

       // System.out.println("");
        if( !(downCastList.contains(expressionType) || upCastList.contains(type))) {
            Interpreter.fatalError( "Illegal Cast operation at "+ loc.toString(), Interpreter.EXIT_STATIC_CHECKING_ERROR);
            return false;
        }
        return false;
    }

    @Override
    Type getStaticType() {
        return type;
    }
}
