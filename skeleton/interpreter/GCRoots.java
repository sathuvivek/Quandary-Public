package interpreter;

import java.util.*;

public class GCRoots {
    public static Map<Long, Boolean> roots = new HashMap<Long, Boolean>();

    public static void push(long addr) {
 //       System.out.println("Adding to Root : " + addr);
        roots.put(addr, true);
    }

    public static void pop(long addr) {
//        System.out.println("Removing from root : " + addr);
        roots.remove(addr);
    }

    public static List<Long> getRoots() {
        return new ArrayList<Long>(roots.keySet());
    }

    public static void removeRoots(List<Long> tempRoots) {
        Collections.sort(tempRoots);
//        System.out.println("Roots to remove : " + tempRoots.toString());
        for(long root : tempRoots) {
            pop(root);
        }
    }

    public static void prepRoots(Map<String, QVal> env) {
//        System.out.println("Iterating over environment to prepare GCRoots");
//        System.out.println(env.keySet());
        for(String key : env.keySet()) {
            QVal value = env.get(key);
//            System.out.println("For variable : " + key + " With value : " + value);
//            System.out.println("For variable : " + key);
            if(value instanceof QRef) {
                long addr = ((QRef) value).getAddress();
                if(addr != -1 && addr != Long.MIN_VALUE) {
//                    System.out.println("Adding it to GC Roots : " + addr);
                    push(addr);
                }
            }
        }
    }

    public static void unPrepRoots(Map<String, QVal> env) {
//        System.out.println("Iterating over environment to un prepare GCRoots");
//        System.out.println(env.keySet());
        for(String key : env.keySet()) {
            QVal value = env.get(key);
//            System.out.println("For variable : " + key + " With value : " + value);
//            System.out.println("For variable : " + key);
            if(value instanceof QRef) {
                long addr = ((QRef) value).getAddress();
                if(addr != -1 && addr != Long.MIN_VALUE) {
//                    System.out.println("removing addr from GC Roots : " + addr);
                    pop(addr);
                }
            }
        }
    }
}
