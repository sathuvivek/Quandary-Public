package interpreter;

class QRef extends QVal{
    final QObj referent;
    QRef(QObj Referent) {
        this.referent = Referent;
    }

    @Override
    public String toString() {
        if(referent == null) {
            return "nil";
        }
        return referent.toString();
    }
}
