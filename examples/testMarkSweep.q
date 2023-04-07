mutable int main(int args) {
    Ref a = 2. 3. nil;
    Ref b = 4.5.nil;
    called();
    againCalled();
    return 3;
}
mutable int called() {
    Ref c = 6.7.nil;
    Ref d = 8.9.nil;
    return 1;
}
mutable int againCalled() {
    Ref e = 10.11.nil;
        Ref f = 12.13.nil;
        return 2;
}