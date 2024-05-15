package cn.strongculture.mybatis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTypeHander implements Typehandler<String>{
    @Override
    public void setParameter(PreparedStatement statement,int i, String value) throws SQLException {
        statement.setString(i,value);
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }
}
