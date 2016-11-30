import jitd.*;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by SwapnilSudam on 10/18/2016.
 */

class FileValueIterator extends KeyValueIterator{
    ArrayList<Long> values;
    int pos = 0;
    long key,value;
    public FileValueIterator(ArrayList<Long> values){
        this.values = values;
    }
    public long getKey(){
        return key;
    }
    public long getValue(){
        return value;
    }
    public boolean next(){
        if(pos<values.size()){
            key = pos;
            value = values.get(pos++);
            return true;
        }
        return false;
    }
}

public class CSVTable {
    private Driver cogDrivers[];
    private boolean hasCog[];
    private CSVParser csvParser;
    //hashset of date columns
    public CSVTable(String filePath, int blockSize, int dateColumns[], int parserType){                            // Constructor for InMem or InFile parser
        if(parserType == 0) {
            csvParser = new CSVInFileParser(filePath, blockSize, dateColumns);
        }
        else{
            csvParser = new CSVInMemParser(filePath, dateColumns);
        }

        System.out.println("Parser = "+csvParser.getClass().toString()+" block size(in KB) = "+(blockSize/1024));
    }

    public CSVTable(String filePath){                                           //Constructor for Naive Parser
        CSVReader csvReader = new CSVReader(filePath);
        byte block[] = csvReader.readCSVAllLines();
        int totalCol = calculateTotalCol(block);
        ArrayList<Long> columns[] = new ArrayList[totalCol];
        for(int i=0;i<totalCol;i++){
            columns[i]=new ArrayList<Long>();
        }
        ArrayList<Byte> cell = new ArrayList<>();
        int colNo =0;
        byte newLine = (byte)10,comma = (byte)44;
        for(int i=0;i<block.length;i++){
            if(block[i]==newLine) {            //'\n'
                columns[colNo].add(CSVUtil.byteArrayToLong(cell));
                colNo=0;
                cell.clear();
                } else if(block[i]==comma){
                columns[colNo++].add(CSVUtil.byteArrayToLong(cell));
                long l = CSVUtil.byteArrayToLong(cell);
                cell.clear();
                } else{
                cell.add(block[i]);
            }
        }
    }


    private int calculateTotalCol(byte block[]){
        int totalCol=0;
        for(int i=0;block[i]!=10;i++){
            if(block[i]==44){
                totalCol++;
            }
        }
        return totalCol+1;
    }

    private void createCog(int colNo, long time[]){
        time[0]=System.nanoTime();
        ArrayList<Long> columnValues = csvParser.fetchColumn(colNo);
        time[0]=(System.nanoTime()-time[0])/1000000;
        if(cogDrivers==null){
            int totalCols = csvParser.getColumnSize();
            cogDrivers = new Driver[totalCols];
            hasCog = new boolean[totalCols];
        }
        // Jitd integration starts
        FileValueIterator fvi = new FileValueIterator(columnValues);
        ArrayCog root = new ArrayCog(columnValues.size());
        root.load(fvi);
        cogDrivers[colNo] = new Driver(new MergeMode(),root);
        // Jitd integration ends
        hasCog[colNo] = true;
    }

    public KeyValueIterator rangeScan(int colNo, long low, long high, long time[]){
        if(cogDrivers==null || !hasCog[colNo])
            createCog(colNo,time);
        /*Driver driver = cogDrivers[colNo];
        KeyValueIterator iter = driver.scan(low,high);
        return iter;*/
        return null;
    }

    public long lookUp(int col, int row){
        return csvParser.fetchValue(col,row);
    }
    public ArrayList<Long> lookUp(int col, int rowl, int rowh){
        if(rowl>rowh){
            int temp = rowl;
            rowl = rowh;
            rowh = temp;
        }
        return csvParser.fetchColumnByRowId(col, rowl, rowh);
    }
}
