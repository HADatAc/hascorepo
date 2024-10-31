package org.hascoapi.entity.pojo;

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
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JsonFilter("responseOptionFilter")
public class ResponseOption extends HADatAcThing implements SIRElement /*, Comparable<ResponseOption>*/ {

    @PropertyField(uri = "vstoi:hasStatus")    
    private String hasStatus;

    @PropertyField(uri = "vstoi:hasSerialNumber")
    String serialNumber;

    @PropertyField(uri = "hasco:hasImage")
    String image;

    @PropertyField(uri = "vstoi:hasContent")
    String hasContent;

    @PropertyField(uri = "vstoi:hasLanguage")
    private String hasLanguage;

    @PropertyField(uri = "vstoi:hasVersion")
    String hasVersion;

    @PropertyField(uri = "vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public String getHasStatus() {
        return hasStatus;
    }

    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public String getSerialNumber() {
        return serialNumber;
    }       

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHasContent() {
        return hasContent;
    }

    public void setHasContent(String hasContent) {
        this.hasContent = hasContent;
    }

    public String getHasLanguage() {
        return hasLanguage;
    }

    public void setHasLanguage(String hasLanguage) {
        this.hasLanguage = hasLanguage;
    }

    public String getHasVersion() {      
        return hasVersion;
    }

    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }

    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public static ResponseOption find(String uri) {
        ResponseOption responseOption = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        responseOption = new ResponseOption();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                responseOption.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                responseOption.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                responseOption.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                responseOption.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                responseOption.setHasStatus(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SERIAL_NUMBER)) {
                responseOption.setSerialNumber(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_IMAGE)) {
                responseOption.setImage(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_CONTENT)) {
                responseOption.setHasContent(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_LANGUAGE)) {
                responseOption.setHasLanguage(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                responseOption.setHasVersion(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                responseOption.setHasSIRManagerEmail(str);
            }
        }

        responseOption.setUri(uri);

        return responseOption;
    }

    public static boolean attach(CodebookSlot codebookSlot, ResponseOption responseOption) {
        if (codebookSlot == null) {
            return false;
        }
        return codebookSlot.updateCodebookSlotResponseOption(responseOption);
    }

    public static boolean detach(CodebookSlot codebookSlot) {
        if (codebookSlot == null) {
            return false;
        }
        return codebookSlot.updateCodebookSlotResponseOption(null);
    }

    /*
    @Override
    public int compareTo(ResponseOption another) {
        return this.getLabel().compareTo(another.getLabel());
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
