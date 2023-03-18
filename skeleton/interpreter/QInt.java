package interpreter;

import java.util.Objects;

class QInt extends QVal{
    final long value;
    private int alreadyHashed;
    QInt(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        if(alreadyHashed != 0) {
            return alreadyHashed;
        }
        int result =  Objects.hash(value);
        alreadyHashed = result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
       // System.out.println("QInt equality test");
        if(obj == this)
            return true;
       // System.out.println("QInt object didnt match");
        if(obj instanceof QInt) {
            //System.out.println("QInt they are QInts");
            QInt obj1 = (QInt) obj;
            if(obj1.toString().equals( this.toString())) {
                //System.out.println("QInt String match worked");
                return true;
            }
            //System.out.println("QInt String match did not worked");
        }
        return false;
    }
}
