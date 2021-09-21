package se.roland.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class GLOBAL {


    public static String LOGFILE = "LOG_SPARK.txt";

    public static void LOG(String msg) throws IOException {
        LocalDateTime ls = LocalDateTime.now();
        String data = ls.toString()+"::"+msg;
        FileOutputStream fos = new FileOutputStream(LOGFILE, true);
        fos.write((data+"\r\n").getBytes());
        fos.close();

        ///LTHR.LOG__(msg);
    }

    public static void LOG(String msg, String filename) throws IOException {
        LocalDateTime ls = LocalDateTime.now();
        String data = ls.toString()+"::"+msg;
        FileOutputStream fos = new FileOutputStream(filename, true);
        fos.write((data+"\r\n").getBytes());
        fos.close();

        ///LTHR.LOG__(msg);
    }



}
