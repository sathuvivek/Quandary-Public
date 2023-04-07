#!/bin/bash

MYDIR=`dirname $BASH_SOURCE`

runinterp()
{
  java -cp "$MYDIR/java-cup-11b-runtime.jar:$MYDIR/quandary-obfuscated.jar" -ss1g -ea interpreter.Interpreter $*
}

q1()
{
  SUB="$1"
  t="$(mktemp).q"
  cp "$SUB" $t
  cat <<EOF >> $t
Q testq1()
{
    Q v = 1 . 2 . 3 . 4 . 5;
    Q w = (1 . 2) . 3 . (4 . 5);
    return equiv(nil, nil) .
         equiv(1, nil) .
         equiv(1, 1) .
         equiv((1 . 2), (1 . 2)) .
         equiv(v, (1 . 2 . 3 . 4 . 5)) .
         equiv(v, (1 . 2 . 3 . 4)) .
         equiv(v, (1 . 2 . 3 . 4 . nil)) .
         equiv((v . nil), v) .
         equiv((v . nil), (v . nil)) .
         equiv(v, w);
}
Q main(int _){return testq1();}
EOF
  out=$(runinterp $t 0)
  [ "$out" = "Interpreter returned (((((((((1 . 0) . 1) . 1) . 1) . 0) . 0) . 0) . 1) . 0)" ]
  r=$?
  if [ $r -ne 0 ]; then echo $out; fi
  rm $t
  return $r
}

q2()
{
  SUB="$1"
  t="$(mktemp).q"
  cp "$SUB" $t
  cat <<EOF >> $t
int isSorted(Ref list, int lte) {
    if (isNil(list) != 0 || isNil(right(list)) != 0)
        return 1;
    if ((int)lte(left(list), left((Ref)right(list))) != 0) {
        return isSorted((Ref)right(list), lte);
    }
    return 0;
}
Q testq2()
{
    return
    isSorted((3 . (5 . (5 . nil))) .
             ((2 . (8 . nil)) .
             ((6 . (7 . (4 . nil))) .
             ((2 . (3 . (56 . (92 . nil)))
             . nil)))), lteLength) .
    isSorted((3 . (5 . nil)) .
             ((2 . (8 . nil)) .
             ((6 . (7 . (4 . nil))) .
             ((2 . (3 . (56 . (92 . nil)))
             . nil)))), lteLength) .
    isSorted(nil .
             ((2 . (8 . nil)) .
             ((6 . (7 . (4 . nil))) .
             ((2 . (3 . (56 . (92 . nil)))
             . nil)))), lteLength) .
    isSorted((3 . (5 . nil)) .
             (nil .
             ((6 . (7 . (4 . nil))) .
             ((2 . (3 . (56 . (92 . nil)))
             . nil)))), lteLength);
}
Q main(int _){return testq2();}
EOF
  out=$(runinterp $t 0)
  [ "$out" = "Interpreter returned (((0 . 1) . 1) . 0)" ]
  r=$?
  if [ $r -ne 0 ]; then echo $out; fi
  rm $t
  return $r
}

q3()
{
  SUB="$1"
  t="$(mktemp).q"
  cp "$SUB" $t
  cat <<EOF >> $t
Q main(int _){return testUnmemoizable();}
EOF
  t1=$( TIMEFORMAT="%R"; { time runinterp $t 0 > /dev/null 2>&1; } 2>&1 )
  t2=$( TIMEFORMAT="%R"; { time runinterp -memoize unmemoizable $t 0 > /dev/null 2>&1; } 2>&1 )
  echo $t1 $t2
  rm $t
  return $(bc <<< "!$t1*3<=$t2")
}

q4()
{
  SUB="$1"
  t="$(mktemp).q"
  cp "$SUB" $t
  cat <<EOF >> $t
int max(Ref list) {
    if (isNil(right(list)) != 0) return (int)left(list);
    if ((int)left(list) > (int)left((Ref)right(list)) &&
        (int)left(list) > max((Ref)right(list))) {
        return (int)left(list);
    }
    return max((Ref)right(list));
}
Q main(int _){Q t=adversarialList(1000);return nil;}
EOF
  runinterp -heapsize 1638400 $t 0 # > /dev/null 2>&1
  t1=$?

  cp "$SUB" $t
  cat <<EOF >> $t
int max(Ref list) {
    if (isNil(right(list)) != 0) return (int)left(list);
    if ((int)left(list) > (int)left((Ref)right(list)) &&
        (int)left(list) > max((Ref)right(list))) {
        return (int)left(list);
    }
    return max((Ref)right(list));
}
Q main(int _){int t=max(adversarialList(1000));return nil;}
EOF
  runinterp -heapsize 1638400 $t 0 # > /dev/null 2>&1
  t2=$?
  echo $t1 $t2
  # as long as the second one is killed, we are good
  [ $t1 -eq 0 -a $t2 -gt 127 ]
}

POINTS=0
ulimit -t 30
q1 "$1" && let $((POINTS += 5)) || echo "Q1"
q2 "$1" && let $((POINTS += 5)) || echo "Q2"
q3 "$1" && let $((POINTS += 5)) || echo "Q3"
q4 "$1" && let $((POINTS += 5)) || echo "Q4"
echo "$POINTS/20"