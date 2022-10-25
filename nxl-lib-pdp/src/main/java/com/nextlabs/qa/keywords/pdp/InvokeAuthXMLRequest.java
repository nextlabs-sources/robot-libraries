package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by sduan on 15/12/2015.
 */
@Component
@KeywordInfo(
        name = "Invoke Auth XML Request",
        parameters = {"requestRef"},
        description = "classpath:desc/InvokeAuthXMLRequest.txt"
)
public class InvokeAuthXMLRequest extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        List<String> results = pdpRequestHelper.invokeAuthXMLRequest(String.valueOf(objects[0]));
        return results;
    }

}
