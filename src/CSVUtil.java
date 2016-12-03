import java.util.ArrayList;

/**
 * Utility class
 */
public class CSVUtil {
    public static final int IN_FILE_PARSER = 0;
    public static final int IN_MEM_PARSER = 1;

    /*
    Utility function to convert a String equivalent of comma-separated integers to array of integers
     */
    public static int[] stringToIntArray(String s){
        String sarr[] = s.split(",");
        int ret[] = new int[sarr.length];
        int i=0;
        for(String colNo:sarr){
            ret[i++]=Integer.parseInt(colNo);
        }
        return ret;
    }

    /*
    Utility function to convert ArrayList<Byte> to long
     */
    public static long byteArrayToLong(ArrayList<Byte> b){
        long toRet=0l;
        char c = '0';
        byte b0 = (byte)c;
        for(int i=0;i<b.size();i++){
            toRet*=10;
            toRet+=(long)(b.get(i)-b0);
        }
        return toRet;
    }

    /*
    Utility function to array of bytes to long
     */
    public static long byteArrayToLong(byte[] b){
        long toRet=0l;
        char c = '0';
        byte b0 = (byte)c;
        for(int i=0;i<b.length;i++){
            toRet*=10;
            toRet+=(long)(b[i]-b0);
        }
        return toRet;
    }
}
