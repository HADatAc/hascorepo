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

@JsonFilter("dp2Filter")
public class DP2 extends MetadataTemplate {

    public String className = "hasco:DP2";

    public static DP2 find(String uri) {
            
        if (uri == null || uri.isEmpty()) {
            System.out.println("[ERROR] No valid URI provided to retrieve DP2 object: " + uri);
            return null;
        }

        //System.out.println("DP2.java : in find(): uri = [" + uri + "]");
        DP2 dp2 = null;
        Statement statement;
        RDFNode object;
        
        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);
        
        StmtIterator stmtIterator = model.listStatements();

        if (!stmtIterator.hasNext()) {
            return null;
        } else {
            dp2 = new DP2();
        }
        
        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (uri != null && !uri.isEmpty()) {
                if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                    dp2.setLabel(str);
                } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                    dp2.setTypeUri(str); 
                } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                    dp2.setHascoTypeUri(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_STATUS)) {
                    dp2.setHasStatus(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_VERSION)) {
                    dp2.setHasVersion(str);
                } else if (statement.getPredicate().getURI().equals(HASCO.HAS_DATAFILE)) {
                    dp2.setHasDataFileUri(str);
                } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                    dp2.setComment(str);
                } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                    dp2.setHasSIRManagerEmail(str);
                }
            }
        }

        dp2.setUri(uri);
        
        return dp2;
    }

}
