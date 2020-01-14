package io.netty.example.http.websocketx.entity;

import lombok.Data;

/**
 * 情报下载请求信息
 */
@Data
public class ThreatDownReq {

    private Integer offset;
    private String serial_num;
}
