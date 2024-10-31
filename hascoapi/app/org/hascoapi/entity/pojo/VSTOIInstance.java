package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.Utils;
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.VSTOI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hascoapi.Constants.*;

@JsonFilter("vstoiInstanceFilter")
public abstract class VSTOIInstance extends HADatAcThing implements Comparable<VSTOIInstance> {

	private static final Logger log = LoggerFactory.getLogger(VSTOIInstance.class);

	@PropertyField(uri="vstoi:hasSerialNumber")
    protected String hasSerialNumber;

	@PropertyField(uri="vstoi:hasAcquisitionDate")
	private String hasAcquisitionDate;

	@PropertyField(uri="vstoi:isDamaged")
	private String isDamaged;

	@PropertyField(uri="vstoi:hasDamageDate")
	private String hasDamageDate;

	@PropertyField(uri="vstoi:hasSIRManagerEmail")
	private String hasSIRManagerEmail;

	public String getHasSerialNumber() {
		return hasSerialNumber;
	}
	public void setHasSerialNumber(String hasSerialNumber) {
		this.hasSerialNumber = hasSerialNumber;
	}

	public String getHasAcquisitionDate() {
		return hasAcquisitionDate;
	}
	public void setHasAcquisitionDate(String hasAcquisitionDate) {
		this.hasAcquisitionDate = hasAcquisitionDate;
	}

	public String getIsDamaged() {
		return isDamaged;
	}
	public void setIsDamaged(String isDamaged) {
		this.isDamaged = isDamaged;
	}

	public String getHasDamageDate() {
		return hasDamageDate;
	}
	public void setHasDamageDate(String hasDamageDate) {
		this.hasDamageDate = hasDamageDate;
	}

	public String getHasSIRManagerEmail() {
		return hasSIRManagerEmail;
	}
	public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
		this.hasSIRManagerEmail = hasSIRManagerEmail;
	}

	public HADatAcClass getType() {
		if (this.getTypeUri() == null || this.getTypeUri().isEmpty()) {
			return null;
		}
		return HADatAcClass.lightWeightedFind(this.getTypeUri()); 
	}

	/* 
	private static List<VSTOIInstance> findManyByQuery(String queryString) {
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + queryString;

		List<VSTOIInstance> instances = new ArrayList<Place>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        if (!resultsrw.hasNext()) {
            return null;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
        	VSTOIInstance instance = Fin.find(soln.getResource("uri").getURI());
            places.add(place);
        }
        //java.util.Collections.sort((List<Place>) places);
        return places;
    }
		*/

	@Override
	public boolean equals(Object o) {
		if((o instanceof Container) && (((Container)o).getUri().equals(this.getUri()))) {
			return true;
		} else {
			return false;
		}
	}

	public static VSTOIInstance find(VSTOIInstance instance, String uri) {
		//System.out.println("Place.find(): uri = [" + uri + "]");
		if (uri == null || uri.isEmpty()) {
			return null;
		}

	    Statement statement;
	    RDFNode object;
	    
	    String queryString = "DESCRIBE <" + uri + ">";
	    Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
		
		StmtIterator stmtIterator = model.listStatements();

		if (!stmtIterator.hasNext()) {
			return null;
		} 
		
		while (stmtIterator.hasNext()) {
		    statement = stmtIterator.next();
		    object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
			if (uri != null && !uri.isEmpty()) {
				if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
					instance.setLabel(str);
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					instance.setTypeUri(str); 
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					instance.setComment(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_ACQUISITION_DATE)) {
					instance.setHasAcquisitionDate(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SERIAL_NUMBER)) {
					instance.setHasSerialNumber(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.IS_DAMAGED)) {
					instance.setIsDamaged(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_DAMAGE_DATE)) {
					instance.setHasDamageDate(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					instance.setHasSIRManagerEmail(str);
				}
			}
		}

		instance.setUri(uri);
		
		return instance;
	}

	@Override
    public int compareTo(VSTOIInstance another) {
        return this.getLabel().compareTo(another.getLabel());
    }

    @Override public void save() {
		saveToTripleStore();
	}

    @Override public void delete() {
		deleteFromTripleStore();
	}

}
