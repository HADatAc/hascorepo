package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.Constants;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.utils.NameSpaces;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repository extends HADatAcThing {

    private static final Logger log = LoggerFactory.getLogger(Repository.class);

    public static String className = HASCO.REPOSITORY;

    @PropertyField(uri="hasco:hasTitle")
    private String title;

    @PropertyField(uri="hasco:hasBaseOntology")
    private String hasBaseOntology;

    @PropertyField(uri="hasco:hasBaseURL")
    private String hasBaseURL;

    @PropertyField(uri="hasco:hasInstitution", valueType=PropertyValueType.URI)
    private String institutionUri;

    private DateTime startedAt;

    private Organization institution;

    @PropertyField(uri="hasco:hasDefaultNamespaceAbbreviation")
    private String hasDefaultNamespaceAbbreviation;

    @PropertyField(uri="hasco:hasDefaultNamespaceURL", valueType=PropertyValueType.URI)
    private String hasDefaultNamespaceURL;

    @PropertyField(uri="hasco:hasNamespaceAbbreviation")
    private String hasNamespaceAbbreviation;

    @PropertyField(uri="hasco:hasNamespaceURL", valueType=PropertyValueType.URI)
    private String hasNamespaceURL;

    @PropertyField(uri="vstoi:hasVersion")
    private String hasVersion;

    public Repository() {
        this.uri = Constants.DEFAULT_REPOSITORY;
        this.typeUri = HASCO.REPOSITORY;
        this.hascoTypeUri = HASCO.REPOSITORY;
        this.label = "";
        this.title = "";
        this.comment = "";
        this.hasBaseOntology = "";
        this.hasBaseURL = "";
        //this.hasBaseURL =  "http://hadatac.org/kb/" + ConfigProp.getBasePrefix() + "#";
        this.institutionUri = institutionUri;
        this.startedAt = null;
        this.hasDefaultNamespaceAbbreviation = "";
        this.hasDefaultNamespaceURL = "";
        this.hasNamespaceAbbreviation = "";
        this.hasNamespaceURL = "";
        this.hasVersion = Constants.REPOSITORY_VERSION;
    }

    public String getTitle() {
        return title;
    }

    public String getBaseOntology() {
        return hasBaseOntology;
    }

    public String getBaseURL() {
        return hasBaseURL;
    }

    @JsonIgnore
    public String getInstitutionUri() {
        return institutionUri;
    }

    public Organization getInstitution() {
        if (institutionUri == null || institutionUri.equals("")) {
            return null;
        }
        if (institution != null && institution.getUri().equals(institutionUri)) {
            return institution;
        }
        return Organization.find(institutionUri);
    }

    public String getHasDefaultNamespaceAbbreviation() {
        if (hasDefaultNamespaceAbbreviation != null && hasDefaultNamespaceAbbreviation.equals("")) {
            return null;
        }
        return hasDefaultNamespaceAbbreviation;
    }

    public String getHasDefaultNamespaceURL() {
        if (hasDefaultNamespaceURL != null && hasDefaultNamespaceURL.equals("")) {
            return null;
        }
        return hasDefaultNamespaceURL;
    }

    public String getHasNamespaceAbbreviation() {
        if (hasNamespaceAbbreviation != null && hasNamespaceAbbreviation.equals("")) {
            return null;
        }
        return hasNamespaceAbbreviation;
    }

    public String getHasNamespaceURL() {
        if (hasNamespaceURL != null && hasNamespaceURL.equals("")) {
            return null;
        }
        return hasNamespaceURL;
    }

    public String getHasVersion() {
        return hasVersion;
    }

    // get Start Time Methods
    public String getStartedAt() {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        return formatter.withZone(DateTimeZone.UTC).print(startedAt);
    }
    public String getStartedAtXsd() {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        return formatter.withZone(DateTimeZone.UTC).print(startedAt);
    }

    // set Methods

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBaseOntology(String hasBaseOntology) {
        this.hasBaseOntology = hasBaseOntology;
    }

    public void setBaseURL(String hasBaseURL) {
        this.hasBaseURL = hasBaseURL;
    }

    public void setInstitutionUri(String institutionUri) {
        if (institutionUri != null && !institutionUri.equals("")) {
            if (institutionUri.indexOf("http") > -1) {
                this.institutionUri = institutionUri;
            }
        }
    }

    public void setHasDefaultNamespaceAbbreviation(String hasDefaultNamespaceAbbreviation) {
        this.hasDefaultNamespaceAbbreviation = hasDefaultNamespaceAbbreviation;
    }

    public void setHasDefaultNamespaceURL(String hasDefaultNamespaceURL) {
        this.hasDefaultNamespaceURL = hasDefaultNamespaceURL;
    }

    public void setHasNamespaceAbbreviation(String hasNamespaceAbbreviation) {
        this.hasNamespaceAbbreviation = hasNamespaceAbbreviation;
    }

    public void setHasNamespaceURL(String hasNamespaceURL) {
        this.hasNamespaceURL = hasNamespaceURL;
    }

    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    // set Start Time Methods
    public void setStartedAt(String startedAt) {
        if (startedAt == null || startedAt.equals("")) {
            this.startedAt = null;
        } else {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss zzz yyyy");
            this.startedAt = formatter.parseDateTime(startedAt);
        }
    }

    public void setStartedAtXsd(String startedAt) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();
        this.startedAt = formatter.parseDateTime(startedAt);
    }

    public void setStartedAtXsdWithMillis(String startedAt) {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTime();
        this.startedAt = formatter.parseDateTime(startedAt);
    }

    public static boolean newNamespace(String json) {
        ObjectMapper objectMapper = new ObjectMapper(); 
        NameSpace namespace;
        try {
            namespace = (NameSpace)objectMapper.readValue(json, NameSpace.class);
            namespace.saveWithoutURIValidation();
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Following error parsing JSON for " + NameSpace.class + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean resetNamespaces() {
        try {
            NameSpaces.resetNameSpaces();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Following error while resetting NameSpaces: " + e.getMessage());
            return false;
        }
    }

    public static Repository getRepository() {

        String uri = Constants.DEFAULT_REPOSITORY;
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] A value need to be set for Constants.DEFEAULT_REPOSITORY.");
            return null;
        }

        String queryString = "DESCRIBE <" + uri + ">";
	    Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
		
		StmtIterator stmtIterator = model.listStatements();
	    Statement statement;
	    RDFNode object;

		if (!stmtIterator.hasNext()) {
			return null;
		} 

		Repository repo = new Repository();
		while (stmtIterator.hasNext()) {
		    statement = stmtIterator.next();
		    object = statement.getObject();
			String str = URIUtils.objectRDFToString(object);
            //System.out.println(statement.getPredicate().getURI() + "  [" + str + "]");
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                repo.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                repo.setTypeUri(str); 
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                repo.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                repo.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_BASE_ONTOLOGY)) {
                repo.setBaseOntology(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_BASE_URL)) {
                repo.setBaseURL(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_INSTITUTION)) {
                repo.setInstitutionUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DEFAULT_NAMESPACE_ABBREVIATION)) {
                repo.setHasDefaultNamespaceAbbreviation(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DEFAULT_NAMESPACE_URL)) {
                repo.setHasDefaultNamespaceURL(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_NAMESPACE_ABBREVIATION)) {
                repo.setHasNamespaceAbbreviation(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_NAMESPACE_URL)) {
                repo.setHasNamespaceURL(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_VERSION)) {
                repo.setHasVersion(str);
            }
		}

		repo.setUri(uri);
		
		return repo;

    }

    /* 
    public static Repository getRepository() {
        Repository repository = new Repository();

        String uri = "<" + Constants.DEFAULT_REPOSITORY + ">";
        String repositoryQueryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                "SELECT DISTINCT ?label ?title ?comment ?baseOntology ?baseURL ?institutionUri ?defaultNsAbbreviation ?defaultNsUrl ?nsAbbreviation ?nsUrl ?version " +
                " WHERE {  \n" +
                "      ?type rdfs:subClassOf* hasco:Repository . \n" +
                "      " + uri + " a ?type . \n" +
                "      " + uri + " hasco:hascoType ?hascoType . \n" +
                "      OPTIONAL { " + uri + " rdfs:label ?label } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasTitle ?title } . \n" +
                "      OPTIONAL { " + uri + " rdfs:comment ?comment } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasBaseOntology ?baseOntology } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasBaseURL ?baseURL } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasInstitution ?institutionUri } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasDefaultNamespaceAbbreviation ?defaultNsAbbreviation } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasDefaultNamespaceURL ?defaultNsUrl } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasNamespaceAbbreviation ?nsAbbreviation } . \n" +
                "      OPTIONAL { " + uri + " hasco:hasNamespaceURL ?nsUrl } . \n" +
                "      OPTIONAL { " + uri + " vstoi:hasVersion ?version } . \n" +
                " } \n";

        try {

            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), repositoryQueryString);

            if (!resultsrw.hasNext()) {
                System.out.println("[WARNING] REPOSITORY_URI " + uri + " does not retrieve a repository object");
                return null;
            } else {
                repository.setUri(Constants.DEFAULT_REPOSITORY);
                repository.setTypeUri(HASCO.REPOSITORY);
                repository.setHascoTypeUri(HASCO.REPOSITORY);

                while (resultsrw.hasNext()) {
                    QuerySolution soln = resultsrw.next();
                    if (soln.contains("label")) {
                        repository.setLabel(soln.get("label").toString());
                    }
                    if (soln.contains("title")) {
                        repository.setTitle(soln.get("title").toString());
                    }
                    if (soln.contains("comment")) {
                        repository.setComment(soln.get("comment").toString());
                    }
                    if (soln.contains("baseOntology")) {
                        repository.setBaseOntology(soln.get("baseOntology").toString());
                    }
                    if (soln.contains("baseURL")) {
                        repository.setBaseUrl(soln.get("baseURL").toString());
                    }
                    if (soln.contains("institutionUri")) {
                        repository.setInstitutionUri(soln.get("institutionUri").toString());
                    }
                    if (soln.contains("defaultNsAbbreviation")) {
                        repository.setHasDefaultNamespaceAbbreviation(soln.get("defaultNsAbbreviation").toString());
                    }
                    if (soln.contains("defaultNsUrl")) {
                        repository.setHasDefaultNamespaceURL(soln.get("defaultNsUrl").toString());
                    }
                    if (soln.contains("nsAbbreviation")) {
                        repository.setHasNamespaceAbbreviation(soln.get("nsAbbreviation").toString());
                    }
                    if (soln.contains("nsUrl")) {
                        repository.setHasNamespaceURL(soln.get("nsUrl").toString());
                    }
                    if (soln.contains("version")) {
                        repository.setHasVersion(soln.get("version").toString());
                    }
                }
            }
        } catch (QueryExceptionHTTP e) {
            e.printStackTrace();
        }
        return repository;
    }
    */

    @Override
    public void save() {
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

}

