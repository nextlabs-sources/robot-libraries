package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Get Request JSON String",
        parameters = {"requestRef"},
        description = "classpath:desc/GetRequestJSONString.txt"
)
public class GetRequestJSONString extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {

        String outputString = pdpRequestHelper.serializeRequest(String.valueOf(objects[0]),
                PDPRequestHelper.SERIALIZE_FORMAT_JSON);
        return outputString;
    }

}
