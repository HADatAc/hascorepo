package org.hascoapi.entity.pojo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.ingestion.Record;
import org.hascoapi.ingestion.RecordFile;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.Templates;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("kgrFilter")
public class KGR extends MetadataTemplate {

    public String className = "hasco:KnowledgeGraph";

    //private Map<String, String> mapCatalog = new HashMap<String, String>();
    private Map<String, Map<String, String>> postalAddresses = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, String>> places = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, String>> organizations = new HashMap<String, Map<String, String>>();
    private Map<String, Map<String, String>> persons = new HashMap<String, Map<String, String>>();
    //private Templates templates = null;

    /* 
    @PropertyField(uri = "vstoi:hasStatus")
    private String hasStatus;

    @PropertyField(uri = "hasco:hasDataFile")
    private String hasDataFileUri;

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public String getHasStatus() {
        return hasStatus;
    }
    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public String getHasDataFile() {
        return hasDataFileUri;
    }
    public void setHasDataFile(String hasDataFileUri) {
        this.hasDataFileUri = hasDataFileUri;
        this.setNamedGraph(hasDataFileUri);
    }
    public DataFile getDataFile() {
        if (this.hasDataFileUri == null) {
            return null;
        }
        return DataFile.find(this.hasDataFileUri);
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public Map<String, String> getCatalog() {
        return mapCatalog;
    }

    public void setTemplates(String templateFile) {
        this.templates = new Templates(templateFile);
    }
    */

    public static KGR find(String uri) {
            
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve Study object: " + uri);
            return null;
        }

        //System.out.println("Study.java : in find(): uri = [" + uri + "]");
        KGR kgr = null;
        Statement statement;
        RDFNode object;
        
        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
        
        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        } else {
            kgr = new KGR();
        }
        
        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (uri != null && !uri.isEmpty()) {
                if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                    kgr.setLabel(str);
                } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                    kgr.setTypeUri(str); 
                } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                    kgr.setHascoTypeUri(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                    kgr.setHasStatus(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                    kgr.setHasVersion(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                    kgr.setHasDataFileUri(str);
                } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                    kgr.setComment(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                    kgr.setHasSIRManagerEmail(str);
                }
            }
        }

        kgr.setUri(uri);
        
        return kgr;
    }

    public boolean readPostalAddresses(RecordFile file) {
        if (!file.isValid()) {
            System.out.println("[ERROR] Record file is considered invalid");
            return false;
        }

        try {
            for (Record record : file.getRecords()) {
                if (!record.getValueByColumnName(this.getTemplates().getPostalAddressStreet()).isEmpty() &&
                    !record.getValueByColumnName(this.getTemplates().getPostalAddressPostalCode()).isEmpty() ) {
                    //System.out.println(record.getValueByColumnName(this.getTemplates().getPostalAddressStreet()));    
                    //System.out.println(record.getValueByColumnName(this.getTemplates().getPostalAddressPostalCode()));    
                    String postalAddressStreet = record.getValueByColumnName(this.getTemplates().getPostalAddressStreet());
                    String postalAddressPostalCode = record.getValueByColumnName(this.getTemplates().getPostalAddressPostalCode());
                    String postalAddressKey = postalAddressStreet + "|" + postalAddressPostalCode;
                    PostalAddress postalAddressTest = PostalAddress.findByAddress(postalAddressStreet, postalAddressPostalCode);
                    if (postalAddressTest != null) {
                        System.out.println("[WARNING] PostalAddress with address " + 
                            postalAddressStreet + " " + postalAddressPostalCode + 
                            " already exist. It has been ignored.");
                    } else {
                        Map<String, String> mapPostalAddressProperties = null;
                        if (!postalAddresses.containsKey(postalAddressKey)) {
                            mapPostalAddressProperties = new HashMap<String, String>();
                            postalAddresses.put(postalAddressKey, mapPostalAddressProperties);
                        } else {
                            mapPostalAddressProperties = postalAddresses.get(postalAddressKey);
                        }
                        mapPostalAddressProperties.put(this.getTemplates().getManagerEmail(), getHasSIRManagerEmail());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (postalAddresses.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean readPlaces(RecordFile file) {
        if (!file.isValid()) {
            System.out.println("[ERROR] Record file is considered invalid");
            return false;
        }

        for (Record record : file.getRecords()) {
            if (!record.getValueByColumnName(this.getTemplates().getPlaceName()).isEmpty()) {
                String placeName = record.getValueByColumnName(this.getTemplates().getPlaceName());
                Place placeTest = null;
                if (placeName != null && !placeName.isEmpty()) {
                    placeTest = Place.findByName(placeName);
                }
                if (placeTest != null) {
                    System.out.println("[WARNING] Place with name " + placeName + " already exist. It has been ignored.");
                } else {
                    Map<String, String> mapPlaceProperties = null;
                    if (!places.containsKey(placeName)) {
                        mapPlaceProperties = new HashMap<String, String>();
                        places.put(placeName, mapPlaceProperties);
                    } else {
                        mapPlaceProperties = places.get(placeName);
                    }
                    mapPlaceProperties.put(this.getTemplates().getManagerEmail(), getHasSIRManagerEmail());
                }
            }
        }

        if (places.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean readOrganizations(RecordFile file) {
        if (!file.isValid()) {
            System.out.println("[ERROR] Record file is considered invalid");
            return false;
        }

        for (Record record : file.getRecords()) {
            if (!record.getValueByColumnName(this.getTemplates().getAgentOriginalID()).isEmpty()) {
                String orgID = record.getValueByColumnName(this.getTemplates().getAgentOriginalID());
                String email = "";
                if (!record.getValueByColumnName(this.getTemplates().getAgentEmail()).isEmpty()) {
                    email = record.getValueByColumnName(this.getTemplates().getAgentEmail());
                }
                Organization organizationTest = null;
                if (email != null && !email.isEmpty()) {
                    organizationTest = Organization.findByEmail(email);
                }
                if (organizationTest != null) {
                    System.out.println("[WARNING] Organization with email " + organizationTest.getMbox() + " already exist. It has been ignored.");
                } else {
                    Map<String, String> mapOrgProperties = null;
                    if (!organizations.containsKey(orgID)) {
                        mapOrgProperties = new HashMap<String, String>();
                        organizations.put(orgID, mapOrgProperties);
                    } else {
                        mapOrgProperties = organizations.get(orgID);
                    }
                    mapOrgProperties.put(this.getTemplates().getAgentEmail(), email);
                    String name = "";
                    if (!record.getValueByColumnName(this.getTemplates().getAgentName()).isEmpty()) {
                        name = record.getValueByColumnName(this.getTemplates().getAgentName());
                    }
                    mapOrgProperties.put(this.getTemplates().getAgentName(), name);
                    mapOrgProperties.put(this.getTemplates().getManagerEmail(), getHasSIRManagerEmail());
                }
            }
        }

        if (organizations.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean readPersons(RecordFile file) {
        if (!file.isValid()) {
            System.out.println("[ERROR] Record file is considered invalid");
            return false;
        }

        for (Record record : file.getRecords()) {
            if (!record.getValueByColumnName(this.getTemplates().getAgentOriginalID()).isEmpty()) {
                String personID = record.getValueByColumnName(this.getTemplates().getAgentOriginalID());
                String email = "";
                if (!record.getValueByColumnName(this.getTemplates().getAgentEmail()).isEmpty()) {
                    email = record.getValueByColumnName(this.getTemplates().getAgentEmail());
                }
                Person personTest = null;
                if (email != null && !email.isEmpty()) {
                    personTest = Person.findByEmail(email);
                }

                if (personTest != null) {
                    System.out.println("[WARNING] Person with email " + personTest.getMbox() + " already exist. It has been ignored.");
                } else {
                    Map<String, String> mapPersonProperties = null;
                    if (!persons.containsKey(personID)) {
                        mapPersonProperties = new HashMap<String, String>();
                        persons.put(personID, mapPersonProperties);
                    } else {
                        mapPersonProperties = persons.get(personID);
                    }
                    mapPersonProperties.put(this.getTemplates().getAgentEmail(), email);
                    String givenName = "";
                    if (!record.getValueByColumnName(this.getTemplates().getAgentGivenName()).isEmpty()) {
                        givenName = record.getValueByColumnName(this.getTemplates().getAgentGivenName());
                    }
                    mapPersonProperties.put(this.getTemplates().getAgentGivenName(), givenName);
                    String familyName = "";
                    if (!record.getValueByColumnName(this.getTemplates().getAgentFamilyName()).isEmpty()) {
                        familyName = record.getValueByColumnName(this.getTemplates().getAgentFamilyName());
                    }
                    mapPersonProperties.put(this.getTemplates().getAgentFamilyName(), familyName);
                    String hasAffiliationUri = "";
                    if (!record.getValueByColumnName(this.getTemplates().getAgentHasAffiliationUri()).isEmpty()) {
                        hasAffiliationUri = record.getValueByColumnName(this.getTemplates().getAgentHasAffiliationUri());
                    }
                    if (hasAffiliationUri != null && !hasAffiliationUri.isEmpty()) {
                        Organization affiliation = Organization.findByName(hasAffiliationUri);
                        if (affiliation != null && affiliation.getUri() != null && !affiliation.getUri().isEmpty()) {
                            mapPersonProperties.put(this.getTemplates().getAgentHasAffiliationUri(), affiliation.getUri());
                        } else {
                            System.out.println("[WARNING] KGR.java: Not found isMemberOf [" + hasAffiliationUri + "] for PersonID [" + personID + "]");
                        }
                    }
                    mapPersonProperties.put(this.getTemplates().getManagerEmail(), getHasSIRManagerEmail());
                }
            }
        }

        if (persons.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
