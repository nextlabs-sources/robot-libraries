package com.nextlabs.qa.keywords.pdp.serializer;

import com.att.research.xacml.api.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sduan on 14/12/2015.
 */
public class XMLRequestSerializer implements RequestSerializer {

    private static final Log log = LogFactory.getLog(XMLRequestSerializer.class);

    private Marshaller marshaller = null;
    private ObjectFactory objectFactory = null;

    public void init() throws Exception {
        try {
            synchronized (this) {
//                JAXBContext jc = JAXBContext.newInstance(RequestType.class.getPackage().getName());
                // see: http://stackoverflow.com/questions/6963996/can-i-replace-jaxb-properties-with-code
                JAXBContext jc = JAXBContextFactory.createContext(new Class[] { RequestType.class }, null);

                // see: http://wiki.eclipse.org/EclipseLink/Release/2.4.0/JAXB_RI_Extensions/Namespace_Prefix_Mapper
                Map<String, String> urisToPrefixes = new HashMap<>();
                urisToPrefixes.put("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", "");

                marshaller = jc.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, urisToPrefixes);

                objectFactory = new ObjectFactory();
                log.debug("XMLXACMLSerializer initialized successfully");
            }
        } catch (Exception e) {
            throw new Exception("Error occurred while initilizing XMLXACMLSerializer, ", e);
        }
    }

    @Override
    public String serializeData(Request request) throws Exception {
        if(marshaller == null || objectFactory == null) {
            init();
        }
        RequestType requestType = RequestTypeUtil.convert(request);
        JAXBElement<RequestType> requestTypeJAXBElement =
                objectFactory.createRequest(requestType);
        StringWriter writer = new StringWriter();
        marshaller.marshal(requestTypeJAXBElement, writer);
        String responseString = writer.toString();
        return responseString;
    }
}
