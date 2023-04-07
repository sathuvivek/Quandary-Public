package interpreter;

import java.util.Map;

public class ExplicitGC implements GarbageCollector{

    MemoryManager manager;

    RawMemory memory;

    public ExplicitGC() {
//        System.out.println("Explicit GC has initialized");
        manager = MemoryManager.getInstance();
        memory = manager.getMemory();

    }
    @Override
    public void triggerGC() {

    }

    @Override
    public void freeRoot(Map<String,QVal> env, long addr) {
//       System.out.println("Freeing of address called : " + addr );

//       manager.printBlock(addr);
//        manager.printFreeList();
     //  manager.printFreeList();
 //       System.out.println("Freelist addr : " + manager.freeListPointer);
        RawMemory memory = manager.getMemory();
        long headerAddr = addr;
        long leftAddr = addr + RawMemory.BYTES_IN_WORD;
        long rightAddr = addr + 2* RawMemory.BYTES_IN_WORD;
        long header = memory.load(headerAddr);

        long valToStore = -1;
        long dupHeader = 0;
        if(manager.freeListPointer != -1) {
            valToStore = manager.freeListPointer;
            dupHeader = dupHeader | 1;
        } else {
            valToStore = -1;
            dupHeader = dupHeader | 2;
            dupHeader = dupHeader | 1;
        }

        manager.freeListPointer = headerAddr;
        long leftVal = memory.load(leftAddr);
        long rightVal = memory.load(rightAddr);
//
//        System.out.println("header of  freeList : " + dupHeader);
//        System.out.println("left of  freeList : " + valToStore);
//        System.out.println("right of  freeList : " + Long.MIN_VALUE);
        memory.store(leftAddr, valToStore);
        memory.store(rightAddr, Long.MIN_VALUE);
        memory.store(headerAddr, dupHeader);


        if((header & 2) != 2) {
            if(leftVal != -1 && leftVal >= memory.getStartAddr() && leftVal <= memory.getEndAddr()) {
                boolean hasValue = env.containsValue(leftVal) || env.containsValue(new QRef(leftVal));
                if(!hasValue)
                    freeRoot(env, leftVal);
            }
        }

        if((header & 1) != 1) {
            if(rightVal != -1 && rightVal >= memory.getStartAddr() && rightVal <= memory.getEndAddr()) {
                boolean hasValue = env.containsValue(rightVal) || env.containsValue(new QRef(rightVal));
                if(!hasValue)
                    freeRoot(env, rightVal);
            }
        }
//        System.out.println("Freelist after freeing");
//        manager.printFreeList();
    }
}
