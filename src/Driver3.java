import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by SwapnilSudam on 11/26/2016.
 * Total Read Time Vs File Size 
 */
public class Driver3 {
    private static int[] stringToIntArray(String s){
        if(s==null || s.equals("")){
            return new int[0];
        }
        String sarr[] = s.split(",");
        int ret[] = new int[sarr.length];
        int i=0;
        for(String colNo:sarr){
            ret[i++]=Integer.parseInt(colNo);
        }
        return ret;
    }
    public static void main(String args[]) {
        File sourceDir = new File(args[0]);
        String destPath = args[1];
        int dateColumns[] = stringToIntArray(args[2]);
        FileOutputStream fileOut = null;
        int blockSize = 512 * 1024;
        int parserType = 0;                             // 0 -> infile , 1 -> inMem
        int totalColumns = 10;
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        Arrays.sort(inputFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                Long l = o1.length();
                return l.compareTo(o2.length());
            }
        });
        // infile - 510mb
        //inmem - 390mb
        for (parserType = 1; parserType <2; parserType++) {
            Random rand = new Random();
            // Code to create excel file
            int xrow = 0, xcol = 0;
            HSSFSheet worksheet = workbook.createSheet("TotalReadTimeVsFileSize" + (parserType == 0 ? "(InFile)" : "(InMem)"));
            HSSFRow row1 = worksheet.createRow(xrow++);
            HSSFCell cellA1 = row1.createCell(xcol);
            cellA1.setCellValue(parserType == 0 ? "InFile" : "InMem");
            for (File file : inputFiles) {
                row1 = worksheet.createRow(xrow++);
                cellA1 = row1.createCell(0);
                cellA1.setCellValue(file.getName());
            }

            for (int iterationCount = 0; iterationCount < 6; iterationCount++) {
                xrow = 0;
                xcol++;
                row1 = worksheet.getRow(xrow++);
                cellA1 = row1.createCell(xcol);
                cellA1.setCellValue("Iteration " + (iterationCount + 1));
                try {
                    for (File file : inputFiles) {
                        System.out.println(file.getName());
                        CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, dateColumns, parserType);
                        long totalTime = 0l;
                        for (int colToQuery = 0; colToQuery < totalColumns; colToQuery++) {
                            System.gc();
                            Thread.sleep(3000);
                            long k = rand.nextLong() % 56;
                            System.out.println("**************************READINGS STARTED for column "+(colToQuery+1)+"********************************");
                            long time[] = new long[1];
                            sportyDS.rangeScan(colToQuery, k, k + 100, time);
                            totalTime += time[0];
                            System.out.println("**************************READING COMPLETED*******************************");
                        }
                        row1 = worksheet.getRow(xrow++);
                        cellA1 = row1.createCell(xcol);
                        cellA1.setCellValue(totalTime);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        try {
            fileOut = new FileOutputStream(destPath +"TotalReadTimeVsFileSize.csv");
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
