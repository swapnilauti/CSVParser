import jitd.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by SwapnilSudam on 10/18/2016.
 */

class DefaultParserIterator extends KeyValueIterator {
    Iterator<CSVRecord> iterator;
    int columnNo;
    long value, key=-1;
    public DefaultParserIterator(List<CSVRecord> list,int columnNo) {
        this.columnNo = columnNo;
        iterator = list.iterator();
    }
    public long getKey(){
        return key;
    }
    public long getValue(){
        return value;
    }
    public boolean next(){
        if(iterator.hasNext()){
            value = Long.parseLong(iterator.next().get(columnNo));
            key++;
            return true;
        }
        return false;
    }
}

public class CSVInitLoad {

    private Driver cogDrivers[];
    int totalColumns;
    List<CSVRecord> list;

    public CSVInitLoad(String filePath){
        FileReader fr = null;
        try {
            fr = new FileReader(filePath);
            org.apache.commons.csv.CSVParser apacheParser = CSVFormat.EXCEL.parse(fr);
            list = apacheParser.getRecords();
            totalColumns = list.get(0).size();
            cogDrivers = new Driver[totalColumns];
            for(int i=0;i<totalColumns;i++) {
                DefaultParserIterator iterator = new DefaultParserIterator(list, i);
                ArrayCog root = new ArrayCog(list.size());
                root.load(iterator);
                cogDrivers[i] = new Driver(new MergeMode(),root);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public KeyValueIterator rangeScan(int colNo, long low, long high){
        if(high<low){
            long temp = high;
            high = low;
            low = temp;
        }
        Driver driver = cogDrivers[colNo];
        KeyValueIterator iter = driver.scan(low,high);
        return iter;
    }
    public Long fetchValue(int col, int row){
        return Long.parseLong(list.get(row).get(col));
    }
    public ArrayList<Long> fetchColumnByRowId(int col, int rowl, int rowh){
        ArrayList<Long> alTOReturn = new ArrayList<Long>();
        if(rowl>rowh){
            int temp = rowl;
            rowl = rowh;
            rowh = temp;
        }
        for(int i = rowl;i<rowh;i++){
            alTOReturn.add(Long.parseLong(list.get(i).get(col)));
        }
        return alTOReturn;
    }
}
