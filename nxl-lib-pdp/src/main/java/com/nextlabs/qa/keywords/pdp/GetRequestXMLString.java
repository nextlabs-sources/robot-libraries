package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Get Request XML String",
        parameters = {"requestRef"},
        description = "classpath:desc/GetRequestXMLString.txt"
)
public class GetRequestXMLString extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {

        String outputString = pdpRequestHelper.serializeRequest(String.valueOf(objects[0]),
                PDPRequestHelper.SERIALIZE_FORMAT_XML);
        return outputString;
    }

}
