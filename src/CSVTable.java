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
    private int blockSize = 24*1024;
    private CSVParser csvParser;

    public CSVTable(String filePath, int blockSize){
        this.blockSize = blockSize;
        csvParser = new CSVParser(filePath,blockSize);
    }
    public CSVTable(String filePath){
        CSVReader csvReader = new CSVReader(filePath);
        byte block[] = csvReader.readCSVAllLines();
        int totalCol = calculateTotalCol(block);
        ArrayList<Long> columns[] = new ArrayList[totalCol];
        for(int i=0;i<totalCol;i++){
            columns[i]=new ArrayList<Long>();
        }
        ArrayList<Byte> cell = new ArrayList<>();
        int colNo =0;
        for(int i=0;i<block.length;i++){
            if(block[i]==10) {            //'\n'
                columns[colNo].add(byteArrayToLong(cell));
                colNo=0;
                cell = new ArrayList<Byte>();
            } else if(block[i]==44){
                columns[colNo++].add(byteArrayToLong(cell));
                cell = new ArrayList<Byte>();
            } else{
                cell.add(block[i]);
            }
        }
    }

    private long byteArrayToLong(ArrayList<Byte> b){
        long toRet=0l;
        for(int i=0;i<b.size();i++){
            toRet*=10;
            toRet+=(long)b.get(i);
        }
        return toRet;
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
        time[0]=(System.nanoTime()-time[0])/1000;
        if(cogDrivers==null){
            int totalCols = csvParser.getColumnSize();
            cogDrivers = new Driver[totalCols];
            hasCog = new boolean[totalCols];
        }
        FileValueIterator fvi = new FileValueIterator(columnValues);
        ArrayCog root = new ArrayCog(columnValues.size());
        root.load(fvi);
        cogDrivers[colNo] = new Driver(new MergeMode(),root);
        hasCog[colNo] = true;
    }

    public KeyValueIterator rangeScan(int colNo, long low, long high, long time[]){
        if(cogDrivers==null || !hasCog[colNo])
            createCog(colNo,time);
        Driver driver = cogDrivers[colNo];
        KeyValueIterator iter = driver.scan(low,high);
        return iter;
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
