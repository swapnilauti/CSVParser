import jitd.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by SwapnilSudam on 10/11/2016.
 */
class FileValueIterator extends KeyValueIterator{
    ArrayList<StringBuilder> sb;
    int pos = 0;
    long key,value;
    public FileValueIterator(ArrayList<StringBuilder> sb){
        this.sb = sb;
    }
    public long getKey(){
        return key;
    }
    public long getValue(){
        return value;
    }
    public boolean next(){
        if(pos<sb.size()){
            key = pos;
            value = Long.valueOf(sb.get(pos).toString());
            return true;
        }
        return false;
    }
}
public class CSVDriver {
    public static void main(String args[]){
        String pathname = "D:\\Languages and Runtime for Big Data\\CSVParser\\extras\\NBA.csv";
        int totalColumns = 18;
        int opCount = 10;
        boolean fullScan = true;
        File file = new File(pathname);
        CSVParser obj = new CSVParser(file, totalColumns);
        for(int colToQuery=totalColumns-2;colToQuery>0;colToQuery--){
            System.out.println("reading column no " + colToQuery);
            long start = System.nanoTime();
            ArrayList<StringBuilder> sb = obj.fetchCol(colToQuery);
            long end = System.nanoTime();
            KeyValueIterator src = new FileValueIterator(sb);
            ArrayCog root = new ArrayCog(sb.size());
            System.out.println("loading");
            root.load(src);
            long fullEnd = System.nanoTime();
            Mode mode = new MergeMode();
            Driver driver = new Driver(mode, root);
            System.out.println("Fetch (" + colToQuery + "): " + (end - start) / 1000 + " us (w/ Convert: " + (fullEnd - start) / 1000 + " us");

//            for (int i = 0; i < opCount; i++) {
//                long k = rand.nextLong() % 56;
//                long start = System.nanoTime();
//                KeyValueIterator iter = driver.scan(k, k + 100);
//                long end = System.nanoTime();
//                if (fullScan) {
//                    while (iter.next()) { /* do nothing? */ }
//                }
//                long fullEnd = System.nanoTime();
//                System.out.println("Read (" + i + "): " + (end - start) / 1000 + " us (w/ scan: " + (fullEnd - start) / 1000 + " us");
//            }

        }
    }
}
