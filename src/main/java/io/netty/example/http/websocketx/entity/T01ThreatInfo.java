package io.netty.example.http.websocketx.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * T01威胁情报数据
 */
@Data
public class T01ThreatInfo implements Serializable {

    /**
     * 是否匹配子域名：0：不匹配，1：匹配
     */
    private Integer DOMAIN_FLAG;

    /**
     * 情报类型：“ip”、“domain”
     */
    private String TYPE;

    /**
     * 反弹ip/domain
     */
    private String NAME;

    /**
     * 置信度
     */
    private Integer CONFIDENCE;

    /**
     * 安全等级
     */
    private Integer LEVEL;

    /**
     * 活跃状态
     */
    private String ACTIVE_STATUE;

    /**
     * 威胁列表
     */
    private String THREAT_LIST;

    /**
     * 恶意标签列表
     */
    private String TAG_LIST;

    /**
     * 最后发现时间
     */
    private Date UPDATE_TIME;

}
