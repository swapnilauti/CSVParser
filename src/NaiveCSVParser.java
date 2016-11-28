import java.util.ArrayList;
import java.util.logging.Logger;


public class NaiveCSVParser {
    private static String fileName;
    private static Logger log = Logger.getLogger("NaiveCSVReader");
    private CSVReader fileReader;
    private int blockSize;
    private boolean isPositionalMapFormed;
    private ArrayList<ArrayList<Long>> positionalMap;
    private int columnSize = 1;

    /**Constructor
     * @param blockSize blocksize for the file read
     * @param fileName filename with path
     */
    public NaiveCSVParser(String fileName, int blockSize) {
        this.fileName = fileName;
        positionalMap = new ArrayList<>();
        fileReader = new CSVReader(fileName);
        isPositionalMapFormed = false;
        this.blockSize = blockSize;
    }

    /**This method returns the total columns in the CSV File
     * @return int colunmSize value
     */
    public int getColumnSize(byte []block) {
        int size = Array.getlength(block)
        int columnSize =1;
        int i =0;
        while(block[i] != 92 && block[i+1] != 110){
            i++;
            columnSize = columnSize + 1;
        }
        return columnSize;
    }


    public ArrayList<Long> creatColumn(byte []block) {
        int size = Array.getlength(block);
        int i =0;
        while(block[i] != 92 && block[i+1] != 110){
            ArrayList<Long> columnList1 = new ArrayList<Long>();
            ArrayList<Long> columnList2 = new ArrayList<Long>();
            ArrayList<Long> columnList3 = new ArrayList<Long>();
            ArrayList<Long> columnList4 = new ArrayList<Long>();
            ArrayList<Long> columnList5 = new ArrayList<Long>();
            ArrayList<Long> columnList6 = new ArrayList<Long>();
            ArrayList<Long> columnList7 = new ArrayList<Long>();
            ArrayList<Long> columnList8 = new ArrayList<Long>();
            ArrayList<Long> columnList9 = new ArrayList<Long>();
            ArrayList<Long> columnList10 = new ArrayList<Long>();
            ArrayList<Long> columnList11 = new ArrayList<Long>();
            ArrayList<Long> columnList12 = new ArrayList<Long>();
            ArrayList<Long> columnList13 = new ArrayList<Long>();
            ArrayList<Long> columnList14 = new ArrayList<Long>();
            ArrayList<Long> columnList15 = new ArrayList<Long>();
            ArrayList<Long> columnList16 = new ArrayList<Long>();
            ArrayList<Long> columnList17 = new ArrayList<Long>();
            ArrayList<Long> columnList18 = new ArrayList<Long>();
            }

        for(int i=0;i<size;++i) {
            while(block[i] != 92 && block[i+1] != 110){
                int j=0;
                columnList1[j] = block[i];
                columnList2[j] = block[i];
                columnList3[j] = block[i];
                columnList4[j] = block[i];
                columnList5[j] = block[i];
                columnList6[j] = block[i];
                columnList7[j] = block[i];
                columnList8[j] = block[i];
                columnList9[j] = block[i];
                columnList10[j] = block[i];
                columnList11[j] = block[i];
                columnList12[j] = block[i];
                columnList13[j] = block[i];
                columnList14[j] = block[i];
                columnList15[j] = block[i];
                columnList16[j] = block[i];
                columnList17[j] = block[i];
                columnList18[j] = block[i];
                j++
            }

        }

    }
}