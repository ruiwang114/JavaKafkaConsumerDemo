package io.netty.example.http.websocketx.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultData implements Serializable {

    /**
     * 0-发生异常，1-连接正常
     */
    private Integer status=1;
    private Object msg=null;
}
