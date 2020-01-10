package io.netty.example.http.websocketx.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class DruidUtil {

        private static DruidUtil jdbcUtil = null;
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
        public static synchronized DruidUtil getInstance(){
                if (null == jdbcUtil){
                        jdbcUtil = new DruidUtil();
                }
                return jdbcUtil;
        }

        /**
         * 返回druid数据库连接
         * @return
         * @throws SQLException
         */
        public static DruidPooledConnection getConnection(){
                try {
                        return druidDataSource.getConnection();
                } catch (SQLException e) {
                        log.error("获取数据库连接失败");
                        e.printStackTrace();
                }
                return null;
        }
}
