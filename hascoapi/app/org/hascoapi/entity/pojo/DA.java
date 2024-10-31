package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFilter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("daFilter")
public class DA extends MetadataTemplate {

    public String className = "hasco:DataAcquisition";

    @PropertyField(uri = "hasco:hasDD")
    private String hasDDUri;

    @PropertyField(uri = "hasco:hasSDD")
    private String hasSDDUri;
    
    @PropertyField(uri = "hasco:isMemberOf")
    private String isMemberOfUri;
    
    public String getHasDDUri() {
        return hasDDUri;
    }
    public void setHasDDUri(String hasDDUri) {
        this.hasDDUri = hasDDUri;
    }
    public DD getHasDD() {
        if (this.hasDDUri == null) {
            return null;
        }
        return DD.find(hasDDUri);
    }	

    public String getHasSDDUri() {
        return hasSDDUri;
    }
    public void setHasSDDUri(String hasSDDUri) {
        this.hasSDDUri = hasSDDUri;
    }
    public SDD getHasSDD() {
        if (this.hasSDDUri == null) {
            return null;
        }
        return SDD.find(hasSDDUri);
    }	

    public String getIsMemberOfUri() {
        return isMemberOfUri;
    }
    public void setIsMemberOfUri(String isMemberOfUri) {
        this.isMemberOfUri = isMemberOfUri;
    }
    public Study getIsMemberOf() {
        if (this.isMemberOfUri == null) {
            return null;
        }
        return Study.find(isMemberOfUri);
    }	

    public static DA find(String uri) {
            
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve DA object: " + uri);
            return null;
        }

        DA da = null;
        Statement statement;
        RDFNode object;
        
        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
        
        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        } else {
            da = new DA();
        }
        
        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (uri != null && !uri.isEmpty()) {
                if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                    da.setLabel(str);
                } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                    da.setTypeUri(str); 
                } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                    da.setHascoTypeUri(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                    da.setHasStatus(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                    da.setHasVersion(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                    da.setHasDataFileUri(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DD)) {
                    da.setHasDDUri(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_SDD)) {
                    da.setHasSDDUri(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.IS_MEMBER_OF)) {
                    da.setIsMemberOfUri(str);
                } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                    da.setComment(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                    da.setHasSIRManagerEmail(str);
                }
            }
        }

        da.setUri(uri);
        
        return da;
    }

}
