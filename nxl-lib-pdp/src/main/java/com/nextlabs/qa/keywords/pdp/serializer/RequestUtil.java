package com.nextlabs.qa.keywords.pdp.serializer;

import com.att.research.xacml.api.*;
import com.att.research.xacml.std.*;
import com.att.research.xacml.std.datatypes.DataTypes;
import com.bluejungle.destiny.agent.pdpapi.*;
import com.bluejungle.framework.utils.DynamicAttributes;
import com.google.common.net.InetAddresses;
import com.nextlabs.qa.keywords.pdp.beans.PDPNamedAttributesWrapper;
import com.nextlabs.qa.keywords.pdp.beans.PDPRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class RequestUtil {

    private PDPRequest pdpRequest;

    /*
     * To check the individual data attributes for being the correct type, we need an instance of the DataTypeFactory
     */
    private static DataTypeFactory dataTypeFactory = null;

    public static final String CATEGORY_APPLICATION = "urn:nextlabs:names:evalsvc:1.0:attribute-category:application";
    public static final String ATTRIBUTE_APPLICATION_ID = "urn:nextlabs:names:evalsvc:1.0:application:application-id";
    public static final String ATTRIBUTE_RESOURCE_TYPE = "urn:nextlabs:names:evalsvc:1.0:resource:resource-type";
    public static final String ATTRIBUTE_RESOURCE_DIMENSION = "urn:nextlabs:names:evalsvc:1.0:resource:resource-dimension";
    public static final String CATEGORY_HOST = "urn:nextlabs:names:evalsvc:1.0:attribute-category:host";
    public static final String ATTRIBUTE_HOST_NAME = "urn:nextlabs:names:evalsvc:1.0:host:name";
    public static final String ATTRIBUTE_HOST_INET_ADDR = "urn:nextlabs:names:evalsvc:1.0:host:inet_address";
    public static final String ATTRIBUTE_RECIPIENT_EMAIL = "urn:nextlabs:names:evalsvc:1.0:recipient:email";
    public static final String CATEGORY_POLICY_ON_DEMAND = "urn:nextlabs:names:evalsvc:1.0:attribute-category:pod";
    public static final String ATTRIBUTE_POD_ID = "urn:nextlabs:names:evalsvc:1.0:pod:pod-id";
    public static final String ATTRIBUTE_POD_IGNORE_BUILT_IN = "urn:nextlabs:names:evalsvc:1.0:pod:pod-ignore-built-in";

    /**
     * Convert full AttributeId to short one used by underline PDP code
     * Also make the key a valid URI (replace space with %20)
     *
     * @param qualifiedKey
     * @return
     */
    private static String convertToPDPKey(String qualifiedKey) {
        // TODO
        try {
            qualifiedKey = java.net.URLEncoder.encode(qualifiedKey, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {}
        return qualifiedKey;
    }

    /**
     * Convert short keys used by underline PDP code to full AttributeId
     * @param pdpKey
     * @return
     */
    private static String convertToQualifiedKey(Identifier categoryID, String pdpKey) {
        // TODO
        pdpKey = pdpKey.replaceAll(" ","%20");
//        try {
//            pdpKey = URLEncoder.encode(pdpKey, "UTF-8");
//        } catch (UnsupportedEncodingException e) {}

        return pdpKey;
    }

    public static Request convertRequest(List<PDPRequest> requestList) throws Exception {

        if (dataTypeFactory == null) {
            dataTypeFactory = DataTypeFactory.newInstance("com.att.research.xacml.std.StdDataTypeFactory");
        }

        if(requestList == null || requestList.size() == 0) {
            return null;
        }

        // gather all categories of the requestList
        Map<String, String> actionMap = new HashMap<>();
        Map<String, PDPNamedAttributesWrapper> userMap = new HashMap<>();
        Map<String, PDPNamedAttributesWrapper> hostMap = new HashMap<>();
        Map<String, PDPNamedAttributesWrapper> applicationMap = new HashMap<>();
        Map<String, PDPNamedAttributesWrapper> resourceMap = new HashMap<>();
        Map<String, PDPNamedAttributesWrapper> additionalDataMap = new HashMap<>();

        RefIDGenerator idGen = new RefIDGenerator();

        List<PDPRequestRef> requestRefList = new ArrayList<>(requestList.size());

        for(PDPRequest pdpReq: requestList) {
            PDPRequestRef reqRef = new PDPRequestRef();

            // first is action
            String action = pdpReq.getAction();
            String actionID = assembleRequestPart(actionMap, action, idGen, RefIDGenerator.ACTION);
            reqRef.setActionRef(actionID);

            // second is user
            IPDPUser user = pdpReq.getUser();
            PDPNamedAttributesWrapper userWrapper = new PDPNamedAttributesWrapper(user);
            String userID = assembleRequestPart(userMap, userWrapper, idGen, RefIDGenerator.USER);
            reqRef.setUserRef(userID);

            // then host, which may be null
            IPDPHost host = pdpReq.getHost();
            if(host != null) {
                PDPNamedAttributesWrapper hostWrapper = new PDPNamedAttributesWrapper(host);
                String hostID = assembleRequestPart(hostMap, hostWrapper, idGen, RefIDGenerator.HOST);
                reqRef.setHostRef(hostID);
            }

            // then application, which may be null
            IPDPApplication application = pdpReq.getApplication();
            if(application != null) {
                PDPNamedAttributesWrapper applicationWrapper = new PDPNamedAttributesWrapper(application);
                String applicationID = assembleRequestPart(applicationMap, applicationWrapper, idGen, RefIDGenerator.APPLICATION);
                reqRef.setApplicationRef(applicationID);
            }

            // then resources, which should not be null
            // TODO add the validation
            IPDPResource[] resourceArr = pdpReq.getResourceArr();
            String[] resourceRef = new String[resourceArr.length];
            for(int i=0; i <resourceArr.length; i++) {
                PDPNamedAttributesWrapper resourceWrapper = new PDPNamedAttributesWrapper(resourceArr[i]);
                String resourceID = assembleRequestPart(resourceMap, resourceWrapper, idGen, RefIDGenerator.RESOURCE);
                resourceRef[i] = resourceID;
            }
            reqRef.setResourceRef(resourceRef);

            // then additionalDataRef, which can be null
            IPDPNamedAttributes[] additionalData = pdpReq.getAdditionalData();
            if(additionalData != null && additionalData.length != 0) {
                String[] additionalDataRef = new String[additionalData.length];
                for(int i=0; i<additionalData.length; i++) {
                    PDPNamedAttributesWrapper additionalDataWrapper = new PDPNamedAttributesWrapper(additionalData[i]);
                    String additionalDataID = assembleRequestPart(additionalDataMap, additionalDataWrapper,
                            idGen, RefIDGenerator.ADDITIONALDATA);
                    additionalDataRef[i] = additionalDataID;
                }
                reqRef.setAdditionalDataRef(additionalDataRef);
            }

            requestRefList.add(reqRef);

        }

        // then create a Request object to fill in the categories
        StdMutableRequest mutableRequest = new StdMutableRequest();

        // first action map
        Set<Map.Entry<String, String>> actionEntrySet = actionMap.entrySet();
        Identifier actionCategory = XACML3.ID_ATTRIBUTE_CATEGORY_ACTION;
        for(Map.Entry<String, String> entry: actionEntrySet) {
            assembleCategory(actionCategory, entry.getValue(), entry.getKey(), mutableRequest);
        }

        // then user map
        Set<Map.Entry<String, PDPNamedAttributesWrapper>> userEntrySet = userMap.entrySet();
        Identifier userCategory = XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT;
        for(Map.Entry<String, PDPNamedAttributesWrapper> entry: userEntrySet) {
            assembleCategory(userCategory, entry.getValue(), entry.getKey(), mutableRequest);
        }

        // then host map
        Set<Map.Entry<String, PDPNamedAttributesWrapper>> hostEntrySet = hostMap.entrySet();
        Identifier hostCategory = new IdentifierImpl(CATEGORY_HOST);
        for(Map.Entry<String, PDPNamedAttributesWrapper> entry: hostEntrySet) {
            assembleCategory(hostCategory, entry.getValue(), entry.getKey(), mutableRequest);
        }

        // then application map
        Set<Map.Entry<String, PDPNamedAttributesWrapper>> applicationEntrySet = applicationMap.entrySet();
        Identifier applicationCategory = new IdentifierImpl(CATEGORY_APPLICATION);
        for(Map.Entry<String, PDPNamedAttributesWrapper> entry: applicationEntrySet) {
            assembleCategory(applicationCategory, entry.getValue(), entry.getKey(), mutableRequest);
        }

        // then resources map
        Set<Map.Entry<String, PDPNamedAttributesWrapper>> resourceEntrySet = resourceMap.entrySet();
        Identifier resourceCategory = XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE;
        for(Map.Entry<String, PDPNamedAttributesWrapper> entry: resourceEntrySet) {
            assembleCategory(resourceCategory, entry.getValue(), entry.getKey(), mutableRequest);
        }

        // then additionalData
        Set<Map.Entry<String, PDPNamedAttributesWrapper>> additionalDataEntrySet = additionalDataMap.entrySet();
        for(Map.Entry<String, PDPNamedAttributesWrapper> entry: additionalDataEntrySet) {
            String entryCategoryName = entry.getValue().getIpdpNamedAttributes().getName();
            if(entryCategoryName.equals(PDPRequest.KEY_POD_NAME)) {
                // policyOnDemand
                assembleCategory(new IdentifierImpl(CATEGORY_POLICY_ON_DEMAND), entry.getValue(),
                        entry.getKey(), mutableRequest);
            } else if(entryCategoryName.equals(PDPRequest.KEY_RECIPIENT_NAME)) {
                // recipient
                assembleCategory(XACML3.ID_SUBJECT_CATEGORY_RECIPIENT_SUBJECT, entry.getValue(), entry.getKey(),
                        mutableRequest);
            } else if(entryCategoryName.equals(PDPRequest.KEY_ENVIRONMENT)) {
                // environment
                assembleCategory(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT, entry.getValue(), entry.getKey(),
                        mutableRequest);
            } else {
                // just add the category, but it need to be a valid uri
                try {
                    entryCategoryName = java.net.URLEncoder.encode(entryCategoryName, "UTF-8").replace("+", "%20");
                } catch (UnsupportedEncodingException e) {}
                assembleCategory(new IdentifierImpl(entryCategoryName), entry.getValue(), entry.getKey(),
                        mutableRequest);
            }
        }

        if(requestList.size()==1) {
            //single request
            // no need to add RequestReference
        } else {
            //multi request
            for(PDPRequestRef reqRef: requestRefList) {
                // create reference corresponding to RequestReference list element
                StdMutableRequestReference requestReference = new StdMutableRequestReference();
                List<String> allRef = reqRef.getAllRef();
                for(String ref: allRef) {
                    StdRequestAttributesReference requestAttributesReference = new StdRequestAttributesReference(ref);
                    requestReference.add(requestAttributesReference);
                }
                mutableRequest.add(requestReference);
            }

        }
        // all done
        return new StdRequest(mutableRequest);
    }

    /**
     * This method put the pdpPart into the partMap and returns the key (which is used as ID for that pdpPart)
     * If the partMap already has the value of pdpPart, will skip adding the pdpPart and return the existing key
     *
     * @param partMap
     * @param pdpPart
     * {@link String} or {@link PDPNamedAttributesWrapper} or any other class implementing
     * hashCode and equals methods correctly
     * @param idGen
     * the id generator used for assembling this Request
     * @param idPrefix
     * the prefix for the generated ID
     * @return
     */
    private static <T> String assembleRequestPart(Map<String, T> partMap, T pdpPart, RefIDGenerator idGen, String idPrefix) {
        if(pdpPart == null) {
            return null;
        }

        String pdpPartID = null;
        if(partMap.containsValue(pdpPart)) {
            for(Map.Entry<String, T> entry: partMap.entrySet()) {
                if(entry.getValue().equals(pdpPart)) {
                    pdpPartID = entry.getKey();
                    break;
                }
            }
        } else {
            pdpPartID = idGen.generateNext(idPrefix);
            partMap.put(pdpPartID, pdpPart);
        }
        return pdpPartID;
    }

    private static <T> void assembleCategory(Identifier categoryID, T pdpPart, String xmlID, StdMutableRequest mutalbleRequest) throws Exception {
        //TODO includeInResult for Attribute is set to false for all now

        if(categoryID == null || pdpPart == null || mutalbleRequest == null) {
            return;
        }

        List<Attribute> attributeList = new ArrayList<>();

        if(categoryID.equals(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT)) {
            // user
            IPDPUser user = (IPDPUser) ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();

            Attribute userIDAttr = assembleAttribute(categoryID, XACML3.ID_SUBJECT_SUBJECT_ID, DataTypes.DT_STRING.getId(),
                    user.getValue(PDPRequest.ATTR_USER_ID), false);

            Map<String, List<String>> userAttributeMap = getAttributeMap(user);
            List<Attribute> userOtherAttrs = assembleBulkAttribute(categoryID, userAttributeMap,
                    Arrays.asList(PDPRequest.ATTR_USER_ID), false);

            attributeList.add(userIDAttr);
            attributeList.addAll(userOtherAttrs);

        } else if(categoryID.equals(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION)) {
            // action
            Attribute actionAttr = assembleAttribute(categoryID, XACML3.ID_ACTION_ACTION_ID, DataTypes.DT_STRING.getId(),
                    pdpPart.toString(), false);
            attributeList.add(actionAttr);

        } else if(categoryID.equals(new IdentifierImpl(CATEGORY_APPLICATION))) {
            // application
            IPDPApplication application = (IPDPApplication) ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();

            Attribute applicationNameAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_APPLICATION_ID),
                    DataTypes.DT_STRING.getId(), application.getValue(PDPRequest.ATTR_APPLICATION_NAME), false);

            Map<String, List<String>> applicationAttributeMap = getAttributeMap(application);
            List<Attribute> applicationOtherAttrs = assembleBulkAttribute(categoryID, applicationAttributeMap,
                    Arrays.asList(PDPRequest.ATTR_APPLICATION_NAME), false);

            attributeList.add(applicationNameAttr);
            attributeList.addAll(applicationOtherAttrs);

        } else if(categoryID.equals(new IdentifierImpl(CATEGORY_HOST))) {
            // host
            IPDPHost host = (IPDPHost) ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();

            // ip or hostname
            if(host.getValue(PDPRequest.ATTR_HOST_IP) != null ) {
                String ipString =  InetAddresses.fromInteger(
                        Integer.parseInt(host.getValue(PDPRequest.ATTR_HOST_IP))).getHostAddress();
                Attribute hostIPAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_HOST_INET_ADDR),
                        DataTypes.DT_IPADDRESS.getId(), ipString, false);
                attributeList.add(hostIPAttr);
            }
            if(host.getValue(PDPRequest.ATTR_HOST_HOSTNAME) != null) {
                Attribute hostNameAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_HOST_NAME),
                        DataTypes.DT_STRING.getId(), host.getValue(PDPRequest.ATTR_HOST_HOSTNAME), false);
                attributeList.add(hostNameAttr);
            }

            Map<String, List<String>> hostAttributeMap = getAttributeMap(host);
            List<Attribute> hostOtherAttrs = assembleBulkAttribute(categoryID, hostAttributeMap,
                    Arrays.asList(PDPRequest.ATTR_HOST_HOSTNAME, PDPRequest.ATTR_HOST_IP), false);
            attributeList.addAll(hostOtherAttrs);

        } else if(categoryID.equals(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE)) {
            // resource
            IPDPResource resource = (IPDPResource) ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();

            Attribute resourceIDAttr = assembleAttribute(categoryID, XACML3.ID_RESOURCE_RESOURCE_ID,
                    DataTypes.DT_STRING.getId(), resource.getValue(PDPRequest.ATTR_RESOURCE_ID), false);

            Attribute resourceTypeAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_RESOURCE_TYPE),
                    DataTypes.DT_STRING.getId(), resource.getValue(PDPRequest.ATTR_RESOURCE_TYPE), false);

            Attribute resourceDimensionAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_RESOURCE_DIMENSION),
                    DataTypes.DT_STRING.getId(), resource.getName(), false);

            Map<String, List<String>> resourceAttributeMap = getAttributeMap(resource);
            List<Attribute> resourceOtherAttrs = assembleBulkAttribute(categoryID, resourceAttributeMap,
                    Arrays.asList(PDPRequest.ATTR_RESOURCE_ID, PDPRequest.ATTR_RESOURCE_TYPE), false);

            attributeList.add(resourceIDAttr);
            attributeList.add(resourceTypeAttr);
            attributeList.add(resourceDimensionAttr);
            attributeList.addAll(resourceOtherAttrs);

        } else if(categoryID.equals(new IdentifierImpl(CATEGORY_POLICY_ON_DEMAND))) {
            // policyOnDemand
            //PolicyOnDemand only have 2 attributes
            IPDPNamedAttributes pod = ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();
            Attribute podIDAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_POD_ID),
                    DataTypes.DT_STRING.getId(), pod.getValue(PDPRequest.ATTR_POD_PQL), false);

            Attribute podIgnoreBuildInPoliciesAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_POD_IGNORE_BUILT_IN),
                    DataTypes.DT_BOOLEAN.getId(), pod.getValue(PDPRequest.ATTR_POD_IGNOREDEFAULT), false);

            attributeList.add(podIDAttr);
            attributeList.add(podIgnoreBuildInPoliciesAttr);

        } else if(categoryID.equals(XACML3.ID_SUBJECT_CATEGORY_RECIPIENT_SUBJECT)) {
            // recipient
            IPDPNamedAttributes recipient = ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();
            // currently we only have email for recipient
            Attribute recipientEmailAttr = assembleAttribute(categoryID, new IdentifierImpl(ATTRIBUTE_RECIPIENT_EMAIL),
                    DataTypes.DT_STRING.getId(), recipient.getValue(PDPRequest.ATTR_RECIPIENT_EMAIL), false);
            // for other unknown attributes, we just add them
            Map<String, List<String>> recipientAttributeMap = getAttributeMap(recipient);
            List<Attribute> recipientOtherAttr = assembleBulkAttribute(categoryID, recipientAttributeMap,
                    Arrays.asList(PDPRequest.ATTR_RECIPIENT_EMAIL), false);
            attributeList.add(recipientEmailAttr);
            attributeList.addAll(recipientOtherAttr);

        } else if(categoryID.equals(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT)) {
            // environment
            // TODO
        }
        else {
            // other additionalData
            IPDPNamedAttributes additionalData = ((PDPNamedAttributesWrapper) pdpPart).getIpdpNamedAttributes();
            // just add all attributes
            Map<String, List<String>> additionalDataAttributeMap = getAttributeMap(additionalData);
            List<Attribute> additionalDataAttr = assembleBulkAttribute(categoryID, additionalDataAttributeMap,
                    Collections.EMPTY_LIST, false);
            attributeList.addAll(additionalDataAttr);
        }


        StdMutableRequestAttributes attributes = new StdMutableRequestAttributes(categoryID, attributeList, null, xmlID);
        mutalbleRequest.add(attributes);
    }

    /**
     * <p>
     *     Returns a Map of attributes with a list of String as values for IPDPNamedAttributes
     * </p>
     *
     * @param ipdpNamedAttributes {@link IPDPNamedAttributes}
     * @return
     */
    private static Map<String, List<String>> getAttributeMap(IPDPNamedAttributes ipdpNamedAttributes) {
        Map<String, List<String>> attributesMap = new HashMap<>();

        Map<String, DynamicAttributes> dynamicAttributesHashMap = new HashMap<>();
        ipdpNamedAttributes.addSelfToMap(dynamicAttributesHashMap);
        DynamicAttributes dynamicAttributes = dynamicAttributesHashMap.get(ipdpNamedAttributes.getName());

        for(String key: dynamicAttributes.keySet()) {
            attributesMap.put(key, Arrays.asList(dynamicAttributes.getStrings(key)));
        }

        return attributesMap;
    }

    private static List<Attribute> assembleBulkAttribute(Identifier categoryID,
            Map<String, List<String>> attributeMap, List<String> keysToSkip, boolean includeInResult) throws Exception {
        if(categoryID == null || attributeMap == null || keysToSkip == null ) {
            return Collections.EMPTY_LIST;
        }
        List<Attribute> attrs = new ArrayList<>();

        for(String key: attributeMap.keySet()) {
            if(key != null && !keysToSkip.contains(key)) {
                Attribute attr = assembleAttribute(categoryID, new IdentifierImpl(convertToQualifiedKey(categoryID, key)),
                        DataTypes.DT_STRING.getId(), attributeMap.get(key), includeInResult);
                attrs.add(attr);
            }
        }
        return attrs;
    }

    private static Attribute assembleAttribute(Identifier categoryID, Identifier attributeID, Identifier dataTypeId,
            Object incomingAttributeValue, boolean includeInResult) throws Exception {

        if(attributeID == null || dataTypeId == null || incomingAttributeValue == null) {
            return null;
        }
        DataType<?> dataType = dataTypeFactory.getDataType(dataTypeId);
        // create a single Attribute to return (it may contain multiple AttributeValues)
        Attribute attribute = null;

        if(incomingAttributeValue instanceof List) {
            // this attribute has a list of values
            List<AttributeValue<?>> attributeValueList = new ArrayList<AttributeValue<?>>();
            for (Object o : (List<?>)incomingAttributeValue) {
                AttributeValue<Object> attributeValue;
                Object convertedValue = dataType.convert(o);
                attributeValue = new StdAttributeValue<Object>(dataTypeId, convertedValue);
                attributeValueList.add(attributeValue);
            }
            attribute = new StdAttribute(categoryID, attributeID, attributeValueList, null, includeInResult);
        } else {
            // this attribute has a single value
            AttributeValue<Object> attributeValue;
            Object convertedValue = dataType.convert(incomingAttributeValue);
            attributeValue = new StdAttributeValue<Object>(dataTypeId, convertedValue);
            attribute = new StdAttribute(categoryID, attributeID, attributeValue, null, includeInResult);
        }
        return attribute;

    }
}

class PDPRequestRef {

    private String actionRef;
    private String hostRef;
    private String userRef;
    private String applicationRef;
    private String[] resourceRef;
    private String[] additionalDataRef;

    public String getActionRef() {
        return actionRef;
    }
    public void setActionRef(String actionRef) {
        this.actionRef = actionRef;
    }
    public String getHostRef() {
        return hostRef;
    }
    public void setHostRef(String hostRef) {
        this.hostRef = hostRef;
    }
    public String getUserRef() {
        return userRef;
    }
    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }
    public String getApplicationRef() {
        return applicationRef;
    }
    public void setApplicationRef(String applicationRef) {
        this.applicationRef = applicationRef;
    }
    public String[] getResourceRef() {
        return resourceRef;
    }
    public void setResourceRef(String[] resourceRef) {
        this.resourceRef = resourceRef;
    }
    public String[] getAdditionalDataRef() {
        return additionalDataRef;
    }
    public void setAdditionalDataRef(String[] additionalDataRef) {
        this.additionalDataRef = additionalDataRef;
    }

    public List<String> getAllRef() {
        List<String> refList = new ArrayList<>();
        refList.add(actionRef);
        if(hostRef != null) {
            refList.add(hostRef);
        }
        refList.add(userRef);
        if(applicationRef != null) {
            refList.add(applicationRef);
        }
        refList.addAll(Arrays.asList(resourceRef));

        if(additionalDataRef != null) {
            refList.addAll(Arrays.asList(additionalDataRef));
        }
        return refList;
    }

}

class RefIDGenerator {

    public final static String ACTION = "action";
    public final static String HOST = "host";
    public final static String USER = "user";
    public final static String APPLICATION = "application";
    public final static String RESOURCE = "resource";
    public final static String ADDITIONALDATA = "additionalData";
    public final static String DEFAULT = "other";

    private Map<String, Integer> refCurrentKey;

    public RefIDGenerator() {
        // some init work
        this.refCurrentKey = new HashMap<>(8);
        this.refCurrentKey.put(ACTION, 0);
        this.refCurrentKey.put(HOST, 0);
        this.refCurrentKey.put(USER, 0);
        this.refCurrentKey.put(APPLICATION, 0);
        this.refCurrentKey.put(RESOURCE, 0);
        this.refCurrentKey.put(ADDITIONALDATA, 0);
        this.refCurrentKey.put(DEFAULT, 0);
    }

    public synchronized String generateNext(String prefix) {
        String nextKey = null;
        switch(prefix) {
        case ACTION:
        case HOST:
        case USER:
        case APPLICATION:
        case RESOURCE:
        case ADDITIONALDATA:
            Integer currVal = this.refCurrentKey.get(prefix);
            this.refCurrentKey.put(prefix, currVal+1);
            nextKey = prefix + (currVal+1);
            break;
        default:
            Integer currValDefault = this.refCurrentKey.get(DEFAULT);
            this.refCurrentKey.put(DEFAULT, currValDefault+1);
            nextKey = prefix + (currValDefault+1);
        }
        return nextKey;
    }


}