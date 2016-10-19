import java.util.ArrayList;
import java.util.logging.Logger;


public class CSVParser {
    private static String fileName;
    private static Logger log = Logger.getLogger("CSVReader");
    private CSVReader fileReader;
    private int blockSize;

    public CSVParser(String fileName, int blockSize) {
        this.fileName = fileName;
        positionalMap = new ArrayList<ArrayList<Integer>>();
        fileReader = new CSVReader(fileName);
        isPositionalMapFormed = false;
        this.blockSize = blockSize;
    }

    private ArrayList<ArrayList<Integer>> positionalMap;

    private boolean isPositionalMapFormed;

    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching the column from positional map
     * if it exists
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    public ArrayList<Long> fetchColumn(int column) {

    }

    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching the column from positional map
     * if it exists
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    public long fetchValue(int column, int rowId) {

    }
    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching the column between the given rowids
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value in between the rowIdMin and rowIdMax
     */
    public ArrayList<Long> fetchColumnByRowId(int column, int rowIdMin, int rowIdMax) {

    }

    /**
     * This is private method being worked upon (Sarang)
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of
     */
    private ArrayList<StringBuilder> createPositionalMapFetchCol(int column) {
        int rowCount = 0, colCount = 0, bytesRead = 0;
        byte[] block = new byte[blockSize];
        ArrayList<StringBuilder> returnList = new ArrayList<StringBuilder>();

        while ((bytesRead = fileReader.readCSVBlock(block)) >= 0) {
            ArrayList<Integer> row = new ArrayList<Integer>();
            StringBuilder val = new StringBuilder();
            for (int i = 0; i < bytesRead; ++i) {
                if (block[i] == 44) {
                    row.add(i - 1);
                    colCount++;
                } else if (colCount == column) {
                    val.append((char) block[i]);
                } else if (block[i] == 10) {
                    row.add(i - 1);
                    returnList.add(val);
                    colCount = 0;
                    rowCount++;
                    positionalMap.add(row);
                    row = new ArrayList<Integer>();
                    val = new StringBuilder();
                }
            }
        }
        return returnList;
    }

}
