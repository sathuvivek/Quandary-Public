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
*/

int length(Q list) {
  if(isNil(list) == 1)
    return 0;
  if(isAtom(list) == 1)
    return 1;
  return 1 + length(right((Ref)list));
}
Q unmemoizable(Q lister) {
    return (lister.randomInt(100000)).randomInt(10000);
}
Q testUnmemoizable() {
   mutable int i = 0;
   mutable int j = 0;
   mutable int k = 1;
   mutable Q list = 1;


   while(i < 10) {
       list = 1;
       k = 1;
        while(k < 10) {
           list = list.randomInt(k);
           k = k + 1;
        }
       j = 0;
       while(j < 1000) {
            Q val =  unmemoizable(list);
            j = j + 1;
       }
       i = i + 1;
   }

   return list;
}
/*
int fisherYatesShuffle(mutable Q lst, int n) {
    mutable int i = n - 1;
    mutable int j = 0;
    while(i > 0) {
        j = randomInt(0, i);

    }

}
*/
int max(Ref list) { /* assume list is a non-empty list of integers */
    if (isNil(right(list)) != 0)
        return (int)left(list);
    if ((int)left(list) > (int)left((Ref)right(list)) &&
    (int)left(list) > max((Ref)right(list))) {
        return (int)left(list);
    }
    return max((Ref)right(list));
}

Ref adversarialList(int k) {
    mutable int n = k;
   mutable Q list = n.nil;
   while(n > 0) {
        n = n - 1;
        if(n != 0)
            list = n.list;

   }
   return (Ref)list;

}
Ref revAdversarialList(int k) {
   mutable int i = 1;
   mutable int n = k;
   mutable Q list = i.nil;

   while(n > 0) {
        n = n - 1;
         i = i +1;
        if(n != 0)
           list = i.list;


   }
   return (Ref)list;

}
int main(int args) {

/*
print(isSorted((3 . (5 . nil)) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) .((2 . (3 . (56 . (92 . nil))) . nil)))), lteLength));
    print(isSorted((3 . (5 . (5 . nil))) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))). ((2 . (3 . (56 . (92 . nil))) . nil)))), lteLength));

    Q val = testUnmemoizable();
    print(revAdversarialList(1000));
     print(max(adversarialList(1000)));

*/

 print(max(adversarialList(1000)));

    return 1;

}