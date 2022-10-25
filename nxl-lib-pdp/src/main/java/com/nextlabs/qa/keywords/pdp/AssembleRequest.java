package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Assemble Request",
        parameters = {"*entities"},
        description = "classpath:desc/AssembleRequest.txt"
)
public class AssembleRequest extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {

        if(objects.length < 3) {
            throw new IllegalStateException("the entities specified is not enough to assemble a request");
        }

        String refID = pdpRequestHelper.assembleRequest(Arrays.copyOf(objects, objects.length, String[].class));
        return refID;
    }

}
