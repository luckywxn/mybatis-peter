package cn.strongculture.mybatis;

import cn.strongculture.peter.Hotel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MapperProxyFactory {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static <T> T getMapper(Class<T> mapper) {
        Object proxyInstance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapper}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //解析sql ---> 执行sql ---> 返回结果
                //JDBC
                //1.创建数据库连接
                Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/heima?&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=Asia/Shanghai", "root", "root");

                //2. 构造PrepareStatement
                PreparedStatement statement = connection.prepareStatement("select * from tb_hotel where name = ?");
                statement.setString(1, "7天连锁酒店(上海宝山路地铁站店)");

                //3. 执行PrepareStatement
                statement.execute();

                //4. 封装结果
                List<Hotel> list = new ArrayList<>();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    Hotel hotel = new Hotel();
                    hotel.setId(resultSet.getInt("id"));
                    hotel.setName(resultSet.getString("name"));
                    hotel.setAddress(resultSet.getString("address"));
                    list.add(hotel);
                }
                return list;
            }
        });
        return (T)proxyInstance;
    }
}
