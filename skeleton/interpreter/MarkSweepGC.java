package interpreter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkSweepGC implements GarbageCollector{

    MemoryManager manager;

    public MarkSweepGC() {
        manager = MemoryManager.getInstance();
    }

    public void markUnused() {
        List<Long> keyList = GCRoots.getRoots();
        Collections.sort(keyList);
//        System.out.println("Marking Roots as used : " + keyList.toString());
        for(Long rootAddr : keyList) {
            manager.setMarked(rootAddr);
        }
//        System.out.println("Mark Unused completed");
    }


    public void freeRootOne( long addr) {
//        System.out.println("Freeing of address called : " + addr );
//        System.out.println("Freelist addr : " + manager.freeListPointer);
//        RawMemory memory = manager.getMemory();
//        long headerAddr = addr;
//        long leftAddr = addr + RawMemory.BYTES_IN_WORD;
//        long rightAddr = addr + 2 * RawMemory.BYTES_IN_WORD;
//        long header = memory.load(headerAddr);
//        header = 0;
//        long valToStore = -1;
//        if (manager.freeListPointer != -1) {
//            valToStore = manager.freeListPointer;
//        } else
//            header = header | 2;
//        manager.freeListPointer = headerAddr;
//        long leftVal = memory.load(leftAddr);
//        long rightVal = memory.load(rightAddr);
//
//        header = header | 1;
//        memory.store(leftAddr, valToStore);
//        memory.store(headerAddr, header);
//        System.out.println("Freeing addr : " + addr);
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
    }

    public void sweep() {
//        System.out.println("Starting sweep");
        RawMemory memory = manager.getMemory();
        long startAddr = memory.getStartAddr();
        long endAddr = memory.getEndAddr();
        long blockStart = startAddr;
//        System.out.println("Start : " + startAddr + " | endAddr : " + endAddr);
        while((blockStart + 3*RawMemory.BYTES_IN_WORD) <= endAddr) {
            long header = memory.load(blockStart);
//            manager.printBlock(blockStart);
            if((header & 4) == 4) {
//                System.out.println("Memory : " + blockStart + " | is already visited so unvisiting it for next gc");
                long bitMask = ~(1 << 2); // create a bit mask with 3rd bit set to 0
                header = header & bitMask; // perform bitwise AND operation with the number and the bit mask
                memory.store(blockStart, header);
            } else
                freeRootOne(blockStart);
            blockStart += 3 * RawMemory.BYTES_IN_WORD;
        }
//        System.out.println("sweep complete");
       // manager.rebuildFreeList();
    }
    @Override
    public void triggerGC() {
        markUnused();
        sweep();
    }



    @Override
    public void freeRoot(Map<String, QVal> env, long addr) {

    }

}
