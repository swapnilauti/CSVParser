import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * This program records observations for InMem Parser
 */
public class Driver2 {
    private static int[] stringToIntArray(String s){
        String sarr[] = s.split(",");
        int ret[] = new int[sarr.length];
        int i=0;
        for(String colNo:sarr){
            ret[i++]=Integer.parseInt(colNo);
        }
        return ret;
    }
    public static void main(String args[]){
        File sourceDir = new File(args[0]);
        String destPath = args[1];
        int dateColumns[] = stringToIntArray(args[2]);
        FileOutputStream fileOut = null;
        int blockSize = 512;
        int parserType = 1;                             // 0 -> infile , 1 -> inMem
        File inputFiles[] = sourceDir.listFiles();
        try {
            for (File file : inputFiles) {
                fileOut = new FileOutputStream(destPath + file.getName());
                int totalColumns = 10;
                Random rand = new Random();
                // Code to create excel file

                HSSFWorkbook workbook = new HSSFWorkbook();
                int xrow = 0, xcol = 0;
                HSSFSheet worksheet = workbook.createSheet(Integer.toString(blockSize));
                HSSFRow row1 = worksheet.createRow(xrow++);
                HSSFCell cellA1 = row1.createCell(xcol++);
                cellA1.setCellValue(parserType==0?"InFile":"InMem");
                for(int colToQuery = 0;colToQuery<totalColumns;colToQuery++){
                    row1 = worksheet.createRow(xrow++);
                    cellA1 = row1.createCell(0);
                    cellA1.setCellValue("Col " + (colToQuery + 1));
                }
                xcol = 0;
                for (int iterationCount = 0; iterationCount < 12; iterationCount++) {
                    CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, dateColumns, parserType);
                    xrow = 0;
                    xcol++;
                    row1 = worksheet.getRow(xrow++);
                    cellA1 = row1.createCell(xcol);
                    cellA1.setCellValue("Iteration "+(iterationCount+1));
                    for (int colToQuery = 0; colToQuery < totalColumns; colToQuery++) {
                        System.gc();
                        row1 = worksheet.getRow(xrow++);
                        long k = rand.nextLong() % 56;
                        long time[] = new long[1];
                        sportyDS.rangeScan(colToQuery, k, k + 100, time);
                        cellA1 = row1.createCell(xcol);
                        cellA1.setCellValue(time[0]);
                    }
                    Thread.sleep(3000);
                }

                workbook.write(fileOut);
                fileOut.flush();
            }

        } catch (Exception e) {
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
