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
    private final int blockSize = 24*1024;
    private CSVParser csvParser;

    public CSVTable(String filePath){
        csvParser = new CSVParser(filePath,blockSize);
    }

    private void createCog(int colNo){
        if(cogDrivers==null){
            ArrayList<Long> columnValues = csvParser.fetchColumn(colNo);
            int totalCols = csvParser.getColumnSize();
            cogDrivers = new Driver[totalCols];
            hasCog = new boolean[totalCols];
            FileValueIterator fvi = new FileValueIterator(columnValues);
            ArrayCog root = new ArrayCog(columnValues.size());
            root.load(fvi);
            cogDrivers[colNo] = new Driver(new MergeMode(),root);
            hasCog[colNo] = true;
        }
        else if(!hasCog[colNo]){
            ArrayList<Long> columnValues = csvParser.fetchColumn(colNo);
            FileValueIterator fvi = new FileValueIterator(columnValues);
            ArrayCog root = new ArrayCog(columnValues.size());
            root.load(fvi);
            cogDrivers[colNo] = new Driver(new MergeMode(),root);
            hasCog[colNo] = true;
        }
    }

    public KeyValueIterator rangeScan(int colNo, long low, long high){
        if(cogDrivers==null || !hasCog[colNo])
            createCog(colNo);
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
