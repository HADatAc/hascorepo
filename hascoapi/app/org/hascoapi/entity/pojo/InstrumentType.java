package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("instrumentTypeFilter")
public class InstrumentType extends HADatAcClass implements Comparable<InstrumentType> {

	static String className = "vstoi:Instrument";
	
	private String url;

	public InstrumentType () {
		super(className);
	}

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }
    
    public String getSuperLabel() {
    	InstrumentType superInsType = InstrumentType.find(getSuperUri());
    	if (superInsType == null || superInsType.getLabel() == null) {
    		return "";
    	}
    	return superInsType.getLabel();
    }


	public static List<InstrumentType> find() {
		List<InstrumentType> instrumentTypes = new ArrayList<InstrumentType>();
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
				" SELECT ?instrumentType WHERE { " +
				" ?instrumentType rdfs:subClassOf* " + className + " . " +
				"} ";

		ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

		while (resultsrw.hasNext()) {
			QuerySolution soln = resultsrw.next();
			InstrumentType instrumentType = find(soln.getResource("instrumentType").getURI());
			instrumentTypes.add(instrumentType);
		}			

		java.util.Collections.sort((List<InstrumentType>) instrumentTypes);
		return instrumentTypes;

	}

	public static Map<String,String> getMap() {
		List<InstrumentType> list = find();
		Map<String,String> map = new HashMap<String,String>();
		for (InstrumentType typ: list) 
			map.put(typ.getUri(),typ.getLabel());
		return map;
	}

	public static InstrumentType find(String uri) {
		InstrumentType instrumentType = null;
		Statement statement;
		RDFNode object;

		String queryString = "DESCRIBE <" + uri + ">";
		Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

		instrumentType = new InstrumentType();
		StmtIterator stmtIterator = model.listStatements();

		while (stmtIterator.hasNext()) {
			statement = stmtIterator.next();
			object = statement.getObject();						
			String str = URIUtils.objectRDFToString(object);
			if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
				instrumentType.setLabel(str);
			} else if (statement.getPredicate().getURI().equals(VSTOI.HAS_WEB_DOCUMENTATION)) {
				instrumentType.setURL(str);
			} else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
				instrumentType.setSuperUri(str);
			}
		}
		
		instrumentType.setUri(uri);
		instrumentType.setLocalName(uri.substring(uri.indexOf('#') + 1));

		return instrumentType;
	}

	public static int getNumberInstrumentTypes() {
		String query = NameSpaces.getInstance().printSparqlNameSpaceList() +
				" SELECT (count(?instrumentType) as ?tot) WHERE { " +
				" ?instrumentType rdfs:subClassOf* " + className + " . " +
				"} ";

		try {
			ResultSetRewindable resultsrw = SPARQLUtils.select(
					CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);

			if (resultsrw.hasNext()) {
				QuerySolution soln = resultsrw.next();
				return Integer.parseInt(soln.getLiteral("tot").getString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int compareTo(InstrumentType another) {
		if (this.getLabel() != null && another.getLabel() != null) {
			return this.getLabel().compareTo(another.getLabel());
		}
		return this.getLocalName().compareTo(another.getLocalName());
	}

}
