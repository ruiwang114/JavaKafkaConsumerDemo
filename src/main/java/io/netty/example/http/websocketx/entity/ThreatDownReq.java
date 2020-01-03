package io.netty.example.http.websocketx.entity;

import lombok.Data;

@Data
public class ThreatDownReq {

    private String offset;
    private String serial_num;
}
