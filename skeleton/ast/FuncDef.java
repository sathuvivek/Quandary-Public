package ast;

public class FuncDef extends ASTNode {

    final String funcName;
    final FormalDeclList params;
    final StmtList body;
    public FuncDef(String funcName, FormalDeclList params, StmtList body, Location loc) {
        super(loc);
        this.funcName = funcName;
        this.params = params;
        this.body = body;
    }

    public String getFuncName() {
        return funcName;
    }

    public FormalDeclList getParams() {
        return params;
    }

    public StmtList getBody() {
        return body;
    }

    @Override
    public String toString() {
        return funcName + " ( " + params.toString() + " ) " + " { \n" + body.toString() + "\n}";
    }
}
