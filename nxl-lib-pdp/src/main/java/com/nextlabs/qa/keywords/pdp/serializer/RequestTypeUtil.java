package com.nextlabs.qa.keywords.pdp.serializer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;

import com.att.research.xacml.api.Attribute;
import com.att.research.xacml.api.AttributeValue;
import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.RequestAttributes;
import com.att.research.xacml.api.RequestAttributesReference;
import com.att.research.xacml.api.RequestDefaults;
import com.att.research.xacml.api.RequestReference;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ContentType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MultiRequestsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestDefaultsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;

public class RequestTypeUtil {
	
	public static RequestType convert(Request request) {
		if (request == null) {
			throw new NullPointerException("Null Request");
		} else if(request.getRequestAttributes() == null) {
			throw new IllegalArgumentException("Null Attributes for RequestType");
		} 
		
		RequestType requestType = new RequestType();
		requestType.setCombinedDecision(request.getCombinedDecision());
		requestType.setReturnPolicyIdList(request.getReturnPolicyIdList());
		
		Map<String, AttributesType> attributesTypeRef = new HashMap<>();
		
		if(request.getRequestAttributes() != null) {
			Iterator<RequestAttributes> iterRequestAttributes = request.getRequestAttributes().iterator();
			while(iterRequestAttributes.hasNext()) {
				RequestAttributes requestAttributes = iterRequestAttributes.next();
				AttributesType attributesType = parseAttributes(requestAttributes);
				requestType.getAttributes().add(attributesType);
				attributesTypeRef.put(requestAttributes.getXmlId(), attributesType);
			}
		}
		
		if(request.getMultiRequests() != null) {
			Iterator<RequestReference> iterRequestReference = request.getMultiRequests().iterator();
			MultiRequestsType multiRequestType = new MultiRequestsType();
			while(iterRequestReference.hasNext()) {
				multiRequestType.getRequestReference().add(
						parseRequestReferenceType(attributesTypeRef, iterRequestReference.next()));
			}
			requestType.setMultiRequests(multiRequestType);
		}
		
		if(request.getRequestDefaults() != null) {
			RequestDefaults requestDefaults = request.getRequestDefaults();
			requestType.setRequestDefaults(parseRequestDefaultsType(requestDefaults));
		}
		return requestType;
	}

	private static AttributesType parseAttributes(RequestAttributes requestAttributes) {
		if (requestAttributes == null) {
			throw new NullPointerException("Null AttributesType");
		} else if (requestAttributes.getCategory() == null) {
			throw new IllegalArgumentException("Null categoryId for AttributesType");
		}
		AttributesType attributesType = new AttributesType();
		// set CategoryId
		attributesType.setCategory(requestAttributes.getCategory().stringValue());
		// set Id
		if(requestAttributes.getXmlId() != null) {
			attributesType.setId(requestAttributes.getXmlId());
		}
		// set Content
		if(requestAttributes.getContentRoot() != null) {
			ContentType contentType = new ContentType();
			Node nodeContentRoot = requestAttributes.getContentRoot();
			contentType.getContent().add(nodeContentRoot);
			attributesType.setContent(contentType);
		}
		// set Attribute list
		Iterator<Attribute> iterAttribute = requestAttributes.getAttributes().iterator();
		while(iterAttribute.hasNext()) {
			attributesType.getAttribute().add(parseAttributeType(iterAttribute.next()));
		}
		
		return attributesType;
	}
	
	private static AttributeType parseAttributeType(Attribute attribute) {
		if(attribute == null) {
			throw new NullPointerException("Null Attribute");
		} else if(attribute.getAttributeId() == null) {
			throw new IllegalArgumentException("Null AttributeId for AttributeType");
		} else if(attribute.getValues() == null) {
			throw new IllegalArgumentException("Null AttributeValue for AttributeType");
		}
		
		AttributeType attributeType = new AttributeType();
		// set AttributeId
		attributeType.setAttributeId(attribute.getAttributeId().stringValue());
		// set Issuer
		attributeType.setIssuer(attribute.getIssuer());
		// set IncludeInResult
		attributeType.setIncludeInResult(attribute.getIncludeInResults());
		// set AttributeValue
		Iterator<AttributeValue<?>> iterAttributeValue = attribute.getValues().iterator();
		while(iterAttributeValue.hasNext()) {
			attributeType.getAttributeValue().add(parseAttributeValueType(iterAttributeValue.next()));
		}
		return attributeType;
	}

	private static AttributeValueType parseAttributeValueType(AttributeValue<?> attributeValue) {
		if(attributeValue == null) {
			throw new NullPointerException("Null attributeValue");
		} else if(attributeValue.getDataTypeId() == null) {
			throw new IllegalArgumentException("Null DataType for AttributeValueType");
		}
		AttributeValueType attributeValueType = new AttributeValueType();
		// set DataType
		attributeValueType.setDataType(attributeValue.getDataTypeId().stringValue());
		// set Content
		attributeValueType.getContent().add(attributeValue.getValue());
		
		return attributeValueType;
	}

	private static RequestReferenceType parseRequestReferenceType(Map<String, AttributesType> attributesTypeRef, RequestReference requestReference) {
		if(attributesTypeRef == null) {
			throw new NullPointerException("Null attributesTypeRef");
		} else if (requestReference == null) {
			throw new NullPointerException("Null RequestReference");
		} else if (requestReference.getAttributesReferences() == null || requestReference.getAttributesReferences().size() == 0) {
			throw new IllegalArgumentException("No AttributesReference in RequestReference");
		}
		RequestReferenceType requestReferenceType = new RequestReferenceType();
		Iterator<RequestAttributesReference> iterAttributesReferences = requestReference.getAttributesReferences().iterator();
		while(iterAttributesReferences.hasNext()) {
			requestReferenceType.getAttributesReference().add(parseAttributesReferenceType(attributesTypeRef, iterAttributesReferences.next()));
		}
		return requestReferenceType;
	}
	
	private static AttributesReferenceType parseAttributesReferenceType(Map<String, AttributesType> attributesTypeRef, RequestAttributesReference requestAttributesReference) {
		if(attributesTypeRef == null) {
			throw new NullPointerException("Null attributesTypeRef");
		} else if (requestAttributesReference == null) {
			throw new NullPointerException("Null AttributesReference");
		} else if (requestAttributesReference.getReferenceId() == null) {
			throw new IllegalArgumentException("Null referenceId for AttributesReference");
		}
		AttributesReferenceType attributesReferenceType = new AttributesReferenceType();
		attributesReferenceType.setReferenceId(
				attributesTypeRef.get(requestAttributesReference.getReferenceId()));
		return attributesReferenceType;
	}

	private static RequestDefaultsType parseRequestDefaultsType(RequestDefaults requestDefaults) {
		if (requestDefaults == null) {
			throw new NullPointerException("Null RequestDefaults");
		}
		RequestDefaultsType requestDefaultsType = new RequestDefaultsType();
		
		String uriXPathVersion = "";
		if(requestDefaults.getXPathVersion() != null) {
			uriXPathVersion = requestDefaults.getXPathVersion().toString();
		}
		requestDefaultsType.setXPathVersion(uriXPathVersion);
		return requestDefaultsType;
	}
}
