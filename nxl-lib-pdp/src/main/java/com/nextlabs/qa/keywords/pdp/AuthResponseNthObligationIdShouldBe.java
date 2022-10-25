package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 18/12/2015.
 */
@Component
@KeywordInfo(
        name = "Auth Response Nth Obligation Id Should Be",
        parameters = {"resultRef", "index", "value"},
        description = "classpath:desc/AuthResponseNthObligationIdShouldBe.txt"
)
public class AuthResponseNthObligationIdShouldBe extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] params) throws Exception {
        pdpRequestHelper.authResponseNthObligationIdShouldBe(
                String.valueOf(params[0]),
                Integer.parseInt(String.valueOf(params[1])),
                String.valueOf(params[2])
        );
        return null;
    }

}
