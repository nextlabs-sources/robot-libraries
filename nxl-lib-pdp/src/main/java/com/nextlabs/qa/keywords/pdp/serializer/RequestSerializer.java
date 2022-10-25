package com.nextlabs.qa.keywords.pdp.serializer;

import com.att.research.xacml.api.Request;

/**
 * Created by sduan on 14/12/2015.
 */
public interface RequestSerializer {
    String serializeData(Request request) throws Exception;
}
