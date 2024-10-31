package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
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
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("organizationFilter")
public class Organization extends Agent {

    @PropertyField(uri="schema:parentOrganization")
    protected String parentOrganizationUri;

    public String getParentOrganizationUri() {
        return parentOrganizationUri;
    }
    public void setParentOrganizationUri(String parentOrganizationUri) {
        this.parentOrganizationUri = parentOrganizationUri;
    }

    public static List<Organization> find() {
        String query =
            " SELECT ?uri WHERE { " +
            " ?uri a foaf:Organization ." +
            "} ";
        return findManyByQuery(query);
    }

    public static int findTotalSubOrganizations(String uri) {
        if (uri == null || uri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE { " +   
                "    ?uri schema:parentOrganization <" + uri + "> .  " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        

    public static List<Organization> findSubOrganizations(String uri, int pageSize, int offset) {
        if (uri == null || uri.isEmpty()) {
            return new ArrayList<Organization>();
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?uri schema:parentOrganization <" + uri + ">.  " +
				"          ?uri rdfs:label ?label . " +
                " } " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findManyByQuery(query);
    }        

    public static int findTotalAffiliations(String uri) {
        if (uri == null || uri.isEmpty()) {
            return 0;
        }
        String query = NameSpaces.getInstance().printSparqlNameSpaceList() + 
                " SELECT (count(?uri) as ?tot)  " +
                " WHERE { " +   
                "    ?uri foaf:member <" + uri + "> .  " +
                " }";
        return GenericFind.findTotalByQuery(query);
    }        

    public static List<Person> findAffiliations(String uri, int pageSize, int offset) {
        if (uri == null || uri.isEmpty()) {
            return new ArrayList<Person>();
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?uri foaf:member <" + uri + ">.  " +
				"          ?uri rdfs:label ?label . " +
                " } " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return Person.findManyByQuery(query);
    }        

    private static List<Organization> findManyByQuery(String requestedQuery) {
        List<Organization> organizations = new ArrayList<Organization>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + requestedQuery;

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            String uri = soln.getResource("uri").getURI();
            Organization organization = Organization.find(uri);
            organizations.add(organization);
        }

        java.util.Collections.sort((List<Organization>) organizations);
        return organizations;
    }

    public static Organization findByOriginalID(String originalID) {
        if (originalID == null || originalID.isEmpty()) {
            return null;
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* foaf:Organization . " +
                "          ?uri a ?subUri . " +
                "          ?uri hasco:hasOriginalId ?id .  " +
                "        FILTER (?id=\"" + originalID + "\"^^xsd:string)  . " +
                " }";
        return findOneByQuery(query);
    }        

    public static Organization findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* foaf:Organization . " +
                "          ?uri a ?subUri . " +
                "          ?uri foaf:mbox ?email .  " +
                "        FILTER (?email=\"" + email + "\"^^xsd:string)  . " +
                " }";
        return findOneByQuery(query);
    }        

    public static Organization findByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        String query = 
                "SELECT ?uri " +
                " WHERE {  ?subUri rdfs:subClassOf* foaf:Organization . " +
                "          ?uri a ?subUri . " +
                "          ?uri foaf:name ?name .  " +
                "        FILTER (?name=\"" + name + "\"^^xsd:string)  . " +
                " }";
        return findOneByQuery(query);
    }        

    private static Organization findOneByQuery(String requestedQuery) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + requestedQuery;
        
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        String uri = null;
        Organization organization = null;
        if (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.get("uri") != null) {
                uri = soln.get("uri").toString();
                organization = Organization.find(uri);
            }
        }

        return organization;
    }

    public static Organization find(String uri) {
        Organization organization = null;
        Statement statement;
        RDFNode object;
        String queryString;

        if (uri.startsWith("<")) {
            queryString = "DESCRIBE " + uri + " ";
        } else {
            queryString = "DESCRIBE <" + uri + ">";
        }

        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        organization = new Organization();
        StmtIterator stmtIterator = model.listStatements();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                organization.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                organization.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                organization.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                organization.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(FOAF.NAME)) {
                organization.setName(str);
            } else if (statement.getPredicate().getURI().equals(FOAF.MBOX)) {
                organization.setMbox(str);
            } else if (statement.getPredicate().getURI().equals(SCHEMA.TELEPHONE)) {
                organization.setTelephone(str);
            } else if (statement.getPredicate().getURI().equals(SCHEMA.PARENT_ORGANIZATION)) {
                organization.setParentOrganizationUri(str);
            } else if (statement.getPredicate().getURI().equals(SCHEMA.ADDRESS)) {
                organization.setHasAddressUri(str);
            } else if (statement.getPredicate().getURI().equals(SCHEMA.URL)) {
                organization.setHasUrl(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                organization.setHasSIRManagerEmail(str);
            }
        }

        organization.setUri(uri);

        return organization;
    }

    @Override
    public void save() {
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

}
