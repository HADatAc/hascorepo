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

@JsonFilter("strFilter")
public class STR extends MetadataTemplate {

    public String className = "hasco:STR";

    public static STR find(String uri) {
            
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve STR object: " + uri);
            return null;
        }

        //System.out.println("STR.java : in find(): uri = [" + uri + "]");
        STR str = null;
        Statement statement;
        RDFNode object;
        
        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
        
        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        } else {
            str = new STR();
        }
        
        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String string = URIUtils.objectRDFToString(object);
            if (uri != null && !uri.isEmpty()) {
                if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                    str.setLabel(string);
                } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                    str.setTypeUri(string); 
                } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                    str.setHascoTypeUri(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                    str.setHasStatus(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                    str.setHasVersion(string);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                    str.setHasDataFileUri(string);
                } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                    str.setComment(string);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                    str.setHasSIRManagerEmail(string);
                }
            }
        }

        str.setUri(uri);
        
        return str;
    }

}
