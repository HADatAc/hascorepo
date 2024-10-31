package org.hascoapi.entity.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class AttributeInRelationTo {

    private Attribute attr;
    private Entity inRelationTo;
    
    public AttributeInRelationTo(Attribute attr, Entity inRelationTo) {
	   this.attr = attr;
	   this.inRelationTo = inRelationTo;
    }

    public String getKey() {
    	if (attr == null) {
    		return "";
    	}
    	String attrUri = attr.getUri();
    	if (inRelationTo == null || inRelationTo.getUri().isEmpty()) {
    		return attrUri;
    	}
    	return attrUri + inRelationTo.getUri();
    }

    public Attribute getAttribute() {
    	return attr;
    }

    public Entity getInRelationTo() {
    	return inRelationTo;
    }

    public String toString() {
    	if (attr == null) {
    		System.out.println("ERROR: AttributeInRelationTo: called toString() with null argument.");
    		return "";
    	}
    	String label = attr.getLabel();
    	if (inRelationTo == null || inRelationTo.equals("")) {
    		return label;
    	} else {
    		return label + "-" + inRelationTo;
    	}
    }

}
