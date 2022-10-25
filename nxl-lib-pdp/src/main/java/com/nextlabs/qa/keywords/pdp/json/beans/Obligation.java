package com.nextlabs.qa.keywords.pdp.json.beans;

import java.util.List;

/**
 * Created by sduan on 17/12/2015.
 */
public class Obligation {
    String Id;
    List<AttributeAssignment> AttributeAssignment;

    public List<AttributeAssignment> getAttributeAssignment() {
        return AttributeAssignment;
    }

    public void setAttributeAssignment(List<AttributeAssignment> attributeAssignment) {
        AttributeAssignment = attributeAssignment;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
