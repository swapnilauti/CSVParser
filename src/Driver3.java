import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Total Read Time Vs File Size for InFile and InMem parsers
 */
public class Driver3 {
    private static final Logger logger = LogManager.getLogger(Driver3.class.getName());
     public static void main(String args[]) {
        File sourceDir = new File(args[0]);
        String destPath = args[1];
        int dateColumns[] = CSVUtil.stringToIntArray(args[2]);
        int blockSize = 512 * 1024;
        int totalColumns = 10;
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        Random rand = new Random();
         Arrays.sort(inputFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                Long l = o1.length();
                return l.compareTo(o2.length());
            }
        });
         //Check for InFile and InMem parsers
        for (int parserType = 0; parserType <2; parserType++) {
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
                        logger.info("Readings for file {} Started",file.getName());
                        CSVTable sportyDS = new CSVTable(file.getAbsolutePath(), blockSize, dateColumns, parserType);
                        long totalTime = 0l;
                        for (int colToQuery = 0; colToQuery < totalColumns; colToQuery++) {
                            System.gc();
                            Thread.sleep(3000);
                            long k = rand.nextLong() % 56;
                            logger.info("READINGS STARTED for column {}",(colToQuery+1));
                            long time[] = new long[1];
                            sportyDS.rangeScan(colToQuery, k, k + 100, time);
                            totalTime += time[0];
                            logger.info("READING COMPLETED for column {}",(colToQuery+1));
                        }
                        row1 = worksheet.getRow(xrow++);
                        cellA1 = row1.createCell(xcol);
                        cellA1.setCellValue(totalTime);
                    }
                }catch(Exception e){
                    logger.error(e.getMessage());
                }
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(destPath +"TotalReadTimeVsFileSize.csv");
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
