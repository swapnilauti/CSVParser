import java.util.ArrayList;

/**
 * Created by SwapnilSudam on 11/1/2016.
 */
public class CSVUtil {
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
