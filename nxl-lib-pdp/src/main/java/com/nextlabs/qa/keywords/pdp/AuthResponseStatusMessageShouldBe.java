package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 18/12/2015.
 */
@Component
@KeywordInfo(
        name = "Auth Response StatusMessage Should Be",
        parameters = {"resultRef", "value"},
        description = "classpath:desc/AuthResponseStatusMessageShouldBe.txt"
)
public class AuthResponseStatusMessageShouldBe extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] params) throws Exception {
        pdpRequestHelper.authResponseStatusMessageShouldBe(
                String.valueOf(params[0]),
                String.valueOf(params[1])
        );
        return null;
    }

}
