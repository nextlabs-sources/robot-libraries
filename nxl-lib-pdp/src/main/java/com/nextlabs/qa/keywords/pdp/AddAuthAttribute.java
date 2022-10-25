package com.nextlabs.qa.keywords.pdp;

import org.jspringbot.KeywordInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by sduan on 14/12/2015.
 */
@Component
@KeywordInfo(
        name = "Add Auth Attribute",
        parameters = {"entity", "key", "*value"},
        description = "classpath:desc/AddAuthAttribute.txt"
)
public class AddAuthAttribute extends AbstractPDPRequestKeyword {

    @Override
    public Object execute(Object[] objects) throws Exception {
        if(objects.length < 3) {
            throw new IllegalStateException("To add attribute, at least 3 values should be specified");
        }
        Collection<Object> values = new ArrayList<>();
        values.addAll(Arrays.asList(objects).subList(2, objects.length));

        for(Object value: values) {
            pdpRequestHelper.addAttribute(
                    String.valueOf(objects[0]),
                    String.valueOf(objects[1]),
                    String.valueOf(value)
            );
        }
        return null;
    }

}
