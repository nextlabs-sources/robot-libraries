package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Assemble MultiRequest",
        parameters = {"*requestRefs"},
        description = "classpath:desc/AssembleMultiRequest.txt"
)
public class AssembleMultiRequest extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        if(objects.length < 2) {
            throw new IllegalStateException("To assemble a multi request, at least two request refs are needed");
        }

        String refID = pdpRequestHelper.assembleMultiRequest(Arrays.copyOf(objects, objects.length, String[].class));
        return refID;
    }

}
