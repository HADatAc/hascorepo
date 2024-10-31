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
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.vocabularies.VSTOI;

import static org.hascoapi.Constants.*;

@JsonFilter("platformInstanceFilter")
public class PlatformInstance extends VSTOIInstance {

	public PlatformInstance() {
		this.setTypeUri(VSTOI.PLATFORM_INSTANCE);
		this.setHascoTypeUri(VSTOI.PLATFORM_INSTANCE); 
	}

	public static PlatformInstance find(String uri) {
		PlatformInstance instance = new PlatformInstance();
		return (PlatformInstance)VSTOIInstance.find(instance,uri);
	} 

    public static List<PlatformInstance> findByPlaformWithPage(String platformUri, int pageSize, int offset) {
        if (platformUri == null || platformUri.isEmpty()) {
            return new ArrayList<PlatformInstance>();
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?uri rdf:type <" + platformUri + "> .  " +
				"          ?uri hasco:hascoType vstoi:PlatformInstance . " +
                " } " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findManyByQuery(query);
    }        

    public static int findTotalByPlatform(String platformUri) {
        if (platformUri == null || platformUri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE {  ?uri rdf:type <" + platformUri + "> .  " +
				"          ?uri hasco:hascoType vstoi:PlatformInstance . " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        

	private static List<PlatformInstance> findManyByQuery(String queryString) {
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + queryString;

		List<PlatformInstance> instances = new ArrayList<PlatformInstance>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);
        if (!resultsrw.hasNext()) {
            return null;
        }
        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
        	PlatformInstance instance = PlatformInstance.find(soln.getResource("uri").getURI());
            instances.add(instance);
        }
        return instances;
    }

}
