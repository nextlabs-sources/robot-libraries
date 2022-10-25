package com.nextlabs.qa.keywords.pdp;

import com.att.research.xacml.api.Request;
import com.bluejungle.destiny.agent.pdpapi.*;
import com.google.common.net.InetAddresses;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nextlabs.destiny.sdk.*;
import com.nextlabs.qa.keywords.pdp.Utils.CERequestUtil;
import com.nextlabs.qa.keywords.pdp.Utils.HostIPUtil;
import com.nextlabs.qa.keywords.pdp.beans.PDPNamedAttributesWrapper;
import com.nextlabs.qa.keywords.pdp.beans.PDPRequest;
import com.nextlabs.qa.keywords.pdp.json.beans.PDPResponse;
import com.nextlabs.qa.keywords.pdp.json.beans.Result;
import com.nextlabs.qa.keywords.pdp.parser.ResponseTypeParser;
import com.nextlabs.qa.keywords.pdp.serializer.JSONRequestSerializer;
import com.nextlabs.qa.keywords.pdp.serializer.RequestSerializer;
import com.nextlabs.qa.keywords.pdp.serializer.RequestUtil;
import com.nextlabs.qa.keywords.pdp.serializer.XMLRequestSerializer;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jspringbot.syntax.HighlightRobotLogger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by sduan on 16/11/2015.
 */
public class PDPRequestHelper {

    public static final HighlightRobotLogger LOG = HighlightRobotLogger.getLogger(PDPRequestHelper.class);

    public static final String SERIALIZE_FORMAT_XML = "xml";
    public static final String SERIALIZE_FORMAT_JSON = "json";

    private static final int LOCALHOST = 0x7F000001;

    private Map<String, Object> entityRefMap = new HashMap<>();
    private Map<String, PDPRequest> pdpRequestMap = new HashMap<>();
    private Map<String, List<String>> multiRequestRefMap = new HashMap<>();

    private Map<String, CEEnforcement> responseRMIMap = new HashMap<>();
    private Map<String, ResultType> responseXMLMap = new HashMap<>();
    private Map<String, Result> responseJSONMap = new HashMap<>();

    // the requestCache is used to save some convertion time from PDPRequest or list of PDPRequest
    // to com.att.research.xacml.api.Request
    private Map<String, Request> requestCache = new HashMap<>();

    private RequestSerializer xmlSerializer = null;
    private RequestSerializer jsonSerializer = null;
    private ResponseTypeParser xmlParser = null;

    private CESdk ceSdk = null;

    protected CloseableHttpClient httpClinet;

    private void initSerializer() {
        this.xmlSerializer = new XMLRequestSerializer();
        this.jsonSerializer = new JSONRequestSerializer();
    }

    private void initParser() {
        this.xmlParser = new ResponseTypeParser();
    }

    private String PDPHost;
    private Integer PDPRMIPort;
    private Integer PDPRESTPort;
    private String PDPRESTPath;

    private int timeout = 10000;

    private boolean performObligations = true;
    private int noiseLevel = ICESdk.CE_NOISE_LEVEL_USER_ACTION;

    public PDPRequestHelper(String PDPHost, Integer PDPRMIPort, Integer PDPRESTPort, String PDPRESTPath) {
        this.PDPHost = PDPHost;
        this.PDPRMIPort = PDPRMIPort;
        this.PDPRESTPort = PDPRESTPort;
        this.PDPRESTPath = PDPRESTPath;
    }

    public void reset() {
        this.noiseLevel = ICESdk.CE_NOISE_LEVEL_USER_ACTION;
        this.performObligations = true;
        this.entityRefMap.clear();
        this.pdpRequestMap.clear();
        this.multiRequestRefMap.clear();
        this.responseRMIMap.clear();
        this.responseJSONMap.clear();
        this.responseXMLMap.clear();
        this.requestCache.clear();
        this.ceSdk = null;
    }

    public void setPerformObligations(boolean performObligations) {
        this.performObligations = performObligations;
    }

    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String createUser(String id, String name) {
        String refId = UUID.randomUUID().toString();
        IPDPUser user = null;
        if(name == null) {
            user = new PDPUser(id);
        } else {
            user = new PDPUser(id, name);
        }
        this.entityRefMap.put(refId, user);
        return refId;
    }

    public String createResource(String name, String type, String dimension) {
        String refId = UUID.randomUUID().toString();
        IPDPResource resource = new PDPResource(dimension, name, type);
        this.entityRefMap.put(refId, resource);
        return refId;
    }

    public String createAction(String action) {
        String refId = UUID.randomUUID().toString();
        this.entityRefMap.put(refId, action.toUpperCase());
        return refId;
    }

    public String createApplication(String name) {
        String refId = UUID.randomUUID().toString();
        IPDPApplication app = new PDPApplication(name);
        this.entityRefMap.put(refId, app);
        return refId;
    }

    public String createApplication(String name, long pid) {
        String refId = UUID.randomUUID().toString();
        IPDPApplication app = new PDPApplication(name, pid);
        this.entityRefMap.put(refId, app);
        return refId;
    }

    /*
     * Create a IPDPHost object with name as IPAddress (v4) or hostname
     */
    public String createHost(String name) {
        String refId = UUID.randomUUID().toString();
        InetAddressValidator inetAddressValidator = InetAddressValidator.getInstance();

        IPDPHost host = null;
        if(inetAddressValidator.isValidInet4Address(name)) {
            int ip = InetAddresses.coerceToInteger(InetAddresses.forString(name));
            host = new PDPHost(ip);
        } else {
            // regard the string as a hostname
            host = new PDPHost(name);
        }
        this.entityRefMap.put(refId, host);
        return refId;
    }

    public String createRecipient(String email) {
        String refId = UUID.randomUUID().toString();
        IPDPNamedAttributes recipient = new PDPNamedAttributes(PDPRequest.KEY_RECIPIENT_NAME);
        recipient.setAttribute(PDPRequest.ATTR_RECIPIENT_EMAIL, email);

        this.entityRefMap.put(refId, recipient);
        return refId;
    }

    public String createPolicyOnDemand(String pql, Boolean ignoreBuildInPolicies) {
        String refId = UUID.randomUUID().toString();
        IPDPNamedAttributes pod = new PDPNamedAttributes(PDPRequest.KEY_POD_NAME);
        pod.setAttribute(PDPRequest.ATTR_POD_PQL, pql);
        pod.setAttribute(PDPRequest.ATTR_POD_IGNOREDEFAULT, ignoreBuildInPolicies.toString());

        this.entityRefMap.put(refId, pod);
        return refId;
    }

    public String createAdditionalData(String name) {
        String refID = UUID.randomUUID().toString();
        IPDPNamedAttributes additionalData = new PDPNamedAttributes(name);

        this.entityRefMap.put(refID, additionalData);
        return refID;
    }

    public void addAttribute(String refId, String key, String value) {
        if(refId == null || !this.entityRefMap.containsKey(refId)) {
            throw new IllegalStateException("The entity Reference ID doesn't exist.");
        }
        Object entity = this.entityRefMap.get(refId);
        if(! (entity instanceof IPDPNamedAttributes)) {
            throw new IllegalStateException("The Reference ID is not for entity with attributes.");
        }
        ((IPDPNamedAttributes)entity).setAttribute(key, value);
    }

    public String assembleRequest(String[] refIds) {
        if(refIds == null) {
            throw new IllegalStateException("The entity Reference IDs shouldn't be null");
        }

        PDPRequest request = new PDPRequest();
        for(String refId: refIds) {
            Object entity = entityRefMap.get(refId);
            if(entity == null) {
                throw new IllegalStateException("The entity Reference ID" + refId + " doesn't exist.");
            }
            if(entity instanceof String) {
                // the entity is action
                request.setAction(String.valueOf(entity));
            } else if(entity instanceof IPDPNamedAttributes) {
                IPDPNamedAttributes ipdpNamedAttributes = (IPDPNamedAttributes) entity;
                switch(ipdpNamedAttributes.getName()) {
                    case PDPRequest.KEY_USER_NAME:
                        // the entity is user
                        request.setUser((IPDPUser) ipdpNamedAttributes);
                        break;
                    case PDPRequest.KEY_APPLICATION_NAME:
                        // the entity is application
                        request.setApplication((IPDPApplication) ipdpNamedAttributes);
                        break;
                    case PDPRequest.KEY_HOST_NAME:
                        // the entity is host
                        request.setHost((IPDPHost) ipdpNamedAttributes);
                        break;
                    case PDPRequest.KEY_RESOURCE_NAME_FROM:
                    case PDPRequest.KEY_RESOURCE_NAME_TO:
                        // the entity is a resource
                        IPDPResource[] resources = request.getResourceArr();
                        if(resources == null || resources.length == 0) {
                            request.setResourceArr(new IPDPResource[] {(IPDPResource) ipdpNamedAttributes});
                        } else {
                            IPDPResource[] newResources = new IPDPResource[resources.length+1];
                            System.arraycopy(resources, 0, newResources, 0, resources.length);
                            newResources[-1] = (IPDPResource) ipdpNamedAttributes;
                            request.setResourceArr(newResources);
                        }
                        break;
                    case PDPRequest.KEY_POD_NAME:
                        // the entity is POD
                        // add to additionalData
                    case PDPRequest.KEY_RECIPIENT_NAME:
                        // the entity is a recipient
                        // add to additionalData
                    default:
                        // add to additionalData
                        IPDPNamedAttributes[] additionalData = request.getAdditionalData();
                        if(additionalData == null || additionalData.length == 0) {
                            request.setAdditionalData(new IPDPNamedAttributes[] {ipdpNamedAttributes});
                        } else {
                            IPDPNamedAttributes[] newAdditionalData = new IPDPNamedAttributes[additionalData.length+1];
                            System.arraycopy(additionalData, 0, newAdditionalData, 0, additionalData.length);
                            newAdditionalData[newAdditionalData.length-1] = ipdpNamedAttributes;
                            request.setAdditionalData(newAdditionalData);
                        }
                        break;
                }
            }
        }

        String refID = UUID.randomUUID().toString();
        this.pdpRequestMap.put(refID, request);

        return refID;
    }

    public String assembleMultiRequest(String[] requestRefIds) {
        if(requestRefIds == null || requestRefIds.length < 2) {
            throw new IllegalStateException("The request reference ids specified are less than 2");
        }
        List<String> requestRefs = new ArrayList<>();
        for(String ref: requestRefIds) {
            if(! this.pdpRequestMap.containsKey(ref)) {
                throw new IllegalStateException("The request Reference ID " + ref + " doesn't exist.");
            }
            requestRefs.add(ref);
        }

        String refID = UUID.randomUUID().toString();
        this.multiRequestRefMap.put(refID, requestRefs);

        return refID;
    }

    private Request retrieveRequest(String requestRef) throws Exception {
        Request request = null;

        // first check the cache
        if(!this.requestCache.containsKey(requestRef)) {
            if(this.pdpRequestMap.containsKey(requestRef)) {
                // single request
                request = RequestUtil.convertRequest(Arrays.asList(this.pdpRequestMap.get(requestRef)));
                this.requestCache.put(requestRef, request);
            } else if(this.multiRequestRefMap.containsKey(requestRef)) {
                // multi request
                List<PDPRequest> requests = new ArrayList<>();
                for(String ref: this.multiRequestRefMap.get(requestRef)) {
                    requests.add(this.pdpRequestMap.get(ref));
                }
                request = RequestUtil.convertRequest(requests);
            } else {
                throw new IllegalStateException("Couldn't find the request specified");
            }
        } else {
            request = this.requestCache.get(requestRef);
        }

        return request;
    }

    public String serializeRequest(String requestRef, String format) throws Exception {
        Request request = retrieveRequest(requestRef);

        if(format.equals(SERIALIZE_FORMAT_JSON)) {
            if(this.jsonSerializer == null) {initSerializer();}
            return this.jsonSerializer.serializeData(request);
        } else if(format.equals(SERIALIZE_FORMAT_XML)) {
            if(this.xmlSerializer == null) {initSerializer();}
            return this.xmlSerializer.serializeData(request);
        } else {
            throw new IllegalArgumentException("Unsupported serialize format.");
        }
    }

    private List<CEEnforcement> checkResources(List<PDPRequest> requests) throws CESdkException {
        if(this.ceSdk == null) {
            this.ceSdk = new CESdk(this.PDPHost, this.PDPRMIPort);
        }

        // if all requests have same host, then we invoke the request with one sdk method call
        // else, we need to invoke separate sdk method calls
        // we can improve further later (group together requests with same host)
        boolean samehost = false;
        Set<PDPNamedAttributesWrapper> hostsSet = new HashSet<>();
        for(PDPRequest req: requests) {
            hostsSet.add(new PDPNamedAttributesWrapper(req.getHost()));
        }
        if(hostsSet.size() == 1) {
            samehost = true;
        }

        if(samehost) {
            List<CERequest> ceRequests = new ArrayList<>();
            for(PDPRequest req: requests) {
                CERequest ceReq = CERequestUtil.convertPDPRequest(req);

                // log the request
                LOG.info("Invoke RMI Request");
                this.logCERequest(ceReq, "INFO");

                // set performObligations and noiseLevel (no setter, need to use reflection)
                try {
                    Field performObligationsField = CERequest.class.getDeclaredField("performObligations");
                    performObligationsField.setAccessible(true);
                    performObligationsField.set(ceReq, this.performObligations);
                    Field noiseLevelField = CERequest.class.getDeclaredField("noiseLevel");
                    noiseLevelField.setAccessible(true);
                    noiseLevelField.set(ceReq, this.noiseLevel);
                } catch (IllegalAccessException|NoSuchFieldException e) {
                    LOG.debug("failed to set performObligation or noiseLevel for CERequest");
                }
                ceRequests.add(ceReq);
            }
            int ipAddress = LOCALHOST;
            try {
                ipAddress = HostIPUtil.getIPFromHost(requests.get(0).getHost());
            } catch (Exception e) {
                LOG.info(e.getMessage() + " Use localhost as host");
            }
            // policyOnDemand is already stored inside the CERequests, no need to pass to this method
            List<CEEnforcement> ceEnforcements = this.ceSdk.checkResources(ceRequests, null, false, ipAddress, this.timeout);
            return ceEnforcements;
        } else {
            List<CEEnforcement> ceEnforcements = new ArrayList<>();
            for(PDPRequest req: requests) {
                CERequest ceReq = CERequestUtil.convertPDPRequest(req);
                int ipAddress = LOCALHOST;
                try {
                    ipAddress = HostIPUtil.getIPFromHost(req.getHost());
                } catch (Exception e) {
                    LOG.info(e.getMessage() + " Use localhost as host");
                }
                CEEnforcement ceEnforcement = this.ceSdk.checkResources(
                        ceReq.getAction(),
                        ceReq.getSource(), ceReq.getSourceAttributes(),
                        ceReq.getDest(), ceReq.getDestAttributes(),
                        ceReq.getUser(), ceReq.getUserAttributes(),
                        ceReq.getApplication(), ceReq.getApplicationAttributes(),
                        ceReq.getAdditionalAttributes(),
                        ceReq.getRecipients(),
                        ipAddress,
                        this.performObligations,
                        this.noiseLevel,
                        this.timeout);
                ceEnforcements.add(ceEnforcement);
            }
            return ceEnforcements;
        }
    }

    public List<String> invokeAuthRMIRequest(String requestRef) throws CESdkException {

        List<CEEnforcement> ceEnforcements = null;
        if(this.pdpRequestMap.containsKey(requestRef)) {
            // single request
            ceEnforcements = this.checkResources(Arrays.asList(this.pdpRequestMap.get(requestRef)));
        } else if(this.multiRequestRefMap.containsKey(requestRef)) {
            // multi request
            List<PDPRequest> pdpRequests = new ArrayList<>();
            for(String reqRef: this.multiRequestRefMap.get(requestRef)) {
                pdpRequests.add(this.pdpRequestMap.get(reqRef));
            }
            ceEnforcements = this.checkResources(pdpRequests);
        } else {
            throw new IllegalStateException("Couldn't find the request specified");
        }

        List<String> resultRefs = new ArrayList<>();

        for(CEEnforcement enforcement: ceEnforcements) {
            String refId = UUID.randomUUID().toString();
            this.responseRMIMap.put(refId, enforcement);
            resultRefs.add(refId);
        }
        return resultRefs;

    }

    private List<String> invokeAuthRestRequest(String requestRef, String format) throws Exception {

        if(this.httpClinet == null) {
            this.httpClinet = HttpClients.createDefault();
        }

        String url = String.format("http://%s:%d%s", this.PDPHost, this.PDPRESTPort, this.PDPRESTPath);

        HttpPost post = new HttpPost(url);
        List <NameValuePair> params = new ArrayList <>();
        params.add(new BasicNameValuePair("Version", "1.0"));
        params.add(new BasicNameValuePair("DataType", format));
        params.add(new BasicNameValuePair("Service", "EVAL"));
        params.add(new BasicNameValuePair("data", this.serializeRequest(requestRef, format)));

        post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

        CloseableHttpResponse response = this.httpClinet.execute(post);
        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity, "utf-8");

        LOG.info("Got response String: ");
        LOG.info(responseString);

        List<String> resultRefs = new ArrayList<>();
        if(format.equals(SERIALIZE_FORMAT_XML)) {
            // xml request
            ResponseType responseType = null;
            if(this.xmlParser == null) {
                this.initParser();
            }
            try {
                responseType = this.xmlParser.parse(responseString);
            } catch (Exception e) {
                throw new IllegalStateException("Invalid XML XACML response." + e.getMessage());
            }
            List<ResultType> results = responseType.getResult();
            Iterator<ResultType> iterResults = results.iterator();

            while(iterResults.hasNext()) {
                String refId = UUID.randomUUID().toString();
                ResultType result = iterResults.next();
                this.responseXMLMap.put(refId, result);
                resultRefs.add(refId);
            }

        } else {
            // json request
            try {
                Gson gson = new Gson();
                PDPResponse pdpResponse = gson.fromJson(responseString, PDPResponse.class);

                Iterator<Result> iterPDPResults = pdpResponse.getResponse().getResult().iterator();
                while(iterPDPResults.hasNext()) {
                    String refId = UUID.randomUUID().toString();
                    Result result = iterPDPResults.next();
                    this.responseJSONMap.put(refId, result);
                    resultRefs.add(refId);
                }
            } catch (JsonSyntaxException e) {
                throw new IllegalStateException("Invalid JSON response.");
            }

        }
        return resultRefs;
    }

    public List<String> invokeAuthJSONRequest(String requestRef) throws Exception {
        return this.invokeAuthRestRequest(requestRef, SERIALIZE_FORMAT_JSON);
    }
    public List<String> invokeAuthXMLRequest(String requestRef) throws Exception {
        return this.invokeAuthRestRequest(requestRef, SERIALIZE_FORMAT_XML);
    }

    public void authResponseEffectShouldBe(String resultRef, String expectValue) {
        String actualValue = null;
        if(this.responseRMIMap.containsKey(resultRef) && this.responseRMIMap.get(resultRef) != null) {
            // RMI
            actualValue = this.responseRMIMap.get(resultRef).getResponseAsString();
        } else if(this.responseXMLMap.containsKey(resultRef) && this.responseXMLMap.get(resultRef) != null) {
            // XML
            actualValue = this.responseXMLMap.get(resultRef).getDecision().value();
        } else if(this.responseJSONMap.containsKey(resultRef) && this.responseJSONMap.get(resultRef) != null) {
            // JSON
            actualValue = this.responseJSONMap.get(resultRef).getDecision();
        } else {
            throw new IllegalStateException("Couldn't find the auth result specified");
        }

        if(actualValue == null) {
            throw new IllegalStateException("The result got is null");
        } else if(! actualValue.equalsIgnoreCase(expectValue)) {
            throw new IllegalStateException(String.format(
                    "Expecting result decision '%s' but was '%s'.", expectValue, actualValue));
        } else {
            return;
        }
    }

    public void authResponseObligationsCountShouldBe(String resultRef, int expectValue) {
        int actualValue = 0;
        if(this.responseRMIMap.containsKey(resultRef) && this.responseRMIMap.get(resultRef) != null) {
            // RMI
            for(CEAttributes.CEAttribute ceAttr: this.responseRMIMap.get(resultRef).getObligations().getAttributes()) {
                if(ceAttr.getKey().equals("CE_ATTR_OBLIGATION_COUNT")) {
                    actualValue = Integer.parseInt(ceAttr.getValue());
                }
            }
        } else if(this.responseXMLMap.containsKey(resultRef) && this.responseXMLMap.get(resultRef) != null) {
            // XML
            actualValue = this.responseXMLMap.get(resultRef).getObligations().getObligation().size();
        } else if(this.responseJSONMap.containsKey(resultRef) && this.responseJSONMap.get(resultRef) != null) {
            // JSON
            actualValue = this.responseJSONMap.get(resultRef).getObligations().size();
        } else {
            throw new IllegalStateException("Couldn't find the auth result specified");
        }
        if(actualValue != expectValue) {
            throw new IllegalStateException(String.format(
                    "Expecting result decision '%d' but was '%d'.", expectValue, actualValue));
        } else {}
    }

    public void authResponseStatusCodeShouldBe(String resultRef, String expectValue) {
        String actualValue = null;
        if(this.responseRMIMap.containsKey(resultRef) && this.responseRMIMap.get(resultRef) != null) {
            // RMI
            LOG.warn("No status code for RMI Response Result");
            return;
        } else if(this.responseXMLMap.containsKey(resultRef) && this.responseXMLMap.get(resultRef) != null) {
            // XML
            actualValue = this.responseXMLMap.get(resultRef).getStatus().getStatusCode().getValue();
        } else if(this.responseJSONMap.containsKey(resultRef) && this.responseJSONMap.get(resultRef) != null) {
            // JSON
            actualValue = this.responseJSONMap.get(resultRef).getStatus().getStatusCode().getValue();
        } else {
            throw new IllegalStateException("Couldn't find the auth result specified");
        }
        if(actualValue == null) {
            throw new IllegalStateException("The result got is null");
        } else if(! actualValue.equalsIgnoreCase(expectValue)) {
            throw new IllegalStateException(String.format(
                    "Expecting result status code '%s' but was '%s'.", expectValue, actualValue));
        } else {}
    }

    public void authResponseStatusMessageShouldBe(String resultRef, String expectValue) {
        String actualValue = null;
        if(this.responseRMIMap.containsKey(resultRef) && this.responseRMIMap.get(resultRef) != null) {
            // RMI
            LOG.warn("No status message for RMI Response Result");
            return;
        } else if(this.responseXMLMap.containsKey(resultRef) && this.responseXMLMap.get(resultRef) != null) {
            // XML
            actualValue = this.responseXMLMap.get(resultRef).getStatus().getStatusMessage();
        } else if(this.responseJSONMap.containsKey(resultRef) && this.responseJSONMap.get(resultRef) != null) {
            // JSON
            actualValue = this.responseJSONMap.get(resultRef).getStatus().getStatusMessage();
        } else {
            throw new IllegalStateException("Couldn't find the auth result specified");
        }
        if(actualValue == null) {
            throw new IllegalStateException("The result got is null");
        } else if(! actualValue.equalsIgnoreCase(expectValue)) {
            throw new IllegalStateException(String.format(
                    "Expecting result status message '%s' but was '%s'.", expectValue, actualValue));
        } else {}
    }

    public void authResponseNthObligationIdShouldBe(String resultRef, int index, String expectValue) throws Exception {
        String actualValue = null;
        if(this.responseRMIMap.containsKey(resultRef) && this.responseRMIMap.get(resultRef) != null) {
            // RMI
            LOG.warn("Not applicable for RMI Response Result");
            return;
        } else if(this.responseXMLMap.containsKey(resultRef) && this.responseXMLMap.get(resultRef) != null) {
            // XML
            actualValue = this.responseXMLMap.get(resultRef).getObligations().getObligation().get(index).getObligationId();
        } else if(this.responseJSONMap.containsKey(resultRef) && this.responseJSONMap.get(resultRef) != null) {
            // JSON
            actualValue = this.responseJSONMap.get(resultRef).getObligations().get(index).getId();
        } else {
            throw new IllegalStateException("Couldn't find the auth result specified");
        }
        if(actualValue == null) {
            throw new IllegalStateException("The result got is null");
        } else if(! actualValue.equalsIgnoreCase(expectValue)) {
            throw new IllegalStateException(String.format(
                    "Expecting result obligationId '%s' but was '%s'.", expectValue, actualValue));
        } else {}
    }

    private void logCERequest(CERequest ceReq, String level) {
        LOG.log(level, String.format("User: %s %s", ceReq.getUser().getName(), ceReq.getUser().getId()));
        if(ceReq.getUserAttributes() != null) {
            logCEAttributes(ceReq.getUserAttributes(), level);
        }
        LOG.log(level, String.format("Action: %s", ceReq.getAction()));
        LOG.log(level, String.format("Source: %s, type: %s", ceReq.getSource().getName(), ceReq.getSource().getType()));
        if(ceReq.getSourceAttributes() != null) {
            logCEAttributes(ceReq.getSourceAttributes(), level);
        }
        if(ceReq.getDest() != null) {
            LOG.log(level, String.format("Dest: %s, type: %s", ceReq.getDest().getName(), ceReq.getDest().getType()));
            if(ceReq.getDestAttributes() != null) {
                logCEAttributes(ceReq.getDestAttributes(), level);
            }
        }
        if(ceReq.getApplication() != null) {
            LOG.log(level, String.format("App: %s", ceReq.getApplication().getName()));
            if(ceReq.getApplicationAttributes() != null) {
                logCEAttributes(ceReq.getApplicationAttributes(), level);
            }
        }
        if(ceReq.getRecipients() != null) {
            for(String recipient: ceReq.getRecipients()) {
                LOG.log(level, String.format("Recipient: %s", recipient));
            }
        }
        if(ceReq.getAdditionalAttributes() != null && ceReq.getAdditionalAttributes().length != 0) {
            LOG.log(level, "Additional Attributes: ");
            for(CENamedAttributes cna: ceReq.getAdditionalAttributes()) {
                LOG.log(level, String.format("Attribute name: %s", cna.getName()));
                logCEAttributes(cna, level);
            }
        }

    }

    private void logCEAttributes(CEAttributes ceAttributes, String level) {
        for(CEAttributes.CEAttribute attr: ceAttributes.getAttributes()) {
            LOG.log(level, String.format("\t%s: %s", attr.getKey(), attr.getValue()));
        }
    }

}
