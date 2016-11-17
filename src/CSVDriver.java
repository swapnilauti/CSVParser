import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.poi.hssf.usermodel.*;


/**
 * Created by SwapnilSudam on 10/20/2016
 */
public class CSVDriver {
    public static void main(String args[]){
        String pathname = args[0];
        String excelfilepath = args[1];
        FileOutputStream fileOut = null;
        int blockSize = 512*1024;
        try {
            fileOut = new FileOutputStream(excelfilepath);
            int totalColumns = 18;
            Random rand = new Random();
            // Code to create excel file

            int xrow = 0, xcol = 0;
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("naive vs sporty");
            HSSFRow row1 = worksheet.createRow(xrow++);
            HSSFCell cellA1 = row1.createCell(xcol++);
            cellA1.setCellValue("time in microseconds");
            cellA1 = row1.createCell(xcol++);
            cellA1.setCellValue("SportyDS Parser");
            cellA1 = row1.createCell(xcol++);
            cellA1.setCellValue("Naive Parser");
            xcol = 0;
            row1 = worksheet.createRow(xrow++);
            cellA1 = row1.createCell(xcol++);
            cellA1.setCellValue("initial load and convert");
            cellA1 = row1.createCell(xcol++);
            cellA1.setCellValue(0);

            CSVTable sportyDS = new CSVTable(pathname, blockSize);

            for (int colToQuery = 0; colToQuery <totalColumns; colToQuery++) {
                xcol = 0;
                System.out.println("GC initiated");
                System.gc();
                System.out.println("GC completed");
                row1 = worksheet.createRow(xrow++);
                cellA1 = row1.createCell(xcol++);
                cellA1.setCellValue("Col " + (colToQuery + 1));
                long k = rand.nextLong() % 56;
                long time[] = new long[1];
                sportyDS.rangeScan(colToQuery, k, k + 100, time);
                cellA1 = row1.createCell(xcol++);
                cellA1.setCellValue(time[0]);
            }

            long wholeParseStart = System.nanoTime();
            CSVTable initLoad = new CSVTable(pathname);
            long wholeParseEnd = System.nanoTime();
            cellA1 = row1.createCell(xcol++);
            cellA1.setCellValue((wholeParseEnd - wholeParseStart) / 1000);

            workbook.write(fileOut);
            fileOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}