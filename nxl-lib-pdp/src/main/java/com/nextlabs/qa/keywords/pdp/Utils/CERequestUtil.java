package com.nextlabs.qa.keywords.pdp.Utils;

import com.bluejungle.destiny.agent.pdpapi.IPDPApplication;
import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.destiny.agent.pdpapi.IPDPResource;
import com.bluejungle.destiny.agent.pdpapi.IPDPUser;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.nextlabs.destiny.sdk.*;
import com.nextlabs.qa.keywords.pdp.PDPRequestHelper;
import com.nextlabs.qa.keywords.pdp.beans.PDPRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sduan on 15/12/2015.
 */
public class CERequestUtil {

    public static CERequest convertPDPRequest(PDPRequest pdpRequest) {
        // action
        String action = pdpRequest.getAction();
        // user
        CEUser user = getUser(pdpRequest.getUser());
        CEAttributes userAttributes = getAttributes(pdpRequest.getUser(),
                Arrays.asList(PDPRequest.ATTR_USER_ID, PDPRequest.ATTR_USER_NAME));
        // application
        CEApplication app = getApplication(pdpRequest.getApplication());
        CEAttributes appAttributes = getAttributes(pdpRequest.getApplication(),
                Arrays.asList(PDPRequest.ATTR_APPLICATION_NAME));
        // host is not in CERequest
        // resources (from and to)
        CEResource source = null;
        CEResource dest = null;
        CEAttributes sourceAttributes = null;
        CEAttributes destAttributes = null;

        for(IPDPResource res: pdpRequest.getResourceArr()) {
            if(res.getName().equals(PDPRequest.KEY_RESOURCE_NAME_FROM)) {
                source = getResource(res);
                sourceAttributes = getAttributes(res,
                        Arrays.asList(PDPRequest.ATTR_RESOURCE_ID, PDPRequest.ATTR_RESOURCE_TYPE));
            } else if(res.getName().equals(PDPRequest.KEY_RESOURCE_NAME_TO)) {
                dest = getResource(res);
                destAttributes = getAttributes(res,
                        Arrays.asList(PDPRequest.ATTR_RESOURCE_ID, PDPRequest.ATTR_RESOURCE_TYPE));
            }

        }

        if(source == null) {
            throw new IllegalArgumentException("The PDPRequest doesn't have a source resource");
        }

        // recipient, policyOnDemand and additional data
        String[] recipients = null;
        CENamedAttributes[] additionalAttrs = null;
        if(pdpRequest.getAdditionalData() != null) {
            for (IPDPNamedAttributes namedAttrs : pdpRequest.getAdditionalData()) {
                if (namedAttrs.getName().equals(PDPRequest.KEY_RECIPIENT_NAME)) {
                    // recipient
                    String recipient = namedAttrs.getValue(PDPRequest.ATTR_RECIPIENT_EMAIL);
                    if (recipients == null || recipients.length == 0) {
                        recipients = new String[]{recipient};
                    } else {
                        String[] newRecipients = new String[recipients.length + 1];
                        System.arraycopy(recipients, 0, newRecipients, 0, recipients.length);
                        newRecipients[newRecipients.length-1] = recipient;
                        recipients = newRecipients;
                    }
                } else {
                    // policyOnDemand and others
                    CENamedAttributes ceNamedAttrs = new CENamedAttributes(namedAttrs.getName());
                    // a little redundant...
                    Map<String, DynamicAttributes> attribMap = new HashMap<>();
                    namedAttrs.addSelfToMap(attribMap);
                    DynamicAttributes attributes = attribMap.get(namedAttrs.getName());
                    if (attributes != null) {
                        for (String key : attributes.keySet()) {
                            String[] values = attributes.getStrings(key);
                            for (String value : values) {
                                if (value != null) {
                                    ceNamedAttrs.add(key, value);
                                }
                            }
                        }
                    }

                    if (additionalAttrs == null || additionalAttrs.length == 0) {
                        additionalAttrs = new CENamedAttributes[]{ceNamedAttrs};
                    } else {
                        CENamedAttributes[] newAdditionalAttrs = new CENamedAttributes[additionalAttrs.length + 1];
                        System.arraycopy(additionalAttrs, 0, newAdditionalAttrs, 0, additionalAttrs.length);
                        newAdditionalAttrs[newAdditionalAttrs.length-1] = ceNamedAttrs;
                        additionalAttrs = newAdditionalAttrs;
                    }
                }
            }
        }

        // performObligations is not in PDPRequest
        // noiseLevel is not in PDPRequest
        // use default values for now

        return new CERequest(action,source, sourceAttributes, dest, destAttributes, user, userAttributes,
                app, appAttributes, recipients, additionalAttrs, true,
                ICESdk.CE_NOISE_LEVEL_USER_ACTION);
    }

    private static CEUser getUser(IPDPUser user) {
        String userId = user.getValue(PDPRequest.ATTR_USER_ID);
        String userName = user.getValue(PDPRequest.ATTR_USER_NAME);
        if (userName == null) {
            userName = userId;
        }
        CEUser ceUser = new CEUser(userId, userName);
        return ceUser;
    }

    private static CEResource getResource(IPDPResource res) {
        String resId = res.getValue(PDPRequest.ATTR_RESOURCE_ID);
        String resType = res.getValue(PDPRequest.ATTR_RESOURCE_TYPE);
        CEResource ceRes = new CEResource(resId, resType);
        return ceRes;
    }

    private static CEApplication getApplication(IPDPApplication application) {
        String appName = application.getValue(PDPRequest.ATTR_APPLICATION_NAME);
        CEApplication app = new CEApplication(appName, null);
        return app;
    }

    private static CEAttributes getAttributes(IPDPNamedAttributes pdpNamedAttributes, List<String> keysToSkip) {
        CEAttributes ceAttributes = null;
        Map<String, DynamicAttributes> attribMap = new HashMap<>();
        pdpNamedAttributes.addSelfToMap(attribMap);
        DynamicAttributes attributes = attribMap.get(pdpNamedAttributes.getName());

        if(attributes == null) {
            return null;
        } else {
            ceAttributes = new CEAttributes();
            for(String key: attributes.keySet()) {
                if(!keysToSkip.contains(key)) {
                    String[] values = attributes.getStrings(key);
                    for (String value : values) {
                        if (value != null) {
                            ceAttributes.add(key, value);
                        }
                    }
                }
            }
        }

        return ceAttributes;
    }

}
