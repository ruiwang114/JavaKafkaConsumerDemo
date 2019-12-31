package io.netty.example.http.websocketx.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 威胁情报数据
 */
@Data
@Accessors(chain = true)
public class ThreatInfo {

    private String ip;

    private String infoId;
    /**
     * 操作：+ - =
     */
    private String operation;

    /**
     * 行业码
     */
    private String industryCode;

    private String startTime;

    private String endTime;

    private String total;

    private String score;

    private String dstCompany;

    private String srcCountry;

    private String srcProvince;

    private String srcCity;
}
