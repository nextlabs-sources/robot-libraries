package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Application",
        parameters = {"name", "*pid"},
        description = "classpath:desc/CreateAuthApplication.txt"
)
public class CreateAuthApplication extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = null;
        if(objects.length == 1) {
            refID = pdpRequestHelper.createApplication(String.valueOf(objects[0]));
        } else if(objects.length >= 2) {
            refID = pdpRequestHelper.createApplication(String.valueOf(objects[0]),
                    Long.parseLong(String.valueOf(objects[1])));
        } else {
            throw new IllegalArgumentException("wrong number of arguments");
        }
        return refID;
    }

}
