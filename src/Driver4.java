import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;

/**
 * This program records observation for Navie Parser
 */
public class Driver4 {
    private static final Logger logger = LogManager.getLogger(Driver4.class.getName());
    public static void main(String args[]){
        File sourceDir = new File(args[0]);
        String destPath = args[1];
        FileOutputStream fileOut = null;
        File inputFiles[] = sourceDir.listFiles();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet worksheet = workbook.createSheet("naive parser");
        int xrow = 0, xcol = 0;
        HSSFRow row1 = worksheet.createRow(xrow++);
        HSSFCell cellA1 = row1.createCell(xcol);
        cellA1.setCellValue("time in microseconds");
        Arrays.sort(inputFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                Long l = o1.length();
                return l.compareTo(o2.length());
            }
        });
        for (File file : inputFiles) {
            row1 = worksheet.createRow(xrow++);
            cellA1 = row1.createCell(0);
            cellA1.setCellValue(file.getName());
        }
        try {
            fileOut = new FileOutputStream(destPath + "NaiveParserObservations");
            for(int i=0;i<1;i++) {
                xcol++;
                xrow = 0;
                row1=worksheet.getRow(xrow++);
                cellA1 = row1.createCell(xcol);
                cellA1.setCellValue("Iteration "+(i+1));
                for (File file : inputFiles) {
                    row1=worksheet.getRow(xrow++);
                    System.gc();
                    Thread.sleep(3000);
                    logger.info("READING STARTED for file {}",file.getName());
                    long wholeParseStart = System.nanoTime();
                    CSVTable initLoad = new CSVTable(file.getAbsolutePath());
                    long wholeParseEnd = System.nanoTime();
                    logger.info("READING COMPLETED for file {}",file.getName());
                    cellA1 = row1.createCell(xcol);
                    cellA1.setCellValue((wholeParseEnd - wholeParseStart) / 1000);
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