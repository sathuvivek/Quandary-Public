mutable int main(int arg) {
   return foo(arg);
}

mutable int foo(mutable Q x) {
    x = 1.nil;
    setRight((Ref)x,x);
    print(x);
    x = 42;
    x = nil;
    return 0;
}

