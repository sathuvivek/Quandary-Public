package interpreter;

class QObj {
    QVal left;
    QVal right;

    QObj(QVal left, QVal right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + " . " + right + ")";
    }
}
