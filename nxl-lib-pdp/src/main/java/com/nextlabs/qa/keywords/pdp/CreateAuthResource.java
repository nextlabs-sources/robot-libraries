package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Resource",
        parameters = {"name", "type", "dimension"},
        description = "classpath:desc/CreateAuthResource.txt"
)
public class CreateAuthResource extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = pdpRequestHelper.createResource(
                String.valueOf(objects[0]),
                String.valueOf(objects[1]),
                String.valueOf(objects[2])
        );
        return refID;
    }

}
