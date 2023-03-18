package interpreter;

public class QList extends QRef{
    final boolean isNonEmpty;
    QList(QObj Referent) {
        super(Referent);
        if(Referent != null)
            isNonEmpty = Referent.isList;
        else
            isNonEmpty = false;
    }

    public boolean getIsNonEmpty() {
        return isNonEmpty;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
