package io.netty.example.http.websocketx.constant;

/**
 * SQL定义
 */
public class SqlDefine {

    /**
     * 查询用户信息
     * @return Map
     */
    public static final String SELECT_CONSUMER_BY_AUTHKEYCODE="SELECT " +
                                                                    "CONSUMER_ID, " +
                                                                    "`STATUS`, " +
                                                                    "EXPIRE_TIME " +
                                                                "FROM " +
                                                                    "t_consumer " +
                                                                "WHERE " +
                                                                    "AUTH_KEYCODE = ?";
    /**
     * 查询G01设备绑定行业信息
     * @return List
     */
    public static final String SELECT_G01_INDUSTRY="SELECT " +
                                                        "ii.`CODE` " +
                                                    "FROM " +
                                                        "t_consumer_ii ci, " +
                                                        "t_intelligence_industry ii " +
                                                    "WHERE " +
                                                        "ci.CONSUMER_ID = ? " +
                                                    "AND ci.INDUSTRY_ID = ii.ID ";
    /**
     * 查询用户与腾讯的情报绑定关系
     * @return Integer
     */
    public static final String SELECT_CONSUMER_TENCENT_RELATION="SELECT " +
                                                                    "soc.ID " +
                                                                "FROM " +
                                                                    "t_consumer_is cis, " +
                                                                    "t_intelligence_source soc " +
                                                                "WHERE " +
                                                                    "cis.CONSUMER_ID = ? " +
                                                                "AND cis.SOURCE_ID = soc.ID " +
                                                                "AND soc.ID = 2 " +
                                                                "AND soc.`STATUS` = 1 ";
    /**
     * 查询腾讯情报数据
     * @return List<Object>
     */
    public static final String SELECT_TENCENT_THREAT_DATA="SELECT " +
                                                                "DOMAIN_FLAG, " +
                                                                "TYPE, " +
                                                                "`NAME`, " +
                                                                "CONFIDENCE, " +
                                                                "`LEVEL`, " +
                                                                "ACTIVE_STATUE, " +
                                                                "TAG_LIST, " +
                                                                "THREAT_LIST, " +
                                                                "UPDATE_TIME " +
                                                            "FROM " +
                                                                "t_intelligence_tencent " +
                                                            "WHERE " +
                                                                "`STATUS` = 0 " +
                                                            "AND EXPIRE_TIME >= NOW( )";

}
