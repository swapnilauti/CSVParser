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
    private Cog cogs[];
    private boolean hasCog[];
    private final int blockSize = 8*1024;
    private CSVParser csvParser;

    public CSVTable(String filePath){
        csvParser = new CSVParser(filePath,blockSize);
    }

    private void createCog(int colNo){
        if(cogs==null){
            ArrayList<Long> columnValues = csvParser.fetchColumn(colNo);
            int totalCols = csvParser.getColumnSize();
            cogs = new Cog[totalCols];
            hasCog = new boolean[totalCols];
            FileValueIterator fvi = new FileValueIterator(columnValues);
            ArrayCog root = new ArrayCog(columnValues.size());
            root.load(fvi);
            cogs[colNo] = root;
            hasCog[colNo] = true;
        }
        else if(!hasCog[colNo]){
            ArrayList<Long> columnValues = csvParser.fetchColumn(colNo);
            FileValueIterator fvi = new FileValueIterator(columnValues);
            ArrayCog root = new ArrayCog(columnValues.size());
            root.load(fvi);
            cogs[colNo] = root;
            hasCog[colNo] = true;
        }
    }

    public KeyValueIterator rangeScan(int colNo, long low, long high){
        if(cogs==null || !hasCog[colNo])
            createCog(colNo);
        Mode mode = new MergeMode();
        Cog root = cogs[colNo];
        Driver driver = new Driver(mode, root);
        KeyValueIterator iter = driver.scan(low,high);
        return iter;
    }

    public long lookUp(int col, int row){
        return csvParser.fetchValue(col,row);
    }
    public ArrayList<Long> lookUp(int col, int row1, int row2){
        if(row1>row2){
            int temp = row1;
            row1 = row2;
            row2 = temp;
        }
        return csvParser.fetchColumnByRowId(col, row1, row2);
    }
}
