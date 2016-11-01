import java.util.ArrayList;

/**
 * Created by SwapnilSudam on 11/1/2016.
 */
public interface CSVParser {
    public int getColumnSize();
    public ArrayList<Long> fetchColumn(int column);
    public long fetchValue(int column, int rowId);
    public ArrayList<Long> fetchColumnByRowId(int column, int rowIdMin, int rowIdMax);
}
