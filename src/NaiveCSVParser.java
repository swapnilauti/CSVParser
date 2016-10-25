import java.util.ArrayList;
import java.util.logging.Logger;


public class NaiveCSVParser {
    private static String fileName;
    private static Logger log = Logger.getLogger("NaiveCSVReader");
    private CSVReader fileReader;
    private int blockSize;
    private boolean isPositionalMapFormed;
    private ArrayList<ArrayList<Long>> positionalMap;
    private int columnSize;

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
    public int getColumnSize() {
        return columnSize;
    }


    public ArrayList<Long> creatColumn(byte []block) {

        for(int i=0;i<block.size();++i) {

        // use comma as separator
        String[] country = line.split(cvsSplitBy);

       

    }


        if (!isPositionalMapFormed) {
            return (createPositionalMapFetchCol(column));
        }
        ArrayList<Long> returnList = new ArrayList<>();