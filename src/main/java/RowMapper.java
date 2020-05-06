import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * @author: Jemy-Tan
 * @date: 2019/5/24
 * @description:
 */

public interface RowMapper<T> {
    public abstract T mapRow(ResultSet rs, int index) throws SQLException, ClassNotFoundException;
}