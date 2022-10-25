package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Action",
        parameters = {"action"},
        description = "classpath:desc/CreateAuthAction.txt"
)
public class CreateAuthAction extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = pdpRequestHelper.createAction(String.valueOf(objects[0]));
        return refID;
    }

}
