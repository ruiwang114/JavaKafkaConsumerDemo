package io.netty.example.http.websocketx.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class jsonDeal {

    public static void main(String[] args) {
        String json = "";
        List<HashMap> list =JSON.parseArray(json, HashMap.class);
        for(int i=0;i<list.size();i++){
            Map map = new HashMap();
            map=list.get(i);
            JSONObject jsonobj = new JSONObject(map);
            if(jsonobj.containsKey("company")){

            }else{
                jsonobj.put("company","");
            }
            if(!jsonobj.containsKey("cat_tags")){
                jsonobj.put("cat_tags",new JSONArray());
            }
            if(!jsonobj.containsKey("rule_tags")){
                jsonobj.put("rule_tags",new JSONArray());
            }
//            System.out.println(jsonobj);
            Set<String> keySet= jsonobj.keySet();
            keySet.remove("protocol_list");
            keySet.remove("domain_list");
            keySet.remove("host_list");
            keySet.remove("uid");
            keySet.remove("user");
            keySet.remove("port_c");
            keySet.remove("state");
            keySet.remove("vlan");
            keySet.remove("net_bios");
            keySet.remove("mac");
            keySet.remove("name");
            keySet.remove("descriptions");
            keySet.remove("city");
            keySet.remove("province");
            keySet.remove("appserver");
            keySet.remove("business_app");
            keySet.remove("assets_id");
            keySet.remove("domain");
            keySet.remove("lastchecktime");
            keySet.remove("lastupdatetime");
            keySet.remove("company_tags");
            keySet.remove("on_line");
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
