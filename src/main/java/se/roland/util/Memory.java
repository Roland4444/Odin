package se.roland.util;


import com.sun.jna.Library;
import com.sun.jna.Native;

public class Memory {
    public interface CLibrary extends Library {
        public static final Memory.CLibrary INSTANCE = (Memory.CLibrary) Native.loadLibrary("sch", Memory.CLibrary.class);
        long getfreeMem ();
    }

    public long getfreeMem(){
        return CLibrary.INSTANCE.getfreeMem();
    }



    public static void main(String[] args) throws InterruptedException {
        var t = new Memory();
        while (true){
            Thread.sleep(5000);
            System.out.println("FREE MEM:"+t.getfreeMem()/1024);

        }

    }
}