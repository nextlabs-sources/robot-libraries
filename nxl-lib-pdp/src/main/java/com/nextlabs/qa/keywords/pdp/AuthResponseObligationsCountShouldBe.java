package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 17/12/2015.
 */
@Component
@KeywordInfo(
        name = "Auth Response Obligations Count Should Be",
        parameters = {"resultRef", "count"},
        description = "classpath:desc/AuthResponseObligationsCountShouldBe.txt"
)
public class AuthResponseObligationsCountShouldBe extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] params) throws Exception {
        pdpRequestHelper.authResponseObligationsCountShouldBe(
                String.valueOf(params[0]),
                Integer.parseInt(String.valueOf(params[1])));
        return null;
    }

}
