package cn.strongculture.mybatis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Typehandler<T> {
    void setParameter(PreparedStatement statement,int i, T value) throws SQLException;
    T getResult(ResultSet rs, String columnName) throws SQLException;
}
