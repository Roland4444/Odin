package se.roland.xlsx;

import junit.framework.TestCase;

import java.io.IOException;

public class XLSXTest extends TestCase {

    public void testWriteIntoExcel() throws IOException {
        String filename = "out.xlsx";
        XLSX.writeIntoExcel(filename);
    }

    public void testWriteToXLSX() throws IOException {
        String filename = "out3.xlsx";
        XLSX.writeToXLSX(50000,4, "GGGG", filename);
    }
}