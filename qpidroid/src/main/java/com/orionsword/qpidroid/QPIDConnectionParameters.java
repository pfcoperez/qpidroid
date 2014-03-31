package com.orionsword.qpidroid;

import java.util.Map;

/**
 * Created by pablo on 24/03/14.
 */
public class QPIDConnectionParameters  implements IConnectionParameters {

    public QPIDConnectionParameters(String srvAddr, int port, String exchange, String subject) {
        strParams.put("server",srvAddr);
        iParams.put("port", port);
        strParams.put("exchange", exchange);
        strParams.put("subject",subject);
    }

    @Override
    public String getStringParameter(String name) {
        return strParams.get(name);
    }

    @Override
    public Integer getIntegerParameter(String name) {
        return iParams.get(name);
    }

    protected Map<String,String> strParams;
    protected Map<String,Integer> iParams;


}
