import java.io.*;
import java.nio.file.*;
import java.util.logging.Logger;


public class CSVReader {
    private static String fileName;
    private static Logger log = Logger.getLogger("CSVReader");
    private long filePos;
    public CSVReader(String fileName){
        this.fileName=fileName;
        filePos=0;
    }
    /**
     * This method is used to read the CSV File by the blocksize
     * declared. It also sets the filePos to it correct value after read.
     * It also discards the bytes after the read of last \n.
     * @param block byte array to be loaded with the bytes read
     * @return int returns the number of byte read, -1 if eof reached and 0 if no complete line in the block
     */
    public int readCSVBlock(byte[] block){
        int byteRead=0;
        try {
            RandomAccessFile file=new RandomAccessFile(fileName,"r");
            file.seek(filePos);
            byteRead=file.read(block);
            while(byteRead>0 && block[byteRead-1]!=10){
                byteRead--;
                if(byteRead<=0){
                    break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        filePos+=byteRead;
        return byteRead;
    }

    /**int byteRead
     * This method is used to read the CSV File in a single block and
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

}
