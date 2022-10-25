package com.nextlabs.qa.keywords.pdp.parser;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * Created by sduan on 17/12/2015.
 */
public class ResponseTypeParser {

    private Unmarshaller unmarshaller;

    public void init() throws Exception {
        try {
            synchronized (this) {
                JAXBContext jaxbContext = JAXBContext
                        .newInstance(ResponseType.class);
                unmarshaller = jaxbContext.createUnmarshaller();
            }
        }
        catch (Exception e) {
            throw new Exception("Error occurred while initilizing ResponseTypeParser, ", e);
        }
    }

    public ResponseType parse(String stringResponse) throws Exception {
        if(this.unmarshaller == null) {
            this.init();
        }
        StringReader stringReader = new StringReader(stringResponse);
        ResponseType responseType = null;
        try {
            responseType = (ResponseType) JAXBIntrospector.getValue(unmarshaller.unmarshal(stringReader));
        } catch (JAXBException e) {
            throw new Exception("error parsing xml response, " + e.getMessage());
        }
        return responseType;
    }
}
