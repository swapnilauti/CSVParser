import java.util.Random;

/**
 * Created by SwapnilSudam on 10/20/2016
 */
public class CSVDriver {
    public static void main(String args[]){
        String pathname = "D:\\Languages and Runtime for Big Data\\CSVParser\\extras\\NBA.csv";
        int totalColumns = 18;
        int opCount = 10;
        boolean fullScan = true;
        Random rand = new Random();
        CSVTable sportyDS = new CSVTable(pathname);
        long wholeParseStart = System.nanoTime();
        CSVInitLoad initLoad = new CSVInitLoad(pathname);
        long wholeParseEnd = System.nanoTime();
        long initTime =0, sportyTime =0;
        System.out.println("Time taken for initial parsing and conversions of whole file "+(wholeParseEnd-wholeParseStart)/1000 +" us");
        initTime+=(wholeParseEnd-wholeParseStart)/1000;
        long start = 0, end = 0;
        int i=0;

        for(int colToQuery=totalColumns-1;colToQuery>0;colToQuery--) {
                System.out.println(" Querying column No "+ colToQuery);
                //for (int i = 0; i < opCount; i++) {
                    long k = rand.nextLong() % 56;
                    start = System.nanoTime();
                    sportyDS.rangeScan(colToQuery, k, k + 100);
                    end = System.nanoTime();
                    sportyTime+=(end-start)/1000;
                    System.out.println("SportyDS range scan (" + i + ") : " + (end - start) / 1000 + " us ");
                    start = System.nanoTime();
                    initLoad.rangeScan(colToQuery, k, k + 100);
                    end = System.nanoTime();
                    initTime+=(end-start)/1000;
                    System.out.println("InitLoad range scan (" + i + ") : " + (end - start) / 1000 + " us ");
                    System.out.println("Difference Sporty - init = "+(sportyTime-initTime));
                //}
        }
        System.out.println("FINAL Difference Sporty - init = "+(sportyTime-initTime));
    }
}