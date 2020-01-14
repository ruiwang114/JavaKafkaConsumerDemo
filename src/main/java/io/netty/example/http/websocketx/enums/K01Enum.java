package io.netty.example.http.websocketx.enums;

import lombok.Data;

/**
 * 系统枚举聚合类
 */
public class K01Enum {

    /**
     * SQL字段枚举
     */
    public enum SqlFields{
        CONSUMER_ID,
        STATUS,
        EXPIRE_TIME
    }

    /**
     * Redis缓存解析字段枚举
     */
    public enum CacheFields{
        global_offset,
        global,
        offset
    }

    /**
     * 数据解析用到的字符枚举
     */
    public enum SpecialCharacters{

        UPRIGHT_LINE_TRAN("\\|"),
        UPRIGHT_LINE("|");

        String value;

        SpecialCharacters(String value){
            this.value=value;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
}
