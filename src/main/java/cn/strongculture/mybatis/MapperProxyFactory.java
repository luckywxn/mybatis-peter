package cn.strongculture.mybatis;

import cn.strongculture.peter.Hotel;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class MapperProxyFactory {

    private static Map<Class,Typehandler> typehandlers = new HashMap<>();

    static {
        typehandlers.put(Integer.class,new IntergerTypeHander());
        typehandlers.put(String.class,new StringTypeHander());
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
                Connection connection = getConnection();
                Select annotation = method.getAnnotation(Select.class);
                String sql = annotation.value();
                System.out.println(sql);

                // 参数名：参数值
                Map<String, Object> paramMap = new HashMap<>();
                Parameter[] parameters = method.getParameters();
                for (int i = 0;i<parameters.length;i++){
                    Parameter parameter = parameters[i];
                    String name = parameter.getAnnotation(Param.class).value();
                    paramMap.put(name, args[i]);
                }

                ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
                GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
                String prepareSql = parser.parse(sql);
                List<ParameterMapping> parameterMappings = tokenHandler.getParameterMappings();

                //2. 构造PrepareStatement
                PreparedStatement statement = connection.prepareStatement(prepareSql);
                for (int i = 0; i < parameterMappings.size(); i++){
                    String property = parameterMappings.get(i).getProperty();
                    Object value = paramMap.get(property);
                    Class<?> type = value.getClass();
                    typehandlers.get(type).setParameter(statement,i+1,value);
                }

                //3. 执行PrepareStatement
                statement.execute();

                //4. 根据当前执行的方法返回类型
                Object result = null;
                List<Object> list = new ArrayList<>();

                Class<?> returnType = null;
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof Class) {
                    //不是泛型
                    returnType = (Class<?>) genericReturnType;
                }else if (genericReturnType instanceof ParameterizedType){
                    //是泛型
                    Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                    returnType = (Class<?>) actualTypeArguments[0];
                }
                ResultSet resultSet = statement.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<String> columnNames = new ArrayList<>();
                for (int i = 0; i < metaData.getColumnCount(); i++){
                    columnNames.add(metaData.getColumnName(i + 1));
                }
                Map<String,Method> setterMethodMapping = new HashMap<>();
                for (Method declaredMethod : returnType.getDeclaredMethods()) {
                    if (declaredMethod.getName().startsWith("set")){
                        String propertyName = declaredMethod.getName().substring(3);
                        propertyName = propertyName.substring(0,1).toLowerCase(Locale.ROOT) + propertyName.substring(1);
                        setterMethodMapping.put(propertyName,declaredMethod);
                    }
                }

                while (resultSet.next()) {
                    //一行数据 --> Java类型
                    //resultType
                    Object instance = returnType.newInstance();
                    for (int i = 0; i < columnNames.size(); i++){
                        String column = columnNames.get(i);//id name
                        Method setterMethod = setterMethodMapping.get(column);
                        Class clazz = setterMethod.getParameterTypes()[0];
                        Typehandler typehandler = typehandlers.get(clazz);
                        setterMethod.invoke(instance,typehandler.getResult(resultSet,column));
                    }
//                    Field[] fields = returnType.getDeclaredFields();
//                    for (Field field : fields) {
//                        String name = field.getName();
//                        Object value = resultSet.getObject(name);
//                        field.setAccessible(true);
//                        field.set(instance,value);
//                    }

                    list.add(instance);
                }

                if (method.getReturnType().equals(List.class)){
                    result =  list;
                }else {
                    result = list.get(0);
                }

                //5. 关闭数据库连接
                connection.close();
                return result;
            }
        });
        return (T)proxyInstance;
    }

    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/heima?&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&serverTimezone=Asia/Shanghai", "root", "root");
        return connection;
    }
}
