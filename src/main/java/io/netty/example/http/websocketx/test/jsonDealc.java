package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class jsonDealc {

    public static void main(String[] args) {
       String json="";

        String str="";
        JSONArray array=JSONArray.parseArray(str);
        List<HashMap> list =JSON.parseArray(json, HashMap.class);
        for(int i=0;i<list.size();i++){
            Map map = new HashMap();
            map=list.get(i);
            JSONObject jsonobj = new JSONObject(map);
            jsonobj.put("userAndSettings",array);
            jsonobj.put("machineName","");

//            System.out.println(jsonobj);

            System.out.println(jsonobj+",");
//            Iterator iter = map.keySet().iterator();
//            while (iter.hasNext()) {
//                Object key = iter.next();
////                Object val = map.get(key);
//                System.out.println(key);
//            }
        }
        System.out.println();
    }
}
