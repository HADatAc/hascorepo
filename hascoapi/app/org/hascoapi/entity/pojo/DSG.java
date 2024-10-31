package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFilter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

@JsonFilter("dsgFilter")
public class DSG extends MetadataTemplate {

    public String className = "hasco:DSG";

    public static DSG find(String uri) {
            
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve DSG object: " + uri);
            return null;
        }

        //System.out.println("DSG.java : in find(): uri = [" + uri + "]");
        DSG dsg = null;
        Statement statement;
        RDFNode object;
        
        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
        
        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        } else {
            dsg = new DSG();
        }
        
        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (uri != null && !uri.isEmpty()) {
                if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                    dsg.setLabel(str);
                } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                    dsg.setTypeUri(str); 
                } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                    dsg.setHascoTypeUri(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                    dsg.setHasStatus(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                    dsg.setHasVersion(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                    dsg.setHasDataFileUri(str);
                } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                    dsg.setComment(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                    dsg.setHasSIRManagerEmail(str);
                }
            }
        }

        dsg.setUri(uri);
        
        return dsg;
    }

}
