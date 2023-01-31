mutable Ref main(int arg) {
    mutable Ref r = 3 . 5;
    Q s = r;
    setLeft(r,1);
    r = 4.s;
    print left(r);
    print right(r);
    return r;
}