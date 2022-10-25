package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Additional Data",
        parameters = {"name"},
        description = "classpath:desc/CreateAuthAdditionalData.txt"
)
public class CreateAuthAdditionalData extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = pdpRequestHelper.createAdditionalData(String.valueOf(objects[0]));
        return refID;
    }

}
