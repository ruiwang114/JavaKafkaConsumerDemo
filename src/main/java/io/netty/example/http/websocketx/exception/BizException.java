package io.netty.example.http.websocketx.exception;

import java.text.MessageFormat;

/**
 * 业务异常类
 */
public class BizException extends Exception{

    // 错误码
    private int errorCode;
    // 展示给用户的错误信息
    private String errorMsg;
    // 展示给开发者的错误信息
    private String errorMsgForDev;

    private Throwable cause;


    public <T extends IResultMessage> BizException(T resultMsg, Object... args) {
        super("errorCode:" + resultMsg.getCode() + ", errorMsg:" + MessageFormat.format(resultMsg.getMsgForDev(), args));

        // 设置错误码
        this.errorCode = resultMsg.getCode();

        Object [] arguments = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            arguments[i] = String.valueOf(args[i]);
        }

        // 设置展示给用户的错误信息
        this.errorMsg = resultMsg.getMsg();

        if (args.length>0) {
            this.errorMsg = MessageFormat.format(resultMsg.getMsg(), arguments);
        }

        // 设置展示给开发者的错误信息
        this.errorMsgForDev = resultMsg.getMsgForDev();

        if (args.length>0) {
            this.errorMsgForDev = MessageFormat.format(resultMsg.getMsgForDev(), arguments);
        }
    }


    public <T extends IResultMessage> BizException(String resultMsg, Object... args) {

        Object [] arguments = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            arguments[i] = String.valueOf(args[i]);
        }

        // 设置展示给用户的错误信息
        this.errorMsg = resultMsg;

        if (args.length>0) {
            this.errorMsg = MessageFormat.format(resultMsg, arguments);
        }

        // 设置展示给开发者的错误信息
        this.errorMsgForDev = resultMsg;

        if (args.length>0) {
            this.errorMsgForDev = MessageFormat.format(resultMsg, arguments);
        }
    }



    public BizException() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getErrorMsgForDev() {
        return errorMsgForDev;
    }

    public BizException(Throwable cause) {
        super(cause);
        this.cause = cause;
    }
}
