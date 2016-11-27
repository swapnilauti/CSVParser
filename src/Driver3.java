import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by SwapnilSudam on 11/26/2016.
 * Total Read Time Vs File Size 
 */
public class Driver3 {
    public static void main(String args[]){
        File sourceDir = new File(args[0]);
        String destPath = args[1];
        FileOutputStream fileOut = null;
        int blockSize = 512*1024;
        int parserType = 0;                             // 0 -> infile , 1 -> inMem
        int totalColumns = 18;
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            fileOut = new FileOutputStream(destPath + "\\TotalReadTimeVsFileSize.csv");

            Random rand = new Random();
            // Code to create excel file

            int xrow = 0, xcol = 0;
            HSSFSheet worksheet = workbook.createSheet("TotalReadTimeVsFileSize");
            HSSFRow row1 = worksheet.createRow(xrow++);
            HSSFCell cellA1 = row1.createCell(xcol);
            cellA1.setCellValue(parserType==0?"InFile":"InMem");
            for (File file : inputFiles) {
                row1 = worksheet.createRow(xrow++);
                cellA1 = row1.createCell(0);
                cellA1.setCellValue(file.getName());
            }
            for (int iterationCount = 0; iterationCount < 10; iterationCount++) {
                xrow = 0;
                xcol++;
                row1 = worksheet.getRow(xrow++);
                cellA1 = row1.createCell(xcol);
                cellA1.setCellValue("Iteration " + (iterationCount + 1));
                for (File file : inputFiles) {
                    CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, parserType);
                    long totalTime = 0l;
                    for (int colToQuery = 0; colToQuery < totalColumns; colToQuery++) {
                        System.gc();
                        long k = rand.nextLong() % 56;
                        long time[] = new long[1];
                        sportyDS.rangeScan(colToQuery, k, k + 100, time);
                        totalTime+=time[0];
                    }
                    row1 = worksheet.getRow(xrow++);
                    cellA1 = row1.createCell(xcol);
                    cellA1.setCellValue(totalTime);
                    Thread.sleep(3000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            try {
                workbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
