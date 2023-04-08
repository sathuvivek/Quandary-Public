package interpreter;

import java.util.List;
import java.util.Map;

public class MemoryManager {

    public static String gcType = "NoGC";

    public static long heapBytes ;
    long freeListPointer = -1;

    public RawMemory getMemory() {
        return memory;
    }

    RawMemory memory;

    long startAddr;



    GarbageCollector gc;

    private static MemoryManager managerInstance;

    public static MemoryManager getInstance() {
            return managerInstance;
    }

    public static MemoryManager getInstance(long heapBytes, String gcType) {
        MemoryManager.heapBytes = heapBytes;
        MemoryManager.gcType = gcType;
        if(managerInstance == null)
             managerInstance = new  MemoryManager(heapBytes, gcType);
        return managerInstance;
    }

    private MemoryManager(long heapBytes, String gcType) {
        gcType = gcType;
        startAddr = 0;
        memory = new RawMemory(heapBytes, heapBytes);
        freeListPointer = prepFreeList();
    }

    public void setGC() {
        gc = new NoGC();;
        if(gcType.equals("MarkSweep")) {
            gc = new MarkSweepGC();
        } else if(gcType.equals("Explicit")) {
            gc = new ExplicitGC();
        }
    }

    public void setLeft(long address, QVal value) {
        long header = memory.load(address);
        long valToStore ;
        if(value instanceof QInt) {
            header = header | 2;
            QInt intVal = (QInt) value;
            valToStore = intVal.value;
        }  else {
            int bitPosition = 1; // set the 1nd bit to 0
            int mask = ~(1 << bitPosition); // create a mask with the 1nd bit set to 0
            header = header & mask;
            QRef refVal = (QRef) value;
            valToStore = refVal.getAddress();
        }
        memory.store(address, header);
        address += RawMemory.BYTES_IN_WORD;
        memory.store(address, valToStore);
    }

    public void setRight(long address, QVal value) {
        long header = memory.load(address);
        long valToStore ;
        if(value instanceof QInt) {
            header = header | 1;
            QInt intVal = (QInt) value;
            valToStore = intVal.value;
        } else {
            int bitPosition = 0; // set the 1nd bit to 0
            int mask = ~(1 << bitPosition); // create a mask with the 1nd bit set to 0
            header = header & mask;
            QRef refVal = (QRef) value;
            valToStore = refVal.getAddress();
        }

        memory.store(address, header);
        address += 2 *  RawMemory.BYTES_IN_WORD;
        memory.store(address, valToStore);
    }

    public QVal getLeft(long address) {
        long header = memory.load(address);
        long leftVal = memory.load(address + RawMemory.BYTES_IN_WORD);
        if((2 & header) == 2) {
            return new QInt((int)leftVal);
        } else {
            return new QRef(leftVal);
        }
    }

    public QVal getRight(long address) {
        long header = memory.load(address);
        long rightVal = memory.load(address + 2 * RawMemory.BYTES_IN_WORD);

        if((1 & header) == 1) {

            return new QInt((int)rightVal);
        } else {
            return new QRef(rightVal);
        }
    }

    public long prepFreeList() {
        long endAddress = memory.getEndAddr();
        long startAddress = memory.getStartAddr();
        long blockStart = startAddress;
        long prevAddress = startAddress;
        long leftAddr = startAddress;
        if(blockStart + 3*RawMemory.BYTES_IN_WORD > endAddress)
            return -1;
        while( (blockStart + 3*RawMemory.BYTES_IN_WORD) <= endAddress) {
            leftAddr = blockStart + RawMemory.BYTES_IN_WORD;
            long header = 0;
            long rightAddr = blockStart + 2 * RawMemory.BYTES_IN_WORD;
            if(blockStart == startAddress) {
                memory.store(leftAddr, -1);
                header = header | 2;
            } else {
                memory.store(leftAddr, prevAddress);
            }
            header = header | 1;
            memory.store(blockStart, header);
            memory.store(rightAddr, Long.MIN_VALUE);

            prevAddress = blockStart;
            blockStart += (3 * RawMemory.BYTES_IN_WORD);
        }
        blockStart = prevAddress;
        return blockStart;
    }

    public void startGC() {
        gc.triggerGC();
    }

    public void printFreeList() {
        System.out.println("--------_FREELIST--------");
        long start = freeListPointer;
        while(start <= memory.getEndAddr() && start >= memory.getStartAddr()) {

            long header = memory.load(start);

            printBlock(start);
            if((header & 2) != 2) {
                long leftAddr = memory.load(start + RawMemory.BYTES_IN_WORD);
                start = leftAddr;
            } else
                break;
        }
        System.out.println("-------------------------");
    }

    public long allocate(Map<String, QVal> currentEnv) {
        if(freeListPointer == -1) {
            GCRoots.prepRoots(currentEnv);
            gc.triggerGC();
            GCRoots.unPrepRoots(currentEnv);
            if(freeListPointer == -1)
                Interpreter.fatalError("Out of memory " , Interpreter.EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR);
        }
        try {
            long returnAddress = freeListPointer;
            long header = memory.load(freeListPointer);
            freeListPointer = freeListPointer + RawMemory.BYTES_IN_WORD;
            long pointedVal = memory.load(freeListPointer);
            if (pointedVal < memory.getStartAddr())
                freeListPointer = -1;
            else
                freeListPointer = pointedVal;
            this.freeListPointer = freeListPointer;
            return returnAddress;
        } catch (Exception e) {
            String a = "ERROR : for freeListPointer : " + freeListPointer + " " + e ;
            System.out.println(a );
        }
        return freeListPointer;

    }

    public void printFullBlock(long address) {
        long start = address;

        long header = memory.load(start);

        start = start + RawMemory.BYTES_IN_WORD;
        long leftAddr = start;
        long left = memory.load(leftAddr);

        start = start + RawMemory.BYTES_IN_WORD;
        long rightAddr = start;
        long right = memory.load(rightAddr);

        start = start + RawMemory.BYTES_IN_WORD;
        System.out.println("---------Block---------");
        System.out.println("START | SWEEP | LEFTINT | RIGHTINT | LEFT | RIGHT | END");
        System.out.println(address + " | " + ((header & 4) == 4) + " | " + ((header & 2) == 2) + " | " + ((header & 1) == 1) + " | " + left + " | " + right + " | " + (address + 3 * RawMemory.BYTES_IN_WORD));
        System.out.println("---------------------");
        if ((header & 2) != 2) {
            if(left != -1 && left <= memory.getEndAddr() && left >= memory.getStartAddr())
                printBlock(left);
        }
        if((header & 1) != 1) {

            if(right != -1 && right <= memory.getEndAddr() && right >= memory.getStartAddr())
                printBlock(right);
        }
    }
    public void printBlock(long address) {
        long start = address;

        long header = memory.load(start);

        start = start + RawMemory.BYTES_IN_WORD;
        long leftAddr = start;
        long left = memory.load(leftAddr);

        start = start + RawMemory.BYTES_IN_WORD;
        long rightAddr = start;
        long right = memory.load(rightAddr);

        start = start + RawMemory.BYTES_IN_WORD;
        System.out.println("---------Block---------");
        System.out.println("START | SWEEP | LEFTINT | RIGHTINT | LEFT | RIGHT | END");
        System.out.println(address +  " | " + ((header & 4) == 4) + " | " + ((header & 2) == 2) + " | " +((header & 1) == 1) + " | " +left + " | " +right + " | " + (address + 3*RawMemory.BYTES_IN_WORD));
        System.out.println("---------------------");
    }

    public void printMemory() {
        long start = memory.getStartAddr();
        long end = memory.getEndAddr();
        while((start + 3 *RawMemory.BYTES_IN_WORD) <= end) {
            long startAddr = start;
            long header = memory.load(start);
            start = start + RawMemory.BYTES_IN_WORD;
            long left = memory.load(start);
            start = start + RawMemory.BYTES_IN_WORD;
            long right = memory.load(start);
            start = start + RawMemory.BYTES_IN_WORD;

            System.out.println("---------Block---------");
            System.out.println("START | SWEEP | LEFTINT | RIGHTINT | LEFT | RIGHT | END");
            System.out.println(startAddr +  " | " + ((header & 4) == 4) + " | " + ((header & 2) == 2) + " | " +((header & 1) == 1) + " | " +left + " | " +right + " | " + (startAddr + 3*RawMemory.BYTES_IN_WORD));

            System.out.println("---------------------");
        }
    }


    public void freeRoot(Map<String, QVal> env, long addr) {
        gc.freeRoot(env, addr);
    }

    public void setMarked(long addr) {
        long header = memory.load(addr);
        if((header & 4) == 4) {
            return;
        }
        header = header | 4;
        memory.store(addr, header);
        if((header & 2) != 2) {
            long left = memory.load(addr + RawMemory.BYTES_IN_WORD);
            if(left != -1 && left != Long.MIN_VALUE)
                setMarked(left);
        }

        if((header & 1) != 1) {
            long right = memory.load(addr + 2* RawMemory.BYTES_IN_WORD);
            if( right != -1 && right != Long.MIN_VALUE) {
                setMarked(right);
            }


        }
    }

    public void rebuildFreeList() {
        long startAddr = memory.getStartAddr();
        long endAddr = memory.getEndAddr();
        long blockStart = startAddr;
        while(blockStart + 3*RawMemory.BYTES_IN_WORD <= endAddr) {
            long header = memory.load(blockStart);
            if ((header & 4) == 4) {
                blockStart += (3 * RawMemory.BYTES_IN_WORD);
                continue;
            }
            header = 0;
            long leftAddr = blockStart + RawMemory.BYTES_IN_WORD;
            long rightAddr = blockStart + 2 * RawMemory.BYTES_IN_WORD;

            if(blockStart == startAddr) {
                if (freeListPointer == -1) {
                    memory.store(leftAddr, -1);
                    header = header | 2;
                } else
                    memory.store(leftAddr, freeListPointer);
            } else {
                memory.store(leftAddr, freeListPointer);
            }
            header = header | 1;
            memory.store(rightAddr, Long.MIN_VALUE);
            memory.store(blockStart, header);
            freeListPointer = blockStart;
            blockStart += (3 * RawMemory.BYTES_IN_WORD);
        }
    }

    public String joinStr(String left, String right) {
        return "(" + left + " . " + right + ")";
    }

    public String getStr(long addr) {
        String s = "";
        if(addr >= memory.getStartAddr() && addr <= memory.getEndAddr()) {
            long header = memory.load(addr);
            long left = memory.load(addr + RawMemory.BYTES_IN_WORD);
            long right = memory.load(addr + 2 * RawMemory.BYTES_IN_WORD);
            String leftStr = "";
            String rightStr = "";
            if((header & 2) != 2) {
                if(left != -1 && left >= memory.getStartAddr() && left <= memory.getEndAddr())
                    leftStr = getStr(left);
                else
                    leftStr = "nil";
            } else
                leftStr = Long.toString(left);
            if((header & 1) != 1) {
                if(right != -1 && right >= memory.getStartAddr() && right <= memory.getEndAddr())
                    rightStr = getStr(right);
                else
                    rightStr = "nil";
            } else
                rightStr = Long.toString(right);
            s = joinStr(leftStr, rightStr);
        } else
            s = "Start Memory is not in array : " + addr;
        return s;
    }

    public void removeTempRoots(List<Long> tempRoots) {
        //GCRoots.removeRoots(tempRoots);
    }
}
