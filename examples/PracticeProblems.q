int isList(Q list) {
    if(isNil(list) == 1) {
        return 1;
    }
    if(isAtom(list) == 1) {
        return 0;
    }
    return isList(right((Ref)list));
}
int main(int args) {
    Q list = (1 . (2 . (3 . nil)));
    print(isSorted((3 . (5 . nil)) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) .((2 . (3 . (56 . (92 . nil))) . nil))))));
     return 1;
}

Ref append(Ref list1, Ref list2) {

    if(isNil(list1) == 1) {
        return list2;
    }
    if(isAtom(list1) == 1) {
        return list1 . append(list2, nil);
    }

    Q leftL = left(list1);
    Q rightL = right(list1);
    if(isNil(rightL) == 1) {
        return leftL . append(list2, nil);
    }
    return leftL . append((Ref)rightL, list2);
}

Ref reverse(Ref list) {
    if(isNil(list) == 1) {
        return list;
    }
    Q leftL = left(list);
    Q rightL = right(list);
    if(isNil(rightL) == 1) {
        return leftL.nil;
    }
    Ref reversedVal = reverse((Ref)rightL);
    Ref val =  append(reversedVal, leftL.nil);
    return val;
}

int getLeftest(Ref list) {
    if(isNil(list) == 1) {
        return 0;
    }

    Q leftL = left(list);
    if(isNil(leftL) == 1)
        return 0;
    if(isAtom(leftL) == 1)
        return (int)leftL;
    return getLeftest((Ref) leftL);
}

int isSorted(Ref list) {
    Q leftL = left(list);
    Q rightL = right(list);
    print(leftL);
    print(rightL);
    if((isNil(rightL) == 1)) {
        return 1;
    }
    if(isAtom(rightL) == 1) {
        if(isAtom(leftL) != 1) {
            return isSorted((Ref)leftL);
        } else if((int)rightL >= (int)leftL) {
            return 1;
        } {
            return 0;
        }
    }
    if(isAtom(leftL) == 1) {
        if(isAtom(rightL) != 1 ) {
             int leftestOfRight = getLeftest((Ref)rightL);
             print(1111111111);
             print(leftestOfRight);
             print(1111111111);
             if(isNil(leftL) != 1 && leftestOfRight >= (int)leftL) {
                  int valval =  isSorted((Ref)rightL);
                  if(valval != 0)
                    return valval + 1;
                  else
                    return 0;
            } else
                return 0;

        } else if( (int)rightL >= (int)leftL) {
            return 1;
        }

     }
    int lefter = isSorted((Ref)leftL);
     int righter = isSorted((Ref)rightL);
     if(lefter == 0 || righter == 0)
        return 0;
     return lefter+righter;
}

