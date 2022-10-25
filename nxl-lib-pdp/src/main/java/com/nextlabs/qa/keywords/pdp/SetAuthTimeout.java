package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Set Auth Timeout",
        parameters = {"intValue"},
        description = "classpath:desc/SetAuthTimeout.txt"
)
public class SetAuthTimeout extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        pdpRequestHelper.setTimeout(Integer.valueOf(String.valueOf(objects[0])));
        return null;
    }

}
