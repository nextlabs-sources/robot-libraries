package com.nextlabs.qa.keywords.pdp;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;

/**
 * Created by sduan on 23/11/2015.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-test.xml"})
public class PDPRequestHelperTest {

    @Autowired
    private PDPRequestHelper pdpRequestHelper;

    @Test
    public void testAssembleRequest() throws Exception {

        pdpRequestHelper.reset();

        String user0Ref = pdpRequestHelper.createUser("S-1-5-21-830805687-550985140-3285839444-1197", null);
        String user1Ref = pdpRequestHelper.createUser("S-1-5-21-2686955786-3010013143-2770737529-5172", "Duan");

        pdpRequestHelper.addAttribute(user0Ref, "dept", "Dept1");
        pdpRequestHelper.addAttribute(user1Ref, "dept", "Dept2");

        String actionRef = pdpRequestHelper.createAction("open");

        String applicationRef = pdpRequestHelper.createApplication("word");

        String resource0Ref = pdpRequestHelper.createResource("Document1.doc", "fso", "from");
        pdpRequestHelper.addAttribute(resource0Ref, "table", "customer");

        String resource1Ref = pdpRequestHelper.createResource("Document2.doc", "fso", "from");
        pdpRequestHelper.addAttribute(resource1Ref, "table", "employee");

        String host1Ref = pdpRequestHelper.createHost("10.63.0.1");

        String recipient0Ref = pdpRequestHelper.createRecipient("shiqiang.duan@nextlabs.com");
        String recipient1Ref = pdpRequestHelper.createRecipient("nobody@nextlabs.com");

//        URL testRequestUri = getClass().getResource("/Samplepql.txt");
//        File testRequestFile = new File(testRequestUri.toURI());
//        BufferedReader br = new BufferedReader(new FileReader(testRequestFile));
//        StringBuffer sb = new StringBuffer();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line + "\n");
//        }
//        br.close();
//        String pql = sb.toString();
//
//        String pod1Ref = pdpRequestHelper.createPolicyOnDemand(pql, false);
//
//        String addiData1Ref = pdpRequestHelper.createAdditionalData("Computer");

        String request0Ref = pdpRequestHelper.assembleRequest(
                new String[]{user0Ref, actionRef, applicationRef, resource0Ref, host1Ref, recipient0Ref, recipient1Ref});
        String request1Ref = pdpRequestHelper.assembleRequest(
                new String[]{user1Ref, actionRef, applicationRef, resource1Ref, host1Ref, recipient1Ref});


//        String multiReqRef = pdpRequestHelper.assembleMultiRequest(new String[]{request0Ref, request1Ref});

        System.out.println(pdpRequestHelper.serializeRequest(request0Ref, PDPRequestHelper.SERIALIZE_FORMAT_JSON));
//        System.out.println(pdpRequestHelper.serializeRequest(request1Ref, PDPRequestHelper.SERIALIZE_FORMAT_JSON));
//        System.out.println(pdpRequestHelper.serializeRequest(request2Ref, PDPRequestHelper.SERIALIZE_FORMAT_JSON));
//        System.out.println(pdpRequestHelper.serializeRequest(multiReqRef, PDPRequestHelper.SERIALIZE_FORMAT_JSON));

        System.out.println(pdpRequestHelper.serializeRequest(request0Ref, PDPRequestHelper.SERIALIZE_FORMAT_XML));
//        System.out.println(pdpRequestHelper.serializeRequest(request1Ref, PDPRequestHelper.SERIALIZE_FORMAT_XML));
//        System.out.println(pdpRequestHelper.serializeRequest(request2Ref, PDPRequestHelper.SERIALIZE_FORMAT_XML));
//        System.out.println(pdpRequestHelper.serializeRequest(multiReqRef, PDPRequestHelper.SERIALIZE_FORMAT_XML));


        List<String> results0 = pdpRequestHelper.invokeAuthRMIRequest(request0Ref);
//        List<String> results2 = pdpRequestHelper.invokeAuthRMIRequest(request2Ref);
//        List<String> results3 = pdpRequestHelper.invokeAuthRMIRequest(multiReqRef);

//        List<String> jsonResults0 = pdpRequestHelper.invokeAuthJSONRequest(request0Ref);
//        List<String> jsonResults1 = pdpRequestHelper.invokeAuthJSONRequest(request1Ref);
//        List<String> jsonResults2 = pdpRequestHelper.invokeAuthJSONRequest(request2Ref);

//        List<String> xmlResults0 = pdpRequestHelper.invokeAuthXMLRequest(request0Ref);
//        List<String> xmlResults1 = pdpRequestHelper.invokeAuthXMLRequest(request0Ref);
//        List<String> xmlResults2 = pdpRequestHelper.invokeAuthXMLRequest(request0Ref);
//        pdpRequestHelper.invokeAuthJSONRequest(request2Ref);
//        pdpRequestHelper.invokeAuthJSONRequest(multiReqRef);
//
//        pdpRequestHelper.invokeAuthXMLRequest(request1Ref);
//        pdpRequestHelper.invokeAuthXMLRequest(request2Ref);
//        pdpRequestHelper.invokeAuthXMLRequest(multiReqRef);

//        pdpRequestHelper.authResponseEffectShouldBe(results0.get(0), "allow");
//        pdpRequestHelper.authResponseEffectShouldBe(jsonResults0.get(0), "Permit");
//        pdpRequestHelper.authResponseEffectShouldBe(xmlResults0.get(0), "Permit");

//        pdpRequestHelper.authResponseObligationsCountShouldBe(results0.get(0), 2);
//        pdpRequestHelper.authResponseObligationsCountShouldBe(jsonResults0.get(0), 2);
//        pdpRequestHelper.authResponseObligationsCountShouldBe(xmlResults0.get(0), 2);

//        pdpRequestHelper.authResponseStatusCodeShouldBe(jsonResults0.get(0), "urn:oasis:names:tc:xacml:1.0:status:ok");
//        pdpRequestHelper.authResponseStatusCodeShouldBe(xmlResults0.get(0), "urn:oasis:names:tc:xacml:1.0:status:ok");

//        pdpRequestHelper.authResponseStatusMessageShouldBe(jsonResults0.get(0), "success");
//        pdpRequestHelper.authResponseStatusMessageShouldBe(xmlResults0.get(0), "success");

//        pdpRequestHelper.authResponseNthObligationIdShouldBe(jsonResults0.get(0), 0, "FSADAPTER");
//        pdpRequestHelper.authResponseNthObligationIdShouldBe(jsonResults0.get(0), 1, "CE::NOTIFY");

//        pdpRequestHelper.authResponseNthObligationIdShouldBe(xmlResults0.get(0), 0, "FSADAPTER");
//        pdpRequestHelper.authResponseNthObligationIdShouldBe(xmlResults0.get(0), 1, "CE::NOTIFY");

    }

}
