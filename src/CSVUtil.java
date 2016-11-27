import java.util.ArrayList;

/**
 * Created by SwapnilSudam on 11/1/2016.
 */
public class CSVUtil {
    public static long byteArrayToLong(ArrayList<Byte> b){
        long toRet=0l;
        for(int i=0;i<b.size();i++){
            toRet*=10;
            toRet+=(long)b.get(i);
        }
        return toRet;
    }
    public static long byteArrayToLong(byte[] b){
        long toRet=0l;
        for(int i=0;i<b.length;i++){
            toRet*=10;
            toRet+=(long)b[i];
        }
        return toRet;
    }
}
