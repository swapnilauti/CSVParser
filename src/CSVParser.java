import java.util.ArrayList;

/**
 * Interface for Parsers
 */
public interface CSVParser {
    public int getColumnSize();
    public ArrayList<Long> fetchColumn(int column);
    public long fetchValue(int column, int rowId);
    public ArrayList<Long> fetchColumnByRowId(int column, int rowIdMin, int rowIdMax);
}
