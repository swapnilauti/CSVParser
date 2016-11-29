import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.poi.hssf.usermodel.*;


public class CSVDriver {
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
        int parserType = 0;                             // 0 -> infile , 1 -> inMem
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            for(int i=0;i<10;i++) {
                for (File file : inputFiles) {
                    fileOut = new FileOutputStream(destPath + file.getName());
                    int totalColumns = 18;
                    Random rand = new Random();
                    // Code to create excel file

                    int xrow = 0, xcol = 0;
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
                /*
                CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, dateColumns, parserType );

                for (int colToQuery = 0; colToQuery < totalColumns; colToQuery++) {
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
                }*/

                    long wholeParseStart = System.nanoTime();
                    CSVInitLoad initLoad = new CSVInitLoad(file.getAbsolutePath());
                    long wholeParseEnd = System.nanoTime();
                    cellA1 = row1.createCell(xcol++);
                    cellA1.setCellValue((wholeParseEnd - wholeParseStart) / 1000);

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