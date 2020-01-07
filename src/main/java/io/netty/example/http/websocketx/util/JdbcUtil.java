package io.netty.example.http.websocketx.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import io.netty.example.http.websocketx.base.PropertyUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

@Slf4j
public class JdbcUtil {

        private static JdbcUtil jdbcUtil = null;
        private static DruidDataSource druidDataSource = null;

        static {
                try {
                        Properties prop = PropertyUtil.load("druid.properties");
                        druidDataSource= (DruidDataSource) DruidDataSourceFactory.createDataSource(prop);
                        log.info("durid连接池初始化成功");
                } catch (Exception e) {
                        log.error("durid连接池初始化失败:{}",e);
                }
        }

        /**
         * 数据库连接池单例
         * @return
         */
        public static synchronized JdbcUtil getInstance(){
                if (null == jdbcUtil){
                        jdbcUtil = new JdbcUtil();
                }
                return jdbcUtil;
        }

        /**
         * 返回druid数据库连接
         * @return
         * @throws SQLException
         */
        public DruidPooledConnection getConnection() throws SQLException{
                return druidDataSource.getConnection();
        }

        /**
         *查询多条记录
         *
         * @param sql  查询语句
         * @param clazz 返回对象的class
         * @param objects 需要的参数，必须跟sql占位符的位置一一对应
         * @param <T>   泛型返回
         *
         * @return list
         */
        public static <T> List<T> queryForList(String sql, Class<T> clazz, Object... objects) throws SQLException {
                JdbcUtil jdbcUtil = JdbcUtil.getInstance();
                DruidPooledConnection connection = jdbcUtil.getConnection();
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                List<T> list = new ArrayList<>();
                try {
                        preparedStatement = getStateMent(connection, sql, objects);
                        resultSet = getResultSet(preparedStatement);
                        while (resultSet.next()) {
                                //调用 invokeObject方法，把一条记录封装成一个对象，添加到list中
                                list.add(invokeObject(resultSet, clazz));
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }  finally {
                        close(preparedStatement, resultSet, connection);
                }
                return list.size() > 0 ? list : null;

        }

        private static void close(PreparedStatement preparedStatement, ResultSet resultSet, DruidPooledConnection connection) {
                try {
                        if(resultSet != null)
                                resultSet.close();
                        if (preparedStatement != null)
                                preparedStatement.close();
                        if(connection != null)
                                connection.close();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        /**
         * 把数据库中的一条记录通过反射包装成相应的Bean
         * @param resultSet
         * @param clazz
         * @param <T>
         * @return
         * @throws IllegalAccessException
         * @throws InstantiationException
         * @throws SQLException
         * @throws NoSuchFieldException
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         */
        private static <T> T invokeObject(ResultSet resultSet, Class<T> clazz) throws IllegalAccessException, InstantiationException,
                SQLException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
                T object = clazz.getDeclaredConstructor().newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 0, count = metaData.getColumnCount(); i < count; i++) {
                        String columnName = metaData.getColumnName(i + 1);     //数据库返回结果的列名
                        String fieldName = StringUtil.camelName(columnName); //去掉列名中的下划线“_”并转为驼峰命名
                        Field field = clazz.getDeclaredField(fieldName);            //根据字段名获取field
                        String methName = setMethodName(fieldName);         //拼set方法名
                        Class type = field.getType();                       //获取字段类型
                        Method setMethod = clazz.getDeclaredMethod(methName, field.getType());
                        Object value = resultSet.getObject(i + 1);            //获取字段值
                        setMethod.invoke(object, type.cast(value));       //强转并且赋值
                }
                return object;
        }

        private static PreparedStatement getStateMent(Connection connection, String sql, Object... objects) throws SQLException {

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                if(objects!=null){
                        for (int i = 0, len = objects.length; i < len; i++) {
                                preparedStatement.setObject(i + 1, objects[i]);  //给sql每个？占位符填上数据
                        }
                }
                return preparedStatement;
        }

        private static ResultSet getResultSet(PreparedStatement statement) throws SQLException {
                if (statement == null) {
                        return null;
                } else {
                        return statement.executeQuery();
                }
        }

        private static String setMethodName(String str) {
                return "set" + StringUtil.firstUpperCase(str);
        }
}
