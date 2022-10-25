package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Set Noise Level",
        parameters = {"value"},
        description = "classpath:desc/SetNoiseLevel.txt"
)
public class SetNoiseLevel extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        pdpRequestHelper.setNoiseLevel(Integer.valueOf(String.valueOf(objects[0])));
        return null;
    }

}
