package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Create Auth Policy On Demand",
        parameters = {"pql", "ignoreBuildInPolicies"},
        description = "classpath:desc/CreateAuthPolicyOnDemand.txt"
)
public class CreateAuthPolicyOnDemand extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        String refID = pdpRequestHelper.createPolicyOnDemand(
                String.valueOf(objects[0]),
                Boolean.valueOf(String.valueOf(objects[1]))
        );
        return refID;
    }

}
