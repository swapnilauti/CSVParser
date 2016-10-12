import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by SwapnilSudam on 10/10/2016.
 */
public class CSVParser {
    private final int BLOCK_SIZE = 40000;
    private File file;
    private int totalCol;
    private ArrayList<Integer> positionalMaps[];
    private ArrayList<Integer> blockMaps[];
    public CSVParser(File file, int totalCol){
        this.file = file;
        this.totalCol = totalCol;
    }
    public  ArrayList<StringBuilder> initFetch(int colNo){
        if(colNo < 0 || colNo >= totalCol){
            return null;
        }
        FileInputStream fis = null;
        positionalMaps = (ArrayList<Integer>[])new ArrayList[totalCol];
        blockMaps = (ArrayList<Integer>[])new ArrayList[totalCol];
        for(int i=0;i<totalCol;i++){
            positionalMaps[i]=new ArrayList<Integer>();
        }
        for(int i=0;i<totalCol;i++){
            blockMaps[i]=new ArrayList<Integer>();
        }
        int rowOffset = colNo==totalCol-1?1:0;
        ArrayList<StringBuilder> ret = new ArrayList<StringBuilder>();
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        FileChannel fc = fis.getChannel();
        byte[] buffer = new byte[BLOCK_SIZE];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        int bytesRead = 0, i = 0, colCount = 0;
        int rowCount = 0, rowsRead = rowCount;
        positionalMaps[colCount++].add(0);
        try {
            while((bytesRead = fc.read(bb))!=-1){
                for(i=0;i<bytesRead;i++){
                    if(buffer[i]==44){                          // if ',' is encountered
                        positionalMaps[colCount++].add(i+1);
                    }
                    else if(buffer[i]==10){                     // if '\n' is encountered
                        colCount = 0;
                        positionalMaps[colCount++].add(i+1);
                        rowCount++;
                    }
                }
                if(ret.size()==rowsRead+1){
                    StringBuilder sb = ret.get(rowsRead);
                    int endIndex = positionalMaps[(colNo+1)%totalCol].get(rowsRead+rowOffset);
                    for(int startIndex=0;startIndex<endIndex-1;startIndex++){
                        sb.append((char)buffer[startIndex]);
                    }
                    rowsRead++;
                }
                for(;rowsRead<rowCount;rowsRead++){
                    int startIndex = positionalMaps[colNo].get(rowsRead);
                    int endIndex = positionalMaps[(colNo+1)%totalCol].get(rowsRead+rowOffset);
                    StringBuilder sb = new StringBuilder();
                    for(;startIndex<endIndex-1;startIndex++) {
                        sb.append((char)buffer[startIndex]);
                    }
                    ret.add(sb);
                }
                //check this
                int j=0;
                for(;j<colCount;j++) {
                    blockMaps[j].add(rowCount);
                }
                for(;j<totalCol;j++) {
                    blockMaps[j].add(rowCount-1);
                }
                bb.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public  ArrayList<StringBuilder> fetch(int colNo){
        if(colNo < 0 || colNo >= totalCol){
            return null;
        }
        FileInputStream fis = null;
        int rowOffset = colNo==totalCol-1?1:0;
        ArrayList<StringBuilder> ret = new ArrayList<StringBuilder>();
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        FileChannel fc = fis.getChannel();
        byte[] buffer = new byte[BLOCK_SIZE];
        ByteBuffer bb = ByteBuffer.wrap(buffer);
        int bytesRead = 0, i = 0, colCount = 0;
        int rowCount = 0, rowsRead = rowCount;
        positionalMaps[colCount++].add(0);
        try {
            for(int blocksRead=0;(bytesRead = fc.read(bb))!=-1;blocksRead++){
                if(ret.size()==rowsRead+1){
                    StringBuilder sb = ret.get(rowsRead);
                    int endIndex = positionalMaps[(colNo+1)%totalCol].get(rowsRead+rowOffset);
                    for(int startIndex=0;startIndex<endIndex-1;startIndex++){
                        sb.append((char)buffer[startIndex]);
                    }
                    rowsRead++;
                }
                rowCount=blockMaps[colNo].get(blocksRead);
                for(;rowsRead<rowCount;rowsRead++){
                    int startIndex = positionalMaps[colNo].get(rowsRead);
                    int endIndex = positionalMaps[(colNo+1)%totalCol].get(rowsRead+rowOffset);
                    StringBuilder sb = new StringBuilder();
                    for(;startIndex<endIndex-1;startIndex++) {
                        sb.append((char)buffer[startIndex]);
                    }
                    ret.add(sb);
                }
                bb.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public ArrayList<StringBuilder> fetchCol(int colNo){
            return positionalMaps==null?initFetch(colNo):fetch(colNo);
    }
    public static void main(String args[]){
        String pathname = "D:\\Languages and Runtime for Big Data\\CSVParser\\extras\\NBA.csv";
        int totalColumns = 18;
        int colToQuery = 0;
        CSVParser obj = new CSVParser(new File(pathname),totalColumns);
        for(;colToQuery<totalColumns;colToQuery++) {
            ArrayList<StringBuilder> sb = obj.initFetch(colToQuery);
            Iterator<StringBuilder> i = sb.iterator();
            while (i.hasNext()) {
                System.out.println(Long.parseLong(i.next().toString()));
            }
            System.out.println();
        }
        System.out.println("done");
    }

}
