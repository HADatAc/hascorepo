package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.text.WordUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyObjectCollectionType extends HADatAcClass implements Comparable<StudyObjectCollectionType> {

	private static final Logger log = LoggerFactory.getLogger(StudyObjectCollection.class);

	static String className = "hasco:StudyObjectCollection";
	String studyObjectTypeUri = "";
	String acronym = "";
	String labelFragment = "";

	public StudyObjectCollectionType () {
		super(className);
		studyObjectTypeUri = "";
		acronym = "";
		labelFragment = "";
	}

	public String getStudyObjectTypeUri() {
		return studyObjectTypeUri;
	}

	public StudyObjectType getStudyObjectType() {
		if (studyObjectTypeUri == null || studyObjectTypeUri.equals("")) {
			return null;
		}
		return StudyObjectType.find(studyObjectTypeUri);
	}

	public void setStudyObjectTypeUri(String studyObjectTypeUri) {
		this.studyObjectTypeUri = studyObjectTypeUri;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getLabelFragment() {
		return labelFragment;
	}

	public void setLabelFragment(String  labelFragment) {
		this.labelFragment = labelFragment;
	}

	public static List<StudyObjectCollectionType> find() {
		List<StudyObjectCollectionType> socTypes = new ArrayList<StudyObjectCollectionType>();
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
				" SELECT ?uri WHERE { " +
				"    ?uri rdfs:subClassOf* " + className + " . " + 
				"} ";

		// System.out.println("Query: " + queryString);
		
		ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

		while (resultsrw.hasNext()) {
			QuerySolution soln = resultsrw.next();
			StudyObjectCollectionType socType = find(soln.getResource("uri").getURI());
			socTypes.add(socType);
		}			

		java.util.Collections.sort((List<StudyObjectCollectionType>) socTypes);
		
		return socTypes;
	}

	public static Map<String,String> getMap() {
		List<StudyObjectCollectionType> list = find();
		Map<String,String> map = new HashMap<String,String>();
		for (StudyObjectCollectionType typ: list) 
			map.put(typ.getUri(),typ.getLabel());
		return map;
	}

	public static StudyObjectCollectionType find(String uri) {
		StudyObjectCollectionType socType = null;
		Statement statement;
		RDFNode object;

		String queryString = "DESCRIBE <" + uri + ">";
		Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

		socType = new StudyObjectCollectionType();
		StmtIterator stmtIterator = model.listStatements();

		while (stmtIterator.hasNext()) {
			statement = stmtIterator.next();
			object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
			if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
				socType.setLabel(str);
			} else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
				socType.setSuperUri(str);
			} else if (statement.getPredicate().getURI().equals(HASCO.HAS_STUDY_OBJECT_TYPE)) {
				socType.setStudyObjectTypeUri(str);
			} else if (statement.getPredicate().getURI().equals(HASCO.HAS_ACRONYM)) {
				socType.setAcronym(str);
			} else if (statement.getPredicate().getURI().equals(HASCO.HAS_STUDY_OBJECT_TYPE)) {
				socType.setLabelFragment(str);
			}
		}

		socType.setUri(uri);
		socType.setLocalName(uri.substring(uri.indexOf('#') + 1));

		return socType;
	}
	
	@Override
    public boolean equals(Object o) {
        if((o instanceof StudyObjectCollectionType) && (((StudyObjectCollectionType)o).getUri().equals(this.getUri()))) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return getUri().hashCode();
    }

	@Override
	public int compareTo(StudyObjectCollectionType another) {
		if (this.getLabel() != null && another.getLabel() != null) {
			return this.getLabel().compareTo(another.getLabel());
		}
		return this.getLocalName().compareTo(another.getLocalName());
	}
}
