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
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SIO;

@JsonFilter("entityFilter")
public class Entity extends HADatAcClass implements Comparable<Entity> {

    static String className = SIO.ENTITY;

    public Entity() {
        super(className);
    }

    /** 
    public static List<Entity> find() {
        List<Entity> entities = new ArrayList<Entity>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* <" + SIO.ENTITY + "> . " +
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            Entity entity = find(soln.getResource("uri").getURI());
            entities.add(entity);
            break;
        }

        java.util.Collections.sort((List<Entity>) entities);

        return entities;
    }

    public static Map<String,String> getMap() {
        List<Entity> list = find();
        Map<String,String> map = new HashMap<String,String>();
        for (Entity ent: list)
            map.put(ent.getUri(),ent.getLabel());
        return map;
    }
    */

    public static List<String> getSubclasses(String uri) {
        List<String> subclasses = new ArrayList<String>();

        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList()
                + " SELECT ?uri WHERE { \n"
                + " ?uri rdfs:subClassOf* <" + uri + "> . \n"
                + " } \n";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            subclasses.add(soln.get("uri").toString());
        }

        return subclasses;
    }

    public static Entity find(String uri) {
        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        Entity entity = new Entity();
        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        while (stmtIterator.hasNext()) {
            Statement statement = stmtIterator.next();
            RDFNode object = statement.getObject();
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                String label = object.asLiteral().getString();

                // prefer longer one
                if (label.length() > entity.getLabel().length()) {
                    entity.setLabel(label);
                }
            } else if (statement.getPredicate().getURI().equals(RDFS.SUBCLASS_OF)) {
                entity.setSuperUri(object.asResource().getURI());
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                entity.setTypeUri(object.asResource().getURI());
			} else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
				entity.setHascoTypeUri(object.asResource().getURI());
		    } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
		    	entity.setComment(object.asLiteral().getString());
		    }

        }

        entity.setUri(uri);
        entity.setLocalName(uri.substring(uri.indexOf('#') + 1));

        return entity;
    }

    @Override
    public int compareTo(Entity another) {
        if (this.getLabel() != null && another.getLabel() != null) {
            return this.getLabel().compareTo(another.getLabel());
        }
        return this.getLocalName().compareTo(another.getLocalName());
    }
}

