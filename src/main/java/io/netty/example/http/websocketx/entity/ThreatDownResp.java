package io.netty.example.http.websocketx.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class ThreatDownResp implements Serializable {

    /**
     * 是否全量：0全量 1增量
     */
    private Integer is_global;
    /**
     * 最新的offset
     */
    private Integer new_offset;
    /**
     * G01聚合情报数据
     */
    private List<G01ThreatInfoResp> G01;
    /**
     * Tencent聚合情报数据
     */
    private List<T01ThreatInfoResp> T01;

}
