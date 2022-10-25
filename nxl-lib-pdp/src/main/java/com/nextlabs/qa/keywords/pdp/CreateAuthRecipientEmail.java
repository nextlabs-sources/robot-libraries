package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Recipient Email",
        parameters = {"email"},
        description = "classpath:desc/CreateAuthRecipientEmail.txt"
)
public class CreateAuthRecipientEmail extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {

        String refID = pdpRequestHelper.createRecipient(String.valueOf(objects[0]));
        return refID;

    }

}
