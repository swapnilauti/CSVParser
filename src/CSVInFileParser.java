import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/*
    Class for InFile Parser
 */
public class CSVInFileParser implements CSVParser{
    private static String fileName;
    private static Logger log = Logger.getLogger("CSVReader");
    private CSVReader fileReader;
    private int blockSize;
    public enum PositionalMapStatus{
        NONE,PARTIAL,COMPLETE
    }
    PositionalMapStatus positionalMapStatus;
    private int lastRowPositionalMap;
    private ArrayList<ArrayList<Long>> positionalMap;
    private int columnSize;
    private int[] dateColumns;
    private DateFormat df = new SimpleDateFormat("MM-dd-yyyy");


    /**Constructor
     * @param blockSize blocksize for the file read
     * @param fileName filename with path
     */
    public CSVInFileParser(String fileName, int blockSize, int[] dateColumns) {
        this.fileName = fileName;
        this.dateColumns = dateColumns;
        positionalMap = new ArrayList<>();
        fileReader = new CSVReader(fileName);
        this.blockSize = blockSize;
        this.lastRowPositionalMap=-1;
        positionalMapStatus=PositionalMapStatus.NONE;
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
        if (positionalMapStatus==PositionalMapStatus.NONE) {
            if(isDateColumn(column)) {
                return (createPositionalMapFetchDateCol(column));
            }
            else{
                return (createPositionalMapFetchLongCol(column));
            }
        }
        if(positionalMapStatus==PositionalMapStatus.PARTIAL){
            //TODO
        }
        //case when positionalMapStatus==PositionalMapStatus.COMPLETE
        if(isDateColumn(column)){
            return fetchDateColFromPositionalMap(column);
        }
        else{
            return fetchLongColFromPositionalMap(column);
        }

    }

    /**
     * If no positional map is created, this function creates a positional map and returns values for first column
     * * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    private ArrayList<Long> createPositionalMapFetchDateCol(int column) {

        int colCount=0,bytesRead;
        long position=0;
        byte[] block=new byte[blockSize];
        ArrayList<Long> returnList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        while((bytesRead=fileReader.readCSVBlock(block)) > 0){
            ArrayList<Long> row = new ArrayList<>();
            byte newLine = (byte)10,comma = (byte)44;
            for(int i=0;i<bytesRead;++i){
                if(block[i]==comma){
                    row.add(position-1);
                    colCount++;
                }
                else if(block[i]==newLine){
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
                    sb.append((char)block[i]);
                }
                position++;
            }
        }
        columnSize=positionalMap.get(0).size();
        fileReader.resetFilePos();
        positionalMapStatus=PositionalMapStatus.COMPLETE;
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

        int colCount=0,bytesRead;
        long position=0;
        byte[] block=new byte[blockSize];
        ArrayList<Long> returnList = new ArrayList<>();
        ArrayList<Byte> cell = new ArrayList<>();
        byte newLine = (byte)10,comma = (byte)44;
        while((bytesRead=fileReader.readCSVBlock(block)) > 0){
            ArrayList<Long> row = new ArrayList<>();
            for(int i=0;i<bytesRead;++i){
                if(block[i]==comma){
                    row.add(position-1);
                    colCount++;
                }
                else if(block[i]==newLine){
                    row.add(position-1);
                    returnList.add(CSVUtil.byteArrayToLong(cell));
                    colCount=0;
                    positionalMap.add(row);
                    row = new ArrayList<>();
                    cell.clear();
                }
                else if(colCount==column){
                    cell.add(block[i]);
                }
                position++;
            }
        }
        columnSize=positionalMap.get(0).size();
        fileReader.resetFilePos();
        positionalMapStatus=PositionalMapStatus.COMPLETE;
        return returnList;
    }

    private ArrayList<Long> fetchDateColFromPositionalMap(int column) {

        ArrayList<Long> returnList = new ArrayList<>();
        int bytesRead=0;
        long bytesTillLastBlock=0;
        byte[] block=new byte[blockSize];
        StringBuilder sb = new StringBuilder();
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
            if(startPos>=bytesTillLastBlock+(long)bytesRead){
                bytesTillLastBlock+=bytesRead;
                bytesRead=fileReader.readCSVBlock(block);
            }

            for(int j=(int)(startPos-bytesTillLastBlock);j<=(int)(endPos-bytesTillLastBlock);++j){
                sb.append((char)block[j]);
            }
            Date date = null;
            try {
                date = df.parse(sb.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sb = new StringBuilder();
            returnList.add(date.getTime());
        }
        fileReader.resetFilePos();
        return returnList;
    }


    /**
     * This is private method is used for fetching the column completely from the position map
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    private ArrayList<Long> fetchLongColFromPositionalMap(int column) {

        ArrayList<Long> returnList = new ArrayList<>();
        int bytesRead=0;
        long bytesTillLastBlock=0;
        byte[] block=new byte[blockSize];
        ArrayList<Byte> cell = new ArrayList<>();
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
            if(startPos>=bytesTillLastBlock+(long)bytesRead){
                bytesTillLastBlock+=bytesRead;
                bytesRead=fileReader.readCSVBlock(block);
            }
            for(int j=(int)(startPos-bytesTillLastBlock);j<=(int)(endPos-bytesTillLastBlock);++j){
                cell.add(block[j]);
            }
            returnList.add(CSVUtil.byteArrayToLong(cell));
            cell.clear();
        }
        fileReader.resetFilePos();
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

        if(positionalMapStatus==PositionalMapStatus.NONE){
            positionalMapStatus=PositionalMapStatus.PARTIAL;
            return(createPartialMapAndFetch(rowId-1,column));
        }
        if(positionalMapStatus==PositionalMapStatus.PARTIAL){
            if(rowId-1<lastRowPositionalMap){
                return(fetchValueFromPosMap(rowId-1,column));
            }
            long pos=positionalMap.get(lastRowPositionalMap).get(getColumnSize()-1) + 2;
            fileReader.setFilePos(pos);
            return(createPartialMapAndFetch(rowId-1,column));
        }
        // for positionalMapStatus==PositionalMapStatus.COMPLETE
        return(fetchValueFromPosMap(rowId-1,column));
    }

    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching then column between the given rowids
     *
     * @param column int index of the column of the CSV table
     * @return arrayList of column value in between the rowIdMin and rowIdMax
     */
    public ArrayList<Long> fetchColumnByRowId(int column, int rowIdMin, int rowIdMax) {
        return null;
    }

    /**
     * This method fetches the column by creating positional map if it
     * doesn't exist or by just fetching the column from positional map
     * if it exists
     * @param column int index of the column of the CSV table
     * @return arrayList of column value
     */
    private long fetchValueFromPosMap(int rowId, int column) {
        long startPos, endPos;
        if(column==0){
            startPos=(rowId==0?0:positionalMap.get(rowId - 1).get(getColumnSize() - 1) + 2);
            endPos = positionalMap.get(rowId).get(column);
        }
        else {
            startPos = positionalMap.get(rowId).get(column - 1) + 2;
            endPos = positionalMap.get(rowId).get(column);
        }
        byte[] b=fileReader.readValue(startPos,endPos);
        return (CSVUtil.byteArrayToLong(b));
    }

    private long createPartialMapAndFetch(int rowId, int column){
        int bytesRead,colCount=0,rowCount=lastRowPositionalMap+1;
        long position=0;
        boolean valFound=false;
        byte[] block=new byte[blockSize];
        ArrayList<Byte> cell = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        while((bytesRead=fileReader.readCSVBlock(block)) > 0){
            ArrayList<Long> row = new ArrayList<>();
            for(int i=0;i<bytesRead;++i){
                if(block[i]==44){
                    row.add(position-1);
                    colCount++;
                }
                else if(block[i]==10){
                    row.add(position-1);
                    colCount=0;
                    positionalMap.add(row);
                    row = new ArrayList<>();
                    rowCount++;
                }
                else if(rowCount==rowId && colCount==column){
                    if(isDateColumn(column)){
                        sb.append((char)block[i]);
                    }
                    else {
                        cell.add(block[i]);
                    }
                    valFound=true;
                }
                position++;
            }
            if(valFound){
                lastRowPositionalMap=rowCount-1;
                break;
            }
        }
        if(0 >= fileReader.readCSVBlock(block)){
            positionalMapStatus=PositionalMapStatus.COMPLETE;
        }
        columnSize=positionalMap.get(0).size();
        fileReader.resetFilePos();
        if(isDateColumn(column)){
            Date date = null;
            try {
                date = df.parse(sb.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return (date.getTime());
        }
        else{
            return (CSVUtil.byteArrayToLong(cell));
        }
    }

}
