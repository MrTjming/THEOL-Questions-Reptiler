import java.sql.ResultSet;

public interface ResultSetExtractor<T> {
    public abstract T extractData(ResultSet rs);

}