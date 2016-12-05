import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;


/**
 * Created by saran on 11/17/2016.
 */
public class PartialDriver {
    public static void printStats(){
        int mb = 1024*1024;
        Runtime runtime = Runtime.getRuntime();
        System.out.println("##### Heap utilization statistics [MB] #####");
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
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
        int parserType = 0;
        int blockSize=512*1024;
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            for (File file : inputFiles) {
                fileOut = new FileOutputStream(destPath +file.getName());
                int totalRows = 1840000;
                int incrementRow=184000;
                Random rand = new Random();
                // Code to create excel file

                int xrow = 0, xcol = 0;
                HSSFSheet worksheet = workbook.createSheet("Column Read");
                HSSFRow row1 = worksheet.createRow(xrow++);
                HSSFCell cellA1 = row1.createCell(xcol++);
                cellA1.setCellValue("InFile");
                for(int rowToQuery = incrementRow;rowToQuery<=totalRows;rowToQuery=rowToQuery+incrementRow){
                    row1 = worksheet.createRow(xrow++);
                    cellA1 = row1.createCell(0);
                    cellA1.setCellValue("Rows " + (rowToQuery));
                }
                xcol = 0;
                for (int iterationCount = 0; iterationCount < 12; iterationCount++) {
                    CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, dateColumns, parserType);
                    xrow = 0;
                    xcol++;
                    row1 = worksheet.getRow(xrow++);
                    cellA1 = row1.createCell(xcol);
                    cellA1.setCellValue("Iteration "+(iterationCount+1));
                    long time1=System.nanoTime(),time2;
                    for (int rowToQuery = incrementRow;rowToQuery<=totalRows;rowToQuery=rowToQuery+incrementRow) {
                        System.out.println("**************Reading Started***************");
                        row1 = worksheet.getRow(xrow++);
                        sportyDS.lookUp(5,rowToQuery);
                        time2 = System.nanoTime();
                        cellA1 = row1.createCell(xcol);
                        cellA1.setCellValue((time2-time1)/1000000);
                        time1=time2;
                        System.out.println("##################Reading Ended######################");
                    }
                    System.gc();
                    Thread.sleep(6000);
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
