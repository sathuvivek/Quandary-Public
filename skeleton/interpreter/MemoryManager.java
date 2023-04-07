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
 //       System.out.println("Free List Pointer : " + freeListPointer);
  //      System.out.println("GC TYPE : " + gcType);

//        printMemory();
    }

    public void setGC() {
        gc = new NoGC();;
        if(gcType.equals("MarkSweep")) {
//            System.out.println("Marksweep gc selected");
            gc = new MarkSweepGC();
        } else if(gcType.equals("Explicit")) {
//            System.out.println("Explicit gc selected");
            gc = new ExplicitGC();
        }
    }

    public void setLeft(long address, QVal value) {
//        System.out.println("set left from  memory called : " + address  + " for val : " + value);
        long header = memory.load(address);
        long valToStore ;
        if(value instanceof QInt) {
//            System.out.println("Is INT");
            header = header | 2;
            QInt intVal = (QInt) value;
            valToStore = intVal.value;
        }  else {
//            System.out.println("Is REF");
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
//        System.out.println("set right from  memory called : " + address  + " for val : " + value);
//        printBlock(address);
        long header = memory.load(address);
        long valToStore ;
        if(value instanceof QInt) {
          //  System.out.println("Is INT");
            header = header | 1;
            QInt intVal = (QInt) value;
            valToStore = intVal.value;
        } else {
            int bitPosition = 0; // set the 1nd bit to 0
            int mask = ~(1 << bitPosition); // create a mask with the 1nd bit set to 0
            header = header & mask;
           // System.out.println("Is REF");
            QRef refVal = (QRef) value;
            valToStore = refVal.getAddress();
        }

        memory.store(address, header);
        address += 2 *  RawMemory.BYTES_IN_WORD;
        memory.store(address, valToStore);
    }

    public QVal getLeft(long address) {
//        System.out.println("Get Left from  memory called : " + address  );
//        printBlock(address);
        long header = memory.load(address);
        long leftVal = memory.load(address + RawMemory.BYTES_IN_WORD);
        if((2 & header) == 2) {
//            System.out.println("Returning a QInt : " + leftVal);
            return new QInt((int)leftVal);
        } else {
//            System.out.println("Returning a QRef : " + leftVal);
            return new QRef(leftVal);
        }
    }

    public QVal getRight(long address) {
//        System.out.println("Get Right from  memory called : " + address  );
//        printBlock(address);

        long header = memory.load(address);
        long rightVal = memory.load(address + 2 * RawMemory.BYTES_IN_WORD);

        if((1 & header) == 1) {
 //           System.out.println("Returning a QInt : " + rightVal);
            return new QInt((int)rightVal);
        } else {
  //          System.out.println("Returning a QRef : " + rightVal);

            return new QRef(rightVal);
        }
    }

//    public long allocateHeap(QVal value1, QVal value2) {
//        int header = 0;
//        if(value1 instanceof QInt) {
//            header += 1;
//        }
//
//        if(value2 instanceof QInt) {
//            header += 2;
//        }
//
//
//        return freeListPointer;
//    }

    public long prepFreeList() {
        long endAddress = memory.getEndAddr();
        long startAddress = memory.getStartAddr();
        long blockStart = startAddress;
        long prevAddress = startAddress;
        long leftAddr = startAddress;
//        System.out.println("Preping free list ");
//        System.out.println("Start :  " + blockStart);
//        System.out.println("End : "  + endAddress) ;
        if(blockStart + 3*RawMemory.BYTES_IN_WORD > endAddress)
            return -1;
        while( (blockStart + 3*RawMemory.BYTES_IN_WORD) <= endAddress) {
            leftAddr = blockStart + RawMemory.BYTES_IN_WORD;
            long header = 0;
            long rightAddr = blockStart + 2 * RawMemory.BYTES_IN_WORD;
//            System.out.println("Allocation block start : " + blockStart);
//            System.out.println("Left address :  " + leftAddr);
            if(blockStart == startAddress) {
                memory.store(leftAddr, -1);
                header = header | 2;
            } else {
                memory.store(leftAddr, prevAddress);

                //memory.store(blockStart, 2);
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
//        System.out.println("Allocate called : FreeList Pointer at : " + freeListPointer);
//        printFreeList();
        if(freeListPointer == -1) {
//            System.out.println("Triggering GC : ");
//            printMemory();
            GCRoots.prepRoots(currentEnv);
            gc.triggerGC();
            GCRoots.unPrepRoots(currentEnv);
//            System.out.println("Free List pointer after GC : " + freeListPointer);

            if(freeListPointer == -1)
                Interpreter.fatalError("Out of memory " , Interpreter.EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR);
//            printBlock(freeListPointer);
        }
        try {
//            System.out.println("free list addr : " + freeListPointer);
            long returnAddress = freeListPointer;
            long header = memory.load(freeListPointer);
//                System.out.println("header :  " + header);
//                System.out.println("left is int :  " + ((header&2) == 2));
//                System.out.println("right is int :  " + ((header&1) == 1));
            freeListPointer = freeListPointer + RawMemory.BYTES_IN_WORD;
            long pointedVal = memory.load(freeListPointer);
            if (pointedVal < memory.getStartAddr())
                freeListPointer = -1;
            else
                freeListPointer = pointedVal;
            this.freeListPointer = freeListPointer;
//            System.out.println("free list aftr allocation : " + freeListPointer);
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
//        System.out.println("Marked Sweep : " + ((header & 4) == 4));
//        System.out.println("Left : " + left);
//        System.out.println("Right : " + right);
//        System.out.println("Left int : " + ((header & 2) == 2));
//        System.out.println("Right int : " + ((header & 1) == 1));
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
//        System.out.println("Marked Sweep : " + ((header & 4) == 4));
//        System.out.println("Left : " + left);
//        System.out.println("Right : " + right);
//        System.out.println("Left int : " + ((header & 2) == 2));
//        System.out.println("Right int : " + ((header & 1) == 1));
        System.out.println("---------------------");
//        if((header & 2)  != 2)
//            printBlock(left);
//        if((header & 1) != 1)
//            printBlock(right);
    }
//    public void printBlock(long address) {
//        long start = address;
//        System.out.println("---------Block---------");
//        System.out.println("Block start Address : " + start);
//        long header = memory.load(start);
//        System.out.println("header : " + header);
//        System.out.println("Marked Sweep : " + ((header & 4) == 4));
//        System.out.println("Left int : " + ((header & 2) == 2));
//        System.out.println("Right int : " + ((header & 1) == 1));
//        start = start + RawMemory.BYTES_IN_WORD;
//        long leftAddr = start;
//        long left = memory.load(leftAddr);
//        System.out.println("Left : " + left);
//        start = start + RawMemory.BYTES_IN_WORD;
//        long rightAddr = start;
//        long right = memory.load(rightAddr);
//        System.out.println("Right : " + right);
//        start = start + RawMemory.BYTES_IN_WORD;
//        System.out.println("---------------------");
////        if((header & 2)  != 2)
////            printBlock(left);
////        if((header & 1) != 1)
////            printBlock(right);
//    }

    public void printMemory() {
        long start = memory.getStartAddr();
        long end = memory.getEndAddr();
        while((start + 3 *RawMemory.BYTES_IN_WORD) <= end) {
//            System.out.println("--------Block--------");
//            System.out.println("Block start Address : " + start);
            long startAddr = start;
            long header = memory.load(start);
//            System.out.println("header : " + header);
//            System.out.println("Marked Sweep : " + ((header & 4) == 4));
//            System.out.println("Left int : " + ((header & 2) == 2));
//            System.out.println("Right int : " + ((header & 1) == 1));
            start = start + RawMemory.BYTES_IN_WORD;
            long left = memory.load(start);
//            System.out.println("Left : " + left);
            start = start + RawMemory.BYTES_IN_WORD;
            long right = memory.load(start);
//            System.out.println("Right : " + right);
            start = start + RawMemory.BYTES_IN_WORD;

            System.out.println("---------Block---------");
            System.out.println("START | SWEEP | LEFTINT | RIGHTINT | LEFT | RIGHT | END");
            System.out.println(startAddr +  " | " + ((header & 4) == 4) + " | " + ((header & 2) == 2) + " | " +((header & 1) == 1) + " | " +left + " | " +right + " | " + (startAddr + 3*RawMemory.BYTES_IN_WORD));

            System.out.println("---------------------");
        }
    }

//    public void printFreeList() {
//        long endAddress = memory.getEndAddr();
//        long startAddress = memory.getStartAddr();
//        long blockStart = startAddress;
//        long prevAddress = startAddress;
//        long leftAddr = startAddress;
//        while(freeListPointer <= endAddress && freeListPointer >= startAddress) {
//
//        }
//    }



//    public long allocate(QVal value) {
//        if(freeListPointer == 0L) {
//            startGC();
//        } else {
//            allocate(value);
//        }
//    }


    public void freeRoot(Map<String, QVal> env, long addr) {
        gc.freeRoot(env, addr);
    }

    public void setMarked(long addr) {
//        System.out.println("Marking addr as visited : " + addr);
 //       printBlock(addr);
        long header = memory.load(addr);
        if((header & 4) == 4) {
//            System.out.println("Addr already visited");
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
//            System.out.println("right value : " + right);
            if( right != -1 && right != Long.MIN_VALUE) {
//                System.out.println("Calling setMarked for right value : " + right);
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
                //memory.store(blockStart, 2);
            }
            header = header | 1;
            memory.store(rightAddr, Long.MIN_VALUE);
            memory.store(blockStart, header);
            freeListPointer = blockStart;
            blockStart += (3 * RawMemory.BYTES_IN_WORD);
        }
//        printBlock(freeListPointer);
    }

    public String joinStr(String left, String right) {
        return "(" + left + " . " + right + ")";
    }

    public String getStr(long addr) {
        String s = "";
//        System.out.println("Get string for address : " + addr);
//        printBlock(addr);
        if(addr >= memory.getStartAddr() && addr <= memory.getEndAddr()) {
            long header = memory.load(addr);
            long left = memory.load(addr + RawMemory.BYTES_IN_WORD);
//            System.out.println("left : " + left);
            long right = memory.load(addr + 2 * RawMemory.BYTES_IN_WORD);
//            System.out.println("right : " + right);
            String leftStr = "";
            String rightStr = "";
            if((header & 2) != 2) {
//                System.out.println("left is not int ");
                if(left != -1 && left >= memory.getStartAddr() && left <= memory.getEndAddr())
                    leftStr = getStr(left);
                else
                    leftStr = "nil";
            } else
                leftStr = Long.toString(left);
            if((header & 1) != 1) {
//                System.out.println("right is not int ");
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
