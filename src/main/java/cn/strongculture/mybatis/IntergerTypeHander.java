package cn.strongculture.mybatis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntergerTypeHander implements Typehandler<Integer>{
    @Override
    public void setParameter(PreparedStatement statement, int i, Integer value) throws SQLException {
        statement.setInt(i,value);
    }

    @Override
    public Integer getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getInt(columnName);
    }
}
