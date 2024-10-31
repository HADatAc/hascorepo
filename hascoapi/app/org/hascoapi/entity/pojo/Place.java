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

@JsonFilter("placeFilter")
public class Place extends HADatAcThing implements Comparable<Place> {

	private static final Logger log = LoggerFactory.getLogger(Place.class);

	@PropertyField(uri="foaf:name")
    protected String name;

	@PropertyField(uri="hasco:hasImage")
	private String hasImage;

 	@PropertyField(uri="schema:address")
	private String hasAddress;

	@PropertyField(uri="schema:containedInPlace")
	private String containedInPlace;

	@PropertyField(uri="schema:containsPlace")
	private String containsPlace;

	@PropertyField(uri="schema:identifier")
	private String hasIdentifier;

	@PropertyField(uri="schema:geo")
	private String hasGeo;

	@PropertyField(uri="schema:latitude")
	private String hasLatitude;

	@PropertyField(uri="schema:longitude")
	private String hasLongitude;

	@PropertyField(uri="schema:url")
	private String hasUrl;

	@PropertyField(uri="vstoi:hasSIRManagerEmail")
	private String hasSIRManagerEmail;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHasImage() {
		return hasImage;
	}

	public void setHasImage(String hasImage) {
		this.hasImage = hasImage;
	}

	public String getHasAddress() {
		return hasAddress;
	}

	public void setHasAddress(String hasAddress) {
		this.hasAddress = hasAddress;
	}

	public String getContainedInPlace() {
		return containedInPlace;
	}

	public void setContainedInPlace(String containedInPlace) {
		this.containedInPlace = containedInPlace;
	}

	public String getContainsPlace() {
        return containsPlace;
    }

    public void setContainsPlace(String containsPlace) {
        this.containsPlace = containsPlace;
    }

	public String getHasIdentifier() {
		return hasIdentifier;
	}

	public void setHasIdentifier(String hasIdentifier) {
		this.hasIdentifier = hasIdentifier;
	}

	public String getHasGeo() {
		return hasGeo;
	}

	public void setHasGeo(String hasGeo) {
		this.hasGeo = hasGeo;
	}

	public String getHasLatitude() {
		return hasLatitude;
	}

	public void setHasLatitude(String hasLatitude) {
		this.hasLatitude = hasLatitude;
	}

	public String getHasLongitude() {
		return hasLongitude;
	}

	public void setHasLongitude(String hasLongitude) {
		this.hasLongitude = hasLongitude;
	}
   
	public String getHasUrl() {
		return hasUrl;
	}

	public void setHasUrl(String hasUrl) {
		this.hasUrl = hasUrl;
	}
   
	public String getHasSIRManagerEmail() {
		return hasSIRManagerEmail;
	}

	public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
		this.hasSIRManagerEmail = hasSIRManagerEmail;
	}

    public static Place findByOriginalID(String originalID) {
        if (originalID == null || originalID.isEmpty()) {
            return null;
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* schema:Place . " +
                "          ?uri a ?subUri . " +
                "          ?uri hasco:hasOriginalId ?id .  " +
                "        FILTER (?id=\"" + originalID + "\"^^xsd:string)  . " +
                " }";
        return findOneByQuery(query);
    }        

    public static Place findByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* schema:Place . " +
                "          ?uri a ?subUri . " +
                "          ?uri foaf:name ?id .  " +
                "        FILTER (?id=\"" + name + "\"^^xsd:string)  . " +
                " }";
        return findOneByQuery(query);
    }        

    public static Place findSubclassByName(String subclass, String name) {
        if (name == null || name.isEmpty() ||
			subclass == null || subclass.isEmpty()) {
            return null;
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* <" + subclass + "> . " +
                "          ?uri a ?subUri . " +
                "          ?uri foaf:name ?id .  " +
                "        FILTER (?id=\"" + name + "\"^^xsd:string)  . " +
                " }";
        return findOneByQuery(query);
    }        

	private static Place findOneByQuery(String requestedQuery) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + requestedQuery;
        
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        String uri = null;
        Place place = null;
        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.get("uri") != null) {
                uri = soln.get("uri").toString();
                place = Place.find(uri);
            }
        }

        return place;
    }

    public static int findTotalContainsPlace(String uri) {
        if (uri == null || uri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri schema:containedInPlace <" + uri + "> .  " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        

    public static List<Place> findContainsPlace(String uri, int pageSize, int offset) {
        if (uri == null || uri.isEmpty()) {
            return new ArrayList<Place>();
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?uri schema:containedInPlace <" + uri + "> .  " +
				"          ?uri rdfs:label ?label . " +
                " } " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findManyByQuery(query);
    }        

	private static List<Place> findManyByQuery(String queryString) {
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + queryString;

		List<Place> places = new ArrayList<Place>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        if (!resultsrw.hasNext()) {
            return null;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
        	Place place = Place.find(soln.getResource("uri").getURI());
            places.add(place);
        }
        //java.util.Collections.sort((List<Place>) places);
        return places;
    }

	@Override
	public boolean equals(Object o) {
		if((o instanceof Container) && (((Container)o).getUri().equals(this.getUri()))) {
			return true;
		} else {
			return false;
		}
	}

	private static String retrieveHASCOTypeUri(String uri) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?type WHERE { " +
                " <" + uri + "> hasco:hascoType ?type ." +
                "} ";
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);
        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            return soln.getResource("type").getURI();
        }
		return null;
    }

	public static Place find(String uri) {
		//System.out.println("Place.find(): uri = [" + uri + "]");
		Place place;
		String hascoTypeUri = retrieveHASCOTypeUri(uri);
		if (hascoTypeUri == null) {
			System.out.println("[ERROR] Place.java: URI [" + uri + "] has no HASCO TYPE.");
			return null;
		}
		//System.out.println("Place.find(): typeUri = [" + typeUri + "]");
		if (hascoTypeUri.equals(SCHEMA.PLACE)) {
			place = new Place();
		} else {
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
					place.setLabel(str);
				} else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
					place.setTypeUri(str); 
				} else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
					place.setComment(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
					place.setHascoTypeUri(str);
				} else if (statement.getPredicate().getURI().equals(FOAF.NAME)) {
					place.setName(str);
				} else if (statement.getPredicate().getURI().equals(HASCO.HAS_IMAGE)) {
					place.setHasImage(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.ADDRESS)) {
					place.setHasAddress(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.CONTAINED_IN_PLACE)) {
					place.setContainedInPlace(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.CONTAINS_PLACE)) {
					place.setContainsPlace(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.IDENTIFIER)) {
					place.setHasIdentifier(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.GEO)) {
					place.setHasGeo(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.LATITUDE)) {
					place.setHasLatitude(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.LONGITUDE)) {
					place.setHasLongitude(str);
				} else if (statement.getPredicate().getURI().equals(SCHEMA.URL)) {
					place.setHasUrl(str);
				} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
					place.setHasSIRManagerEmail(str);
				}
			}
		}

		place.setUri(uri);
		
		return place;
	}

	@Override
    public int compareTo(Place another) {
        return this.getLabel().compareTo(another.getLabel());
    }

    @Override public void save() {
		saveToTripleStore();
	}

    @Override public void delete() {
		deleteFromTripleStore();
	}

}
