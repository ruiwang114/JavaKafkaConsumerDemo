package io.netty.example.http.websocketx.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * G01威胁情报数据
 */
@Builder
@Data
public class G01ThreatInfoResp implements Serializable  {

    private String ip;

    private Long infoId;
    /**
     * 操作：+ - =
     */
    private String operation;

    /**
     * 行业码
     */
    private List<String> industryCode;

    private String startTime;

    private String endTime;

    private Long total;

    private Long score;

    private String srcCountry;

    private String srcProvince;

    private String srcCity;
}
