package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Set Perform Obligations",
        parameters = {"bool"},
        description = "classpath:desc/SetPerformObligations.txt"
)
public class SetPerformObligations extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        pdpRequestHelper.setPerformObligations(Boolean.valueOf(String.valueOf(objects[0])));
        return null;
    }

}
