package interpreter;

import java.util.*;

public class GCRoots {
    public static Map<Long, Boolean> roots = new HashMap<Long, Boolean>();

    public static void push(long addr) {
        roots.put(addr, true);
    }

    public static void pop(long addr) {
        roots.remove(addr);
    }

    public static List<Long> getRoots() {
        return new ArrayList<Long>(roots.keySet());
    }

    public static void removeRoots(List<Long> tempRoots) {
        Collections.sort(tempRoots);
        for(long root : tempRoots) {
            pop(root);
        }
    }

    public static void prepRoots(Map<String, QVal> env) {
        for(String key : env.keySet()) {
            QVal value = env.get(key);
            if(value instanceof QRef) {
                long addr = ((QRef) value).getAddress();
                if(addr != -1 && addr != Long.MIN_VALUE) {
                    push(addr);
                }
            }
        }
    }

    public static void unPrepRoots(Map<String, QVal> env) {
        for(String key : env.keySet()) {
            QVal value = env.get(key);
            if(value instanceof QRef) {
                long addr = ((QRef) value).getAddress();
                if(addr != -1 && addr != Long.MIN_VALUE) {
                    pop(addr);
                }
            }
        }
    }
}
