/*
mutable int main(int args) {
    Ref q = 2.nil;
    Ref r = 2.q;
    Ref k = (Ref)right(r);

    print(k);
    print(r);
    free r;
    print(k);
    print(k);
    return 1;

}
*/
Q main(int arg) {
  return combine(((5 . 8). foo(arg)), foo(arg));
}

Q foo(mutable int arg) {
  mutable Q x = nil;
  while (arg > 0) {
    x = arg . arg;
    arg = arg - 1;
  }
  return x;
}
Q combine(Q a, Q b) {
  return a . b;
}