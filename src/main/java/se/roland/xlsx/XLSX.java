package se.roland.xlsx;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class XLSX {
    public static void writeIntoExcel(String file) throws IOException, IOException, FileNotFoundException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Birthdays");
        // Нумерация начинается с нуля
        Row row = sheet.createRow(0);
        Row row2 = sheet.createRow(1700);

        // Мы запишем имя и дату в два столбца
        // имя будет String, а дата рождения --- Date,
        // формата dd.mm.yyyy
        sheet.autoSizeColumn(1);
        int firstRow = 0;
        int lastRow = 255;
        int firstCol = 0;
        int lastCol = 255;
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));

        Cell name = row.createCell(0);
        name.setCellValue("John");

        Cell birthdate = row.createCell(1);
        Cell cell2 = row2.createCell(1);
        cell2.setCellValue("Nigga");
        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));
        birthdate.setCellStyle(dateStyle);
        // Нумерация лет начинается с 1900-го
        birthdate.setCellValue(new Date(110, 10, 10));

        // Меняем размер столбца

        // Записываем всё в файл
        book.write(new FileOutputStream(file));
        book.close();
    }

    public static void writeToXLSX(int numberrow, int numbercolumns,  String templte, String FileName) throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("TEST");
        Row[] Arr = new Row[numberrow];
        Cell[] CellArr = new Cell[numbercolumns*numberrow];
        int counter=0;
        for (int i=0; i<numberrow; i++) {
            Arr[i] = sheet.createRow(i);
            for (int j=0; j<numbercolumns; j++) {
                Cell cell = Arr[i].createCell(j);
                CellArr[counter] =cell;
                counter++;
                //cell.setCellValue(templte);
            }
        }

        for (int i=0; i<counter; i++) {
            for (int j=0; j<numbercolumns; j++) {
                CellArr[i].setCellValue(templte);
            }
        }


        book.write(new FileOutputStream(FileName));
        book.close();
    }
}
