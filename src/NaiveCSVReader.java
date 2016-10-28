import java.io.*;
import java.nio.file.*;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class NaiveCSVReader {
    private static String fileName;
    private static Logger log = Logger.getLogger("NaiveCSVReader");
    private long filePos;
    public NaiveCSVReader(String fileName){
        this.fileName=fileName;
        filePos=0;
    }

    /** This method is used to read the CSV File in a single block and
     * return the whole block. Should be used with small files.
     * @return int returns the block read as byte array
     */
    public byte[] readCSVAllLines(){
        byte []block={};
        try {
             block = Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return block;
    }

    public void resetFilePos(){
        filePos=0;
    }

}