package interpreter;

import java.util.Map;

public interface GarbageCollector {
    void triggerGC();

    void freeRoot(Map<String, QVal> env, long addr);
}
