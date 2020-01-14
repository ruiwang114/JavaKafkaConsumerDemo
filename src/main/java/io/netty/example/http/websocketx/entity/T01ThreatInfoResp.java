package io.netty.example.http.websocketx.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * T01威胁情报数据
 */
@Data
@Accessors(chain = true)
public class T01ThreatInfoResp implements Serializable {

    /**
     * 操作 “+” 新增(目前全部返回+)
     */
    private String operation="+";

    /**
     * 是否匹配子域名：0：不匹配，1：匹配
     */
    private Integer subFlag=0;

    /**
     * 情报类型：“ip”、“domain”
     */
    private String infoType;

    /**
     * 反弹ip/domain
     */
    private String infoName;

    /**
     * 置信度
     */
    private Integer confidence=0;

    /**
     * 安全等级
     */
    private Integer level=0;

    /**
     * 活跃状态
     */
    private String status;

    /**
     * 威胁列表
     */
    private List<String> threatList;

    /**
     * 恶意标签列表
     */
    private List<String> tagList;

    /**
     * 最后发现时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
