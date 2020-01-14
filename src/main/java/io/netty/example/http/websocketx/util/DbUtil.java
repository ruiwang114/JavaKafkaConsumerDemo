package io.netty.example.http.websocketx.util;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import io.netty.example.http.websocketx.constant.SqlDefine;
import io.netty.example.http.websocketx.entity.T01ThreatInfo;
import io.netty.example.http.websocketx.entity.T01ThreatInfoResp;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DbUtil {

    private static DruidPooledConnection con = DruidUtil.getConnection();

    public static void main(String[] args) throws SQLException {
        //map
        String authCode="authcodeHou";
        String map = SqlDefine.SELECT_CONSUMER_BY_AUTHKEYCODE;
        Map<String, Object> stringObjectMap = mapHandler(map, authCode);
        System.out.println("查询用户信息:");
        stringObjectMap.entrySet().stream().forEach(System.out::println);
        //list
        String list = SqlDefine.SELECT_G01_INDUSTRY;
        List<Object> codes = listHandler(list, null,  stringObjectMap.get("ID"));
        System.out.println("查询G01设备绑定行业信息:");
        codes.stream().forEach(System.out::println);
        //Integer
        String number = SqlDefine.SELECT_CONSUMER_TENCENT_RELATION;
        Object id = scalarHandler(number, stringObjectMap.get("ID"));
        System.out.println("查询用户与腾讯的情报绑定关系:");
        System.out.println(id.toString());
        //list
        String listBean = SqlDefine.SELECT_TENCENT_THREAT_DATA;
        List<T01ThreatInfo> t01ThreatInfoResps = beanListHander(listBean, T01ThreatInfo.class);
        System.out.println("查询腾讯情报数据:");
        List<T01ThreatInfoResp> list1=new ArrayList<>();
        t01ThreatInfoResps.stream().forEach(t -> {
            String[] tagList = t.getTAG_LIST().split("\\|");
            String[] threatList = t.getTHREAT_LIST().split("\\|");
            T01ThreatInfoResp resp = new T01ThreatInfoResp()
                    .setInfoName(t.getNAME()).setInfoType(t.getTYPE()).setLevel(t.getLEVEL())
                    .setConfidence(t.getCONFIDENCE()).setStatus(t.getACTIVE_STATUE())
                    .setSubFlag(t.getDOMAIN_FLAG()).setTagList(Arrays.asList(tagList)).setThreatList(Arrays.asList(threatList))
                    .setUpdateTime(t.getUPDATE_TIME());
            list1.add(resp);
        });
        String jsonString = JSON.toJSONString(list1);
        System.out.println(jsonString);
        list1.stream().forEach(System.out::println);
    }

    /*
     *  结果集第八种处理方法,MapListHandler
     *  将结果集每一行存储到Map集合,键:列名,值:数据
     *  Map集合过多,存储到List集合
     */
    public static List<Map<String,Object>> mapListHandler(String sql , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法query,传递结果集实现类MapListHandler
        //返回值List集合, 存储的是Map集合
        List<Map<String,Object>> list = qr.query(con, sql, new MapListHandler(),params);
        return list;
    }

    /*
     *  结果集第七种处理方法,MapHandler
     *  将结果集第一行数据,封装到Map集合中
     *  Map<键,值> 键:列名  值:这列的数据
     */
    public static Map<String,Object> mapHandler(String sql , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法query,传递结果集实现类MapHandler
        //返回值: Map集合,Map接口实现类, 泛型
        Map<String,Object> map = qr.query(con, sql, new MapHandler(),params);
        return map;
    }


    /*
     *  结果集第六种处理方法,ScalarHandler
     *  对于查询后,只有1个结果
     */
    public static Object scalarHandler(String sql , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法query,传递结果集处理实现类ScalarHandler
        Object result = qr.query(con, sql, new ScalarHandler<Object>(), params);
        return result;
    }

    /*
     *  结果集第五种处理方法,ColumnListHandler
     *  结果集,指定列的数据,存储到List集合
     *  List<Object> 每个列数据类型不同
     */
    public static List<Object> listHandler(String sql, String column, Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法 query,传递结果集实现类ColumnListHandler
        //实现类构造方法中,使用字符串的列名
        List<Object> list = qr.query(con, sql, new ColumnListHandler<Object>(column), params);
        return list;
    }

    /*
     *  结果集第四种处理方法, BeanListHandler
     *  结果集每一行数据,封装JavaBean对象
     *  多个JavaBean对象,存储到List集合
     */
    public static <T> List<T> beanListHander(String sql , Class<T> clazz , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法query,传递结果集处理实现类BeanListHandler
        List<T> list = qr.query(con, sql, new BeanListHandler<T>(clazz),params);
        return list;
    }

    /*
     *  结果集第三种处理方法,BeanHandler
     *  将结果集的第一行数据,封装成JavaBean对象
     *  注意: 被封装成数据到JavaBean对象, Sort类必须有空参数构造
     */
    public static <T> T beanHandler(String sql , Class<T> clazz , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法,传递结果集实现类BeanHandler
        //BeanHandler(Class<T> type)
        T bean = qr.query(con, sql, new BeanHandler<T>(clazz),params);
        return bean;
    }

    /*
     *  结果集第二种处理方法,ArrayListHandler
     *  将结果集的每一行,封装到对象数组中, 出现很多对象数组
     *  对象数组存储到List集合
     */
    public static List<Object[]> arrayListHandler(String sql , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用query方法,结果集处理的参数上,传递实现类ArrayListHandler
        //方法返回值 每行是一个对象数组,存储到List
        List<Object[]> result=  qr.query(con, sql, new ArrayListHandler(),params);
        return result;
    }

    /*
     *  结果集第一种处理方法, ArrayHandler
     *  将结果集的第一行存储到对象数组中  Object[]
     */
    public static Object[] arrayHandler(String sql , Object... params)throws SQLException{
        QueryRunner qr = new QueryRunner();
        //调用方法query执行查询,传递连接对象,SQL语句,结果集处理方式的实现类
        //返回对象数组
        Object[] result = qr.query(con, sql, new ArrayHandler(),params);
        return result;
    }

}
