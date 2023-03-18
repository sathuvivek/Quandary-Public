package interpreter;

class QRef extends QVal{
    final QObj referent;
    private int alreadyHashed;
    QRef(QObj Referent) {
        this.referent = Referent;
    }

    boolean isNonNil() {
        return !(referent == null);
    }

    @Override
    public String toString() {
        if(referent == null) {
            return "nil";
        }
        return referent.toString();
    }

    @Override
    public int hashCode() {
        if(alreadyHashed != 0) {
            return alreadyHashed;
        }
        int result = 17;
        result = 31 * result + (referent == null ? 0 : referent.hashCode());
        alreadyHashed = result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println("QRef object equality check");
        if(obj == this)
            return true;
        System.out.println("QRef Not exact match");
        if(obj == null && this == null) {
            return true;
        }
        if(obj instanceof QRef) {
            System.out.println("is an instance");
            QRef obj1 = (QRef) obj;
            if(obj1.referent == this.referent) {
                System.out.println("Direct referent match");
                return true;
            }
            if(obj1.referent.equals(this.referent)) {
                System.out.println("referent equality match");
                return true;
            }
        }
        System.out.println("Not a Qref match");
        return false;
    }
}
