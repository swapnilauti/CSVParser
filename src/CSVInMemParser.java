
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;


public class CSVInMemParser implements CSVParser {

    private static String fileName;
    private static Logger log = Logger.getLogger("CSVReader");
    private CSVReader fileReader;
    private boolean isPositionalMapFormed;
    private ArrayList<ArrayList<Long>> positionalMap;
    private int columnSize;
    private byte[] inMemCompleteFile;
    private int[] dateColumns;
    private DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

    /**Constructor
     * @param fileName filename with path
     */
    public CSVInMemParser(String fileName, int dateColumns[]) {
        this.fileName = fileName;
        this.dateColumns = dateColumns;
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

    private boolean isDateColumn(int colNo){
        for(int i:dateColumns){
            if(colNo==i)
                return true;
        }
        return false;
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
            if(isDateColumn(column)) {
                return (createPositionalMapFetchDateCol(column));
            }
            else{
                return (createPositionalMapFetchLongCol(column));
            }
        }
        ArrayList<Long> returnList = null;
        if(isDateColumn(column)){
            returnList = fetchDateColumn(column);
        }
        else{
            returnList = fetchLongColumn(column);
        }
        fileReader.resetFilePos();
        return returnList;
    }

    public ArrayList<Long> fetchDateColumn(int column){
        ArrayList<Long> returnList = new ArrayList<>();
        for(int i=0;i<positionalMap.size();++i){
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
            StringBuilder sb = new StringBuilder();
            for(long j=startPos;j<=endPos;++j){
                sb.append((char)inMemCompleteFile[(int)j]);
            }
            Date date = null;
            try {
                date = df.parse(sb.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            returnList.add(date.getTime());
        }
        return returnList;
    }
    public ArrayList<Long> fetchLongColumn(int column){
        ArrayList<Byte> cell = new ArrayList<>();
        ArrayList<Long> returnList = new ArrayList<>();
        for(int i=0;i<positionalMap.size();++i){
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
                cell.add(inMemCompleteFile[(int)j]);
            }

            returnList.add(CSVUtil.byteArrayToLong(cell));
            cell.clear();
        }
        return returnList;
    }

    @Override
    public long fetchValue(int column, int rowId) {
        return 0;
    }

    @Override
    public ArrayList<Long> fetchColumnByRowId(int column, int rowIdMin, int rowIdMax) {
        return null;
    }

    private ArrayList<Long> createPositionalMapFetchDateCol(int column){
        int colCount=0;
        long position=0;
        ArrayList<Long> returnList = new ArrayList<>();
        ArrayList<Long> row = new ArrayList<>();
        byte newLine = (byte)10,comma = (byte)44;
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<inMemCompleteFile.length;++i){
            if(inMemCompleteFile[i]==comma){
                row.add(position-1);
                colCount++;
            }
            else if(inMemCompleteFile[i]==newLine){
                row.add(position-1);
                Date date = null;
                try {
                    date = df.parse(sb.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sb = new StringBuilder();
                returnList.add(date.getTime());
                colCount=0;
                positionalMap.add(row);
                row = new ArrayList<>();
            }
            else if(colCount==column){
                sb.append((char)inMemCompleteFile[(int)i]);
            }
            position++;
        }
        columnSize=positionalMap.get(0).size();
        isPositionalMapFormed=true;
        return returnList;
    }

    /**
     * This is private method is used for creating full positional map and simultaneously
     * load the desired column as well. It is being called internally from fetchColumn.
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    private ArrayList<Long> createPositionalMapFetchLongCol(int column) {
        int colCount=0;
        long position=0;
        ArrayList<Long> returnList = new ArrayList<>();
        ArrayList<Long> row = new ArrayList<>();
        ArrayList<Byte> cell = new ArrayList<>();
        byte newLine = (byte)10,comma = (byte)44;
        for(int i=0;i<inMemCompleteFile.length;++i){
            if(inMemCompleteFile[i]==comma){
                row.add(position-1);
                colCount++;
            }
            else if(inMemCompleteFile[i]==newLine){
                row.add(position-1);
                returnList.add(CSVUtil.byteArrayToLong(cell));
                colCount=0;
                positionalMap.add(row);
                row = new ArrayList<>();
                cell.clear();
            }
            else if(colCount==column){
                cell.add(inMemCompleteFile[i]);
            }
            position++;
        }
        columnSize=positionalMap.get(0).size();
        isPositionalMapFormed=true;
        return returnList;
    }

}

