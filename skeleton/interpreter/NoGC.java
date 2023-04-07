package interpreter;

import java.util.Map;

public class NoGC implements GarbageCollector{
    MemoryManager manager;

    public NoGC() {
        manager = MemoryManager.getInstance();
    }
    @Override
    public void triggerGC() {

    }

    @Override
    public void freeRoot(Map<String,QVal> env, long addr) {

    }
}
