package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth User",
        parameters = {"id", "*name"},
        description = "classpath:desc/CreateAuthUser.txt"
)
public class CreateAuthUser extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = null;
        if(objects.length > 1) {
            refID = pdpRequestHelper.createUser(String.valueOf(objects[0]), String.valueOf(objects[1]));
        } else {
            refID = pdpRequestHelper.createUser(String.valueOf(objects[0]), null);
        }
        return refID;
    }

}
