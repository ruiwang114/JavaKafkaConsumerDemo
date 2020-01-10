package io.netty.example.http.websocketx.exception;

public interface IResultMessage {
    /**
     * 获取错误码
     *
     * @return 错误码
     */
    int getCode();

    /**
     * 获取展示给客户的错误信息
     *
     * @return 错误信息
     */
    String getMsg();

    /**
     * 获取展示给开发者的错误信息
     *
     * @return 错误信息
     */
    String getMsgForDev();
}
