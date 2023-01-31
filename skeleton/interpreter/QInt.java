package interpreter;

class QInt extends QVal{
    final long value;
    QInt(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
