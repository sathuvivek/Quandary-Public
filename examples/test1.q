mutable Q main(int arg) {
    mutable Q r = 3 . 5;
    if (randomInt(2) == 1) {
        r =4;
    }
    if(isAtom(r) !=0) {
        return (int)r;
    }
    return (int)left((Ref)r) + (int)right((Ref)r);
}