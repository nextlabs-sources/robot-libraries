package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Invoke Auth RMI Request",
        parameters = {"requestRef"},
        description = "classpath:desc/InvokeAuthRMIRequest.txt"
)
public class InvokeAuthRMIRequest extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        List<String> results = pdpRequestHelper.invokeAuthRMIRequest(String.valueOf(objects[0]));
        return results;
    }

}
