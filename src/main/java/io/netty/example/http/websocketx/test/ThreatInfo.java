package io.netty.example.http.websocketx.test;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class ThreatInfo {

    private String operation;

    private String ip;

    private String inFold;

    private String industryCode;

    private String startTime;

    private String endTime;

    private String total;

    private String score;

    private String dstCompany;

    private String srcCountry;

    private String srcProvince;

    private String srcCity;

    public ThreatInfo(){

    }

    public ThreatInfo(ThreatInfo threatInfo){
        operation=threatInfo.getOperation();
        ip=threatInfo.getIp();
        inFold=threatInfo.getInFold();
        industryCode=threatInfo.getIndustryCode();
        startTime=threatInfo.getStartTime();
        endTime=threatInfo.getEndTime();
        total=threatInfo.getTotal();
        score=threatInfo.getScore();
        dstCompany=threatInfo.getDstCompany();
        srcCountry=threatInfo.getSrcCountry();
        srcProvince=threatInfo.getSrcProvince();
        srcCity=threatInfo.getSrcCity();
    }

//    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
//    private Date createTime;
//
//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
}
