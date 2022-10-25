package com.nextlabs.qa.keywords.pdp.beans;

import java.util.HashMap;
import java.util.Map;

import com.bluejungle.destiny.agent.pdpapi.IPDPNamedAttributes;
import com.bluejungle.framework.utils.DynamicAttributes;

/**
 * This is a wrapper class for {@link IPDPNamedAttributes}, it implements equals and hashCode methods
 * </br>
 * It can be used to compare to {@link IPDPNamedAttributes} objects and use HashSet to remove duplicates
 * 
 * @author sduan
 *
 */
public class PDPNamedAttributesWrapper {
	
	private IPDPNamedAttributes ipdpNamedAttributes;
	
	
	public PDPNamedAttributesWrapper(IPDPNamedAttributes ipdpNamedAttributes) {
		this.ipdpNamedAttributes = ipdpNamedAttributes;
	}
	
	
	public IPDPNamedAttributes getIpdpNamedAttributes() {
		return ipdpNamedAttributes;
	}


	public void setIpdpNamedAttributes(IPDPNamedAttributes ipdpNamedAttributes) {
		this.ipdpNamedAttributes = ipdpNamedAttributes;
	}


	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(this == obj) {
			return true;
		}
		if(! (obj instanceof PDPNamedAttributesWrapper)) {
			return false;
		}
		if( ((PDPNamedAttributesWrapper) obj).getIpdpNamedAttributes() == this.getIpdpNamedAttributes() ) {
			return true;
		}
		if( this.ipdpNamedAttributes == null ||
				((PDPNamedAttributesWrapper) obj).getIpdpNamedAttributes() == null ) {
			return false;
		}
		// first compare the name
		if( !(this.ipdpNamedAttributes.getName().equals(
				((PDPNamedAttributesWrapper) obj).getIpdpNamedAttributes().getName())) ) {
			return false;
		}
		
		Map<String, DynamicAttributes> attrMap = new HashMap<>(1);
		this.ipdpNamedAttributes.addSelfToMap(attrMap);
		DynamicAttributes attr = attrMap.get(this.ipdpNamedAttributes.getName());
		
		Map<String, DynamicAttributes> objAttrMap = new HashMap<>(1);
		((PDPNamedAttributesWrapper) obj).getIpdpNamedAttributes().addSelfToMap(objAttrMap);
		DynamicAttributes objAttr = objAttrMap.get(
				((PDPNamedAttributesWrapper) obj).getIpdpNamedAttributes().getName());
		
		if(attr.equals(DynamicAttributes.EMPTY) && objAttr.equals(DynamicAttributes.EMPTY)) {
			return true;
		}
		
		return attr.entrySet().equals(objAttr.entrySet());
		
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		if(this.ipdpNamedAttributes == null) {
			return hashCode;
		}
		hashCode = 31*hashCode + this.ipdpNamedAttributes.getName().hashCode();
		
		Map<String, DynamicAttributes> attrMap = new HashMap<>(1);
		this.ipdpNamedAttributes.addSelfToMap(attrMap);
		DynamicAttributes attr = attrMap.get(this.ipdpNamedAttributes.getName());
		
		if(attr == null) {
			return hashCode;
		}
		hashCode = 31*hashCode + attr.entrySet().hashCode();
		
		return hashCode;
	}
	
}