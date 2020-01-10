package io.netty.example.http.websocketx.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * G01威胁情报数据
 */
@Data
@Accessors(chain = true)
public class G01ThreatInfo  {

    private String ip;

    private Long infoId;
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

    private Long total;

    private Long score;

    private String srcCountry;

    private String srcProvince;

    private String srcCity;
}
