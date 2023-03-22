int equiv(Q val1, Q val2) {
    if(isNil(val1) == 1 && isNil(val2) == 1)
        return 1;
    if(isAtom(val1) == 1 && isAtom(val2) == 1) {
        if((int)val1 == (int)val2) {
            return 1;
        } else
            return 0;
    } else if(isAtom(val1) == 1 || isAtom(val2) == 1){
        return 0;
    } else {
        Q left1 = left((Ref)val1);
        Q left2 = left((Ref)val2);
        Q right1 = right((Ref)val1);
        Q right2 = right((Ref)val2);
        int leftEQ = equiv(left1, left2) ;
        if(leftEQ != 1)
            return 0;
        else
            return equiv(right1, right2);
    }
    return 0;
}


int length(Q list) {
  if(isNil(list) == 1)
    return 0;
  if(isAtom(list) == 1)
    return 1;
  return 1 + length(right((Ref)list));
}

int lteLength(Q list1, Q list2) {
  int length1 = length(list1);
  int length2 = length(list2);
  if(length1 <= length2)
    return 1;
  return 0;
}
/*
int isSorted(Ref list, int lte) {
    if (isNil(list) != 0 || isNil(right(list)) != 0)
        return 1;
    if ((int)lte(left(list), left((Ref)right(list))) != 0) {
        return isSorted((Ref)right(list), lte);
    }
    return 0;
}
int isSortedByListLength(Ref list) {
    return isSorted(list, lteLength);
}
int max(Ref list) { /* assume list is a non-empty list of integers */
    if (isNil(right(list)) != 0)
        return (int)left(list);
    if ((int)left(list) > (int)left((Ref)right(list)) &&
    (int)left(list) > max((Ref)right(list))) {
        return (int)left(list);
    }
    return max((Ref)right(list));
}
*/

Q unmemoizable(Q lister, int n) {
    if(n > 50)
        return lister;
    return unmemoizable(randomInt(1000).lister, n+1);
}

/*
this will run for 10 seconds without memoization and 30 seconds with memoization
*/
Q testUnmemoizable() {
    mutable int i = 0;
    while(i < 50000) {
       Q list1 = unmemoizable(1, 0);
       Q list2 = unmemoizable(1,0);
       if(equiv(list1, list2) == 0)
            return 1;
       else {
            i = i+1;
       }
   }
   return 1;
}

Ref createDecreasingList(int n, int maxNumber) {
    if(n <= 0) {
        return maxNumber.nil;
    }
    return n.createDecreasingList(n-1, maxNumber);
}

Ref createNormalDecreasingList(int n) {
    if(n <= 0) {
        return nil;
    }
    return n.createNormalDecreasingList(n-1);
}

Ref adversarialList(int n) {
    return createDecreasingList(n-1, n);
}

int main(int args) {

    print(equiv(1.nil, (1.(1.nil))));

    print(isSortedByListLength((3 . (5 . nil)) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) .((2 . (3 . (56 . (92 . nil))) . nil))))));
    print(isSortedByListLength((3 . (5 . (5 . nil))) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))). ((2 . (3 . (56 . (92 . nil))) . nil))))));

    Ref list1 = createNormalDecreasingList(20);
    print(max(list1));

    Ref list2 = adversarialList(20);
    print(max(list2));

    Q Val = testUnmemoizable();
    return 1;
}