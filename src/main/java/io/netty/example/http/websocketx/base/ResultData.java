package io.netty.example.http.websocketx.base;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一接口返回包装类
 */
@Data
public class ResultData implements Serializable {

    /**
     * false-发生异常，true-连接正常
     */
    private boolean status=true;
    /**
     * 提示
     */
    private String hint="成功";
    /**
     * 包装数据
     */
    private Object msg=null;
}
