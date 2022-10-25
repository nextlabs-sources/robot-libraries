package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

@Component
@KeywordInfo(
        name = "Start Auth Request",
        description = "classpath:desc/StartAuthRequest.txt"
)
public class StartAuthRequest extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] params) {
        pdpRequestHelper.reset();
        return null;
    }
}
