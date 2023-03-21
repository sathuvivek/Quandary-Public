package interpreter;

class QObj {
    QVal left;
    QVal right;

    boolean isList;

    private int alreadyHashed;

    QObj(QVal left, QVal right) {
        this.left = left;
        this.right = right;
        if(right instanceof QList)
            isList = true;
    }

    @Override
    public String toString() {
        return "(" + left + " . " + right + ")";
    }

    @Override
    public int hashCode() {
        int result = 17;
        if(alreadyHashed != 0) {
            return alreadyHashed;
        }
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        result = 31 * result + (isList ? 1 : 0);
        alreadyHashed = result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
     //   System.out.println("Checking Equality for QObj");
        if(obj == this)
            return true;
     //   System.out.println("Not Exact match for QObj");
        if(obj == null && this == null) {
            return true;
        }
        if(obj instanceof QObj) {
       //     System.out.println("Is an QObj");
            QObj obj1 = (QObj)obj;
            if(obj1.left == this.left && obj1.right == this.right) {
                return true;
            }
            if(obj1.left != null && obj1.right != null && this.left != null && this.right != null) {
                if (obj1.left.equals(this.left) && obj1.right.equals(this.right)) {
          //          System.out.println("is an exact match");
                    return true;
                }

            }
        }
    //    System.out.println("QObj not a match");
        return false;
    }
}
