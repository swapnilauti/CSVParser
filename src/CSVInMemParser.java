
import java.util.ArrayList;
import java.util.logging.Logger;


public class CSVInMemParser {

    private static String fileName;
    private static Logger log = Logger.getLogger("CSVReader");
    private CSVReader fileReader;
    private boolean isPositionalMapFormed;
    private ArrayList<ArrayList<Long>> positionalMap;
    private int columnSize;
    private byte[] inMemCompleteFile;

    /**Constructor
     * @param fileName filename with path
     */
    public CSVInMemParser(String fileName) {
        this.fileName = fileName;
        positionalMap = new ArrayList<>();
        fileReader = new CSVReader(fileName);
        inMemCompleteFile=fileReader.readCSVAllLines();
        isPositionalMapFormed = false;
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
            for(long j=startPos;j<=endPos;++j){
                val.append((char)inMemCompleteFile[(int)j]);
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
        int colCount=0;
        long position=0;
        ArrayList<Long> returnList = new ArrayList<>();
        ArrayList<Long> row = new ArrayList<>();
        StringBuilder val = new StringBuilder();
        for(int i=0;i<inMemCompleteFile.length;++i){
            if(inMemCompleteFile[i]==44){
                row.add(position-1);
                colCount++;
            }
            else if(inMemCompleteFile[i]==10){
                row.add(position-1);
                returnList.add(Long.parseLong(val.toString()));
                colCount=0;
                positionalMap.add(row);
                row = new ArrayList<>();
                val = new StringBuilder();
            }
            else if(colCount==column){
                val.append((char)inMemCompleteFile[i]);
            }
            position++;
        }
        columnSize=positionalMap.get(0).size();
        isPositionalMapFormed=true;
        return returnList;
    }

}

