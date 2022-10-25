package com.nextlabs.qa.keywords.pdp.serializer;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.std.json.JSONRequest;

/**
 * Created by sduan on 14/12/2015.
 */
public class JSONRequestSerializer implements RequestSerializer {

    @Override
    public String serializeData(Request request) throws Exception {
        return JSONRequest.toString(request, true);
    }
}
