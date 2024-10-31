package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.VSTOI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@JsonFilter("semanticVariableFilter")
public class SemanticVariable extends HADatAcThing {

	private static final String className = "hasco:SemanticVariable";

	private static final Logger log = LoggerFactory.getLogger(SemanticVariable.class);
	public static final String EMPTY_CONTENT = "n/a";

	// Mandatory properties for SemanticVariable

	@PropertyField(uri="hasco:hasEntity", valueType=PropertyValueType.URI)
	private String entUri;
	private String entLabel;

	@PropertyField(uri="hasco:hasAttribute", valueType=PropertyValueType.URI)
	private String attrUri;
	private String attrLabel;

	// Optional properties for SemanticVariables

	@PropertyField(uri="hasco:hasRole")
	private String role;

	@PropertyField(uri="hasco:inRelationTo", valueType=PropertyValueType.URI)
    private String inRelationToUri;
	private String inRelationToLabel;

    private String relation;

	@PropertyField(uri="hasco:hasUnit", valueType=PropertyValueType.URI)
	private String unitUri;
	private String unitLabel;

	@PropertyField(uri="hasco:hasEvent", valueType=PropertyValueType.URI)
    private String timeAttrUri;
	private String timeAttrLabel;

	@PropertyField(uri="hasco:isCategorical")
	private boolean isCategorical;

	@PropertyField(uri="vstoi:hasVersion")
	private String hasVersion;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

	private Map<String, String> relations = new HashMap<String, String>();

	//private static Map<String, SemanticVariable> semVarCache;

	//private static Map<String, SemanticVariable> getCache() {
	//	if (semVarCache == null) {
	//		semVarCache = new HashMap<String, SemanticVariable>();
	//	}
	//	return semVarCache;
	//}
	//public static void resetCache() {
	//	semVarCache = null;
	//}

	public SemanticVariable() {
    	this.typeUri = HASCO.SEMANTIC_VARIABLE;
    	this.hascoTypeUri = HASCO.SEMANTIC_VARIABLE;
	}

    public SemanticVariable(String label, AlignmentEntityRole entRole, AttributeInRelationTo attrInRel) {
    	this(label, entRole, attrInRel, null, null);
    }

    public SemanticVariable(String label, AlignmentEntityRole entRole, AttributeInRelationTo attrInRel, Unit unit) {
    	this(label, entRole, attrInRel, unit, null);
    }

    public SemanticVariable(String label, AlignmentEntityRole entRole, AttributeInRelationTo attrInRel, Unit unit, Attribute timeAttr) {
		this(HASCO.SEMANTIC_VARIABLE, HASCO.SEMANTIC_VARIABLE, label, entRole.getEntity(), entRole.getRole(), attrInRel.getAttribute(),
				attrInRel.getInRelationTo(), unit, timeAttr, false);
    }

	public SemanticVariable(String typeUri, String hascoTypeUri , String label, Entity ent, String role, Attribute attr, Entity inRelationTo, Unit unit, Attribute timeAttr, boolean isCategorical) {
		this.typeUri = typeUri;
		this.hascoTypeUri = hascoTypeUri;
		this.label = label;
		if (ent != null) {
			this.entUri = ent.getUri();
		}
		this.role = role;
		if (attr != null) {
			this.attrUri = attr.getUri();
		}
		if (inRelationTo != null) {
			this.inRelationToUri = inRelationTo.getUri();
		}
		if (unit != null) {
			this.unitUri = unit.getUri();
		}
		if (timeAttr != null) {
			this.timeAttrUri = timeAttr.getUri();
		}
		this.isCategorical = false;
	}

	/*
	public String getKey() {
		String getRoleFinal = "";
		String getEntityFinal = "";
		String getAttributeFinal = "";
		String getInRelationToFinal = "";
		String getUnitFinal = "";
		String getTimeFinal = "";
		if (getRole() != null) {
			getRoleFinal = getRole();
		}
		if (getEntityStr() != null) {
			getEntityFinal = getEntityStr();
		}
		if (getAttributeStr() != null) {
			getAttributeFinal = getAttributeStr();
		}
		if (getInRelationToStr() != null) {
			getInRelationToFinal = getInRelationToStr();
		}
		if (getUnitStr() != null) {
			getUnitFinal = getUnitStr();
		}
		if (getTimeStr() != null) {
			getTimeFinal = getTimeStr();
		}
    	return getRoleFinal + getEntityFinal + getAttributeFinal + getInRelationToFinal + getUnitFinal + getTimeFinal;
    }
	*/

	@Override
	public String getLabel() {
		if (this.label == null || this.label.isEmpty()) {
			return this.toString();
		}
		return this.label;
	}

	public Entity getEntity() {
		Entity ent = Entity.find(this.entUri);
    	return ent;
    }

    public String getEntityLabel() {
		if (this.entLabel != null) {
			return this.entLabel;
		}
		Entity ent = getEntity();
		if (ent == null) {
			this.entLabel = "";
			return this.entLabel;
		}
		return ent.getLabel();
	}

	public String getEntityUri() {
		return this.entUri;
	}

	public String getEntityStr() {
        if (entUri == null || entUri.isEmpty()) {
        	return "";
        }
    	return entUri;
    }

    public void setEntityUri(String entUri) {
    	this.entUri = entUri;
	}

    public String getRole() {
    	if (role == null) {
    		return "";
    	}
    	return role;
    }

	public void setRole(String role) {
		this.role = role;
	}

    public Attribute getAttribute() {
		if (this.attrUri == null || this.attrUri.isEmpty()) {
			return null;
		}
		return Attribute.find(this.attrUri);
    }

	public void setAttributeUri(String attrUri) {
    	this.attrUri = attrUri;
	}

	public String getAttributeUri() {
		return this.attrUri;
	}

	public String getAttributeStr() {
        if (attrUri == null || attrUri.isEmpty()) {
            return "";
        }
    	return attrUri;
    }

    public String getAttributeLabel() {
		if (this.attrLabel != null) {
			return this.attrLabel;
		}
		Attribute attr = getAttribute();
		if (attr == null) {
			this.attrLabel = "";
			return this.attrLabel;
		}
		return attr.getLabel();
	}

	public Entity getInRelationTo() {
		if (this.inRelationToUri == null || this.inRelationToUri.isEmpty()) {
			return null;
		}
		return Entity.find(this.inRelationToUri);
    }

	public String getInRelationToUri() {
		return inRelationToUri;
	}

	public void setInRelationToUri(String inRelationToUri) {
    	this.inRelationToUri = inRelationToUri;
	}

	public String getInRelationToStr() {
        if (inRelationToUri == null || inRelationToUri.isEmpty()) {
            return "";
        }
    	return inRelationToUri;
    }

	public String getInRelationToLabel() {
		if (this.inRelationToLabel != null) {
			return this.inRelationToLabel;
		}
		Entity inRelationTo = getInRelationTo();
		if (inRelationTo == null) {
			this.inRelationToLabel = "";
			return this.inRelationToLabel;
		}
		return inRelationTo.getLabel();
	}

	public List<String> getRelationsList() {
		return new ArrayList(relations.values());
	}

	public void addRelation(String key, String relation) {
		relations.put(key, relation);
	}

	public Unit getUnit() {
		if (unitUri == null || unitUri.isEmpty()) {
			return null;
		}
    	return Unit.find(unitUri);
    }

	public void setUnitUri(String unitUri) {
    	this.unitUri = unitUri;
	}

	public String getUnitUri() {
		return unitUri;
	}

	public String getUnitStr() {
        if (unitUri == null || unitUri.isEmpty()) {
            return "";
        }
    	return unitUri;
    }

	public String getUnitLabel() {
		if (this.unitLabel != null) {
			return this.unitLabel;
		}
		Unit unit = getUnit();
		if (unit == null) {
			this.unitLabel = "";
			return this.unitLabel;
		}
		return unit.getLabel();
	}

	public Attribute getTime() {
  		if (timeAttrUri == null || timeAttrUri.isEmpty()) {
  			return null;
		}
    	return Attribute.find(timeAttrUri);
    }

	public void setTimeUri(String timeAttrUri) {
		this.timeAttrUri = timeAttrUri;
	}

	public String getTimeUri() {
		return timeAttrUri;
	}

	public String getTimeStr() {
        if (timeAttrUri == null || timeAttrUri.isEmpty()) {
            return "";
        }
    	return timeAttrUri;
    }

	public String getTimeLabel() {
		if (this.timeAttrLabel != null) {
			return this.timeAttrLabel;
		}
		Attribute timeAttr = getTime();
		if (timeAttr == null) {
			this.timeAttrLabel = "";
			return this.timeAttrLabel;
		}
		return timeAttr.getLabel();
	}

	public boolean getIsCategorical() {
		return isCategorical;
	}

	public boolean isCategorical() {
		return isCategorical;
	}

	public void setIsCategorical(boolean isCategorical) {
    	this.isCategorical = isCategorical;
	}

	public void setHasVersion(String hasVersion) {
    	this.hasVersion = hasVersion;
	}

	public String getHasVersion() {
		return this.hasVersion;
	}

	public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
    	this.hasSIRManagerEmail = hasSIRManagerEmail;
	}

	public String getHasSIRManagerEmail() {
		return this.hasSIRManagerEmail;
	}

	public static String upperCase(String orig) {
    	String[] words = orig.split(" ");
    	StringBuffer sb = new StringBuffer();

    	for (int i = 0; i < words.length; i++) {
    		sb.append(Character.toUpperCase(words[i].charAt(0)))
    		.append(words[i].substring(1)).append(" ");
    	}          
    	return sb.toString().trim();
    }      

    public static String prep(String orig) {
    	String aux = upperCase(orig);
    	return aux.replaceAll(" ","-").replaceAll("[()]","");
    }

    public static SemanticVariable find(String uri) {   

        SemanticVariable sv = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        sv = new SemanticVariable();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                sv.setLabel(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
				if (object.asResource().getURI().equals(HASCO.SEMANTIC_VARIABLE)) {
                	sv.setTypeUri(HASCO.SEMANTIC_VARIABLE);
				}
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                sv.setComment(object.asLiteral().getString());
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                sv.setHascoTypeUri(object.asResource().getURI());
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ENTITY)) {
                try {
                    sv.setEntityUri(object.asResource().getURI());
                } catch (Exception e) {			
                    sv.setEntityUri(null);
                }
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_ATTRIBUTE)) {
                try {
                    sv.setAttributeUri(object.asResource().getURI());
                } catch (Exception e) {
                    sv.setAttributeUri(null);
                }
            } else if (statement.getPredicate().getURI().equals(HASCO.IN_RELATION_TO)) {
                try {
                    sv.setInRelationToUri(object.asResource().getURI());
                } catch (Exception e) {
                    sv.setInRelationToUri(null);
                }
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_UNIT)) {
                try {
                    sv.setUnitUri(object.asResource().getURI());
                } catch (Exception e) {
                    sv.setUnitUri(null);
                }
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                try {
                    sv.setHasVersion(object.asLiteral().getString());
                } catch (Exception e) {
                    sv.setUnitUri(null);
                }
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                try {
                    sv.setHasSIRManagerEmail(object.asLiteral().getString());
                } catch (Exception e) {
                    sv.setHasSIRManagerEmail(null);
                }
			}
        }

        sv.setUri(uri);

        return sv;
    }

	public static String toString(String role, Entity ent, Attribute attr, Entity inRelationTo, Unit unit, Attribute timeAttr) {
		//System.out.println("[" + attr.getLabel() + "]");
		String str = "";
		if (role != null && !role.isEmpty()) {
			str += prep(role) + "-";
		}
		if (ent != null && ent.getLabel() != null && !ent.getLabel().isEmpty()) {
			str += prep(ent.getLabel());
		}
		if (attr != null && attr.getLabel() != null && !attr.getLabel().isEmpty()) {
			str += "-" + prep(attr.getLabel());
		}
		if (inRelationTo != null && !inRelationTo.getLabel().isEmpty()) {
			str += "-" + prep(inRelationTo.getLabel());
		}
		if (unit != null && unit.getLabel() != null && !unit.getLabel().isEmpty()) {
			str += "-" + prep(unit.getLabel());
		}
		if (timeAttr != null && timeAttr.getLabel() != null && !timeAttr.getLabel().isEmpty()) {
			str += "-" + prep(timeAttr.getLabel());
		}
		return str;
	}

	@Override
	public void save() {
		this.saveToTripleStore();
	}

	@Override
	public void delete() {
		this.deleteFromTripleStore();
	}

}
