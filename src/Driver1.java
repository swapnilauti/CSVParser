import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * This Driver is for InFile Parser with varying block Sizes
 */
public class Driver1 {
    private static final Logger logger = LogManager.getLogger(Driver1.class.getName());

    public static void main(String args[]){
        File sourceDir = new File(args[0]);
        String destPath = args[1];
        int dateColumns[] = CSVUtil.stringToIntArray(args[2]);
        FileOutputStream fileOut = null;
        int sizeFactor[] = new int[]{8,16,256,512,768,1024, 16*1024, 32*1024, 96*1024};
        int parserType = CSVUtil.IN_FILE_PARSER;
        int totalColumns = 10;
        Random rand = new Random();
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        try {
            for (File file : inputFiles) {
                fileOut = new FileOutputStream(destPath +file.getName());
                for(int sizeCount = 0; sizeCount<sizeFactor.length;sizeCount++) {
                    int xrow = 0, xcol = 0;
                    int blockSize = sizeFactor[sizeCount]*1024;
                    HSSFSheet worksheet = workbook.createSheet("Block "+Integer.toString(blockSize));
                    HSSFRow row1 = worksheet.createRow(xrow++);
                    HSSFCell cellA1 = row1.createCell(xcol++);
                    cellA1.setCellValue(parserType==0?"InFile":"InMem");
                    for(int colToQuery = 0;colToQuery<totalColumns;colToQuery++){
                        row1 = worksheet.createRow(xrow++);
                        cellA1 = row1.createCell(0);
                        cellA1.setCellValue("Col " + (colToQuery + 1));
                    }
                    xcol = 0;
                    for (int iterationCount = 0; iterationCount < 6; iterationCount++) {
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
                            logger.info("READINGS STARTED for column {}",(colToQuery+1));
                            sportyDS.rangeScan(colToQuery, k, k + 100, time);
                            logger.info("READING COMPLETED for column {}",(colToQuery+1));
                            cellA1 = row1.createCell(xcol);
                            cellA1.setCellValue(time[0]);
                            Thread.sleep(3000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }  finally {
            try {
                workbook.write(fileOut);
                fileOut.flush();
                fileOut.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
