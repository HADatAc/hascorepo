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
import org.hascoapi.annotations.PropertyValueType;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.vocabularies.*;

@JsonFilter("studyRoleFilter")
public class StudyRole extends HADatAcThing implements Comparable<StudyRole>  {

	static String className = "sio:SIO_000776";

    @PropertyField(uri="vstoi:hasStatus")
    private String hasStatus;

    @PropertyField(uri="hasco:isMemberOf", valueType=PropertyValueType.URI)
    private String isMemberOfUri;
    
    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public String getHasStatus() {
        return hasStatus;
    }

    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public String getIsMemberOfUri() {
        return isMemberOfUri;
    }

    public void setIsMemberOfUri(String isMemberOfUri) {
        this.isMemberOfUri = isMemberOfUri;
    }

    public Study getIsMemberOf() {
        if (isMemberOfUri == null || isMemberOfUri.isEmpty()) {
            return null;
        }
        return Study.find(isMemberOfUri);
    }
    
    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }

    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public static StudyRole find(String uri) {
        StudyRole studyRole = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        }

        studyRole = new StudyRole();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                studyRole.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                studyRole.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                studyRole.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                studyRole.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                studyRole.setHasStatus(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.IS_MEMBER_OF)) {
                studyRole.setIsMemberOfUri(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                studyRole.setHasSIRManagerEmail(str);
            } 
        }

        studyRole.setUri(uri);

        return studyRole;
    }

    @Override
    public int compareTo(StudyRole another) {
        return this.getLabel().compareTo(another.getLabel());
    }

    @Override public void save() {
        saveToTripleStore();
    }

    @Override public void delete() {
        deleteFromTripleStore();
    }

}
