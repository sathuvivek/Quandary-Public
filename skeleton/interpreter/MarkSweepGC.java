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
        for(Long rootAddr : keyList) {
            manager.setMarked(rootAddr);
        }
    }


    public void freeRootOne( long addr) {
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
        memory.store(leftAddr, valToStore);
        memory.store(rightAddr, Long.MIN_VALUE);
        memory.store(headerAddr, dupHeader);
    }

    public void sweep() {

        RawMemory memory = manager.getMemory();
        long startAddr = memory.getStartAddr();
        long endAddr = memory.getEndAddr();
        long blockStart = startAddr;

        while((blockStart + 3*RawMemory.BYTES_IN_WORD) <= endAddr) {
            long header = memory.load(blockStart);

            if((header & 4) == 4) {

                long bitMask = ~(1 << 2); // create a bit mask with 3rd bit set to 0
                header = header & bitMask; // perform bitwise AND operation with the number and the bit mask
                memory.store(blockStart, header);
            } else
                freeRootOne(blockStart);
            blockStart += 3 * RawMemory.BYTES_IN_WORD;
        }

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
