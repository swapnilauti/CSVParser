import java.util.ArrayList;
import java.util.logging.Logger;


public class CSVInFileParser implements CSVParser{
    private static String fileName;
    private static Logger log = Logger.getLogger("CSVReader");
    private CSVReader fileReader;
    private int blockSize;
    private boolean isPositionalMapFormed;
    private ArrayList<ArrayList<Long>> positionalMap;
    private int columnSize;

    /**Constructor
     * @param blockSize blocksize for the file read
     * @param fileName filename with path
     */
    public CSVInFileParser(String fileName, int blockSize) {
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


    /**This method fetches the column by creating positional map if it
     * doesn't exist. if the positional map exists then it fetches the column
     * from positional map.
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    public ArrayList<Long> fetchColumn(int column) {
        if (!isPositionalMapFormed) {
            return (createPositionalMapFetchCol(column));
        }
        ArrayList<Long> returnList = new ArrayList<>();
        int bytesRead=0;
        long bytesTillLastBlock=0;
        byte[] block=new byte[blockSize];
        for(int i=0;i<positionalMap.size();++i){
            StringBuilder val = new StringBuilder();
            long startPos=0;
            long endPos=0;
            if(column==0){
                startPos=(i==0?0:positionalMap.get(i - 1).get(getColumnSize() - 1) + 2);
                endPos = positionalMap.get(i).get(column);
            }
            else {
                startPos = positionalMap.get(i).get(column - 1) + 2;
                endPos = positionalMap.get(i).get(column);
            }
            if(startPos>=bytesTillLastBlock+(long)bytesRead){
                bytesTillLastBlock+=bytesRead;
                bytesRead=fileReader.readCSVBlock(block);
            }
            for(int j=(int)(startPos-bytesTillLastBlock);j<=(int)(endPos-bytesTillLastBlock);++j){
                val.append((char)block[j]);
            }
            returnList.add(Long.parseLong(val.toString()));
        }
        fileReader.resetFilePos();
        return returnList;
    }

    /**
     * This is private method is used for creating full positional map and simultaneously
     * load the desired column as well. It is being called internally from fetchColumn.
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    private ArrayList<Long> createPositionalMapFetchCol(int column) {
        int colCount=0,bytesRead;
        long position=0;
        byte[] block=new byte[blockSize];
        ArrayList<Long> returnList = new ArrayList<>();
        while((bytesRead=fileReader.readCSVBlock(block)) > 0){
            ArrayList<Long> row = new ArrayList<>();
            StringBuilder val = new StringBuilder();
            byte newLine = (byte)10,comma = (byte)44;
            for(int i=0;i<bytesRead;++i){
                if(block[i]==comma){
                    row.add(position-1);
                    colCount++;
                }
                else if(block[i]==newLine){
                    row.add(position-1);
                    returnList.add(Long.parseLong(val.toString()));
                    colCount=0;
                    positionalMap.add(row);
                    row = new ArrayList<>();
                    val = new StringBuilder();
                }
                else if(colCount==column){
                    val.append((char)block[i]);
                }
                position++;
            }
        }
        columnSize=positionalMap.get(0).size();
        isPositionalMapFormed=true;
        return returnList;
    }

    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching the column from positional map
     * if it exists
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    public long fetchValue(int column, int rowId) {
        return 0;
    }
    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching the column between the given rowids
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value in between the rowIdMin and rowIdMax
     */
    public ArrayList<Long> fetchColumnByRowId(int column, int rowIdMin, int rowIdMax) {
        return null;
    }

}
