package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.text.WordUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonFilter("studyObjectFilter")
public class StudyObjectType extends HADatAcClass implements Comparable<StudyObjectType> {

    private static final Logger log = LoggerFactory.getLogger(StudyObjectType.class);

    static String className = "hasco:StudyObject";

    public StudyObjectType () {
        super(className);
    }

    public static List<StudyObjectType> find() {
        List<StudyObjectType> objectTypes = new ArrayList<StudyObjectType>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + className + " . " + 
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            StudyObjectType objectType = find(soln.getResource("uri").getURI());
            objectTypes.add(objectType);
        }			

        java.util.Collections.sort((List<StudyObjectType>) objectTypes);
        
        return objectTypes;
    }

    public static Map<String,String> getMap() {
        List<StudyObjectType> list = find();
        Map<String,String> map = new HashMap<String,String>();
        for (StudyObjectType typ: list) 
            map.put(typ.getUri(),typ.getLabel());
        return map;
    }

    public static StudyObjectType find(String uri) {
        StudyObjectType objectType = null;
        
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        objectType = new StudyObjectType();
        StmtIterator stmtIterator = model.listStatements();

		while (stmtIterator.hasNext()) {
			statement = stmtIterator.next();
			object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
			if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
				objectType.setLabel(str);
			} else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
				objectType.setSuperUri(str);
			}
		}

        objectType.setUri(uri);
        objectType.setLocalName(uri.substring(uri.indexOf('#') + 1));

        return objectType;
    }
    
    @Override
    public boolean equals(Object o) {
        if((o instanceof StudyObjectType) && (((StudyObjectType)o).getUri().equals(this.getUri()))) {
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
    public int compareTo(StudyObjectType another) {
        if (this.getLabel() != null && another.getLabel() != null) {
            return this.getLabel().compareTo(another.getLabel());
        }
        return this.getLocalName().compareTo(another.getLocalName());
    }
}
