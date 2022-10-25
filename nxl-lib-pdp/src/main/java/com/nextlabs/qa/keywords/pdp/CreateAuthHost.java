package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Host",
        parameters = {"host"},
        description = "classpath:desc/CreateAuthHost.txt"
)
public class CreateAuthHost extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = pdpRequestHelper.createHost(String.valueOf(objects[0]));
        return refID;
    }

}
