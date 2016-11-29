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

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
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
        int blockSize = 512*1024;
        int parserType = 0;
        File inputFiles[] = sourceDir.listFiles();
        try {
            for(File file:inputFiles) {
                fileOut = new FileOutputStream(destPath+file.getName());
                int totalColumns = 18;
                int totalRows = 99;
                int rowInterval=10;
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

                CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, dateColumns, parserType);
                long time1=System.nanoTime(),time2;
                for (int row = rowInterval; row <=totalRows; row=row+rowInterval) {
                    sportyDS.lookUp(5,row);
                        xcol = 0;
                        row1 = worksheet.createRow(xrow++);
                        cellA1 = row1.createCell(xcol++);
                        cellA1.setCellValue("Row " + (row));
                        time2 = System.nanoTime();
                        cellA1 = row1.createCell(xcol++);
                        cellA1.setCellValue((time2-time1)/1000000);
                        time1=time2;
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
