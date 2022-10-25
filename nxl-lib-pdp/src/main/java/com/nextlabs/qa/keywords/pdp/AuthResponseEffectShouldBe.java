package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 15/12/2015.
 */
@Component
@KeywordInfo(
        name = "Auth Response Effect Should Be",
        parameters = {"resultRef", "effect"},
        description = "classpath:desc/AuthResponseEffectShouldBe.txt"
)
public class AuthResponseEffectShouldBe extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        pdpRequestHelper.authResponseEffectShouldBe(String.valueOf(objects[0]), String.valueOf(objects[1]));
        return null;
    }

}
