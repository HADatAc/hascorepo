package org.hascoapi.entity.pojo;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.VSTOI;

import java.util.*;

public class Table implements Comparable<Table> {

    static String className = "vstoi:Table";

    private String code;

    private String value;

    private String url;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public static List<Table> findLanguage() {
        List<Table> tables = new ArrayList<Table>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri ?label ?definition ?sameas WHERE { " +
                //" ?uri a skos:Concept . " +
                " ?uri a <https://www.omg.org/spec/LCC/Languages/LanguageRepresentation/IndividualLanguage> . " +
                //" ?uri skos:prefLabel ?label . " +
                " ?uri rdfs:label ?label . " +
                " ?uri skos:definition ?definition . " +
                " ?uri owl:sameAs ?sameas . " +
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            Table newTable = new Table();
            newTable.setURL(soln.getResource("uri").getURI());
            String sameas = soln.getResource("sameas").getURI();
            if (sameas != null) {
                newTable.setCode(getLastToken(sameas,"/"));
            } else {
                newTable.setCode("");
            }
            
            newTable.setValue(soln.getLiteral("label").getString());
            tables.add(newTable);
        }

        java.util.Collections.sort((List<Table>) tables);
        return tables;

    }

    private static String getLastToken(String strValue, String splitter )  {        
        String[] strArray = strValue.split(splitter);  
        return strArray[strArray.length -1];            
    }

    public static List<Table> findGenerationActivity() {
        List<Table> tables = new ArrayList<Table>();
        Iterator<Map.Entry<String, String>> iterator = VSTOI.wasGeneratedBy.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Table newTable = new Table();
            newTable.setURL(entry.getKey());
            newTable.setValue(entry.getValue());
            tables.add(newTable);
        }

        java.util.Collections.sort((List<Table>) tables);
        return tables;

    }

    public static List<Table> findInformant() {
        List<Table> tables = new ArrayList<Table>();
        Iterator<Map.Entry<String, String>> iterator = VSTOI.informant.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Table newTable = new Table();
            newTable.setURL(entry.getKey());
            newTable.setValue(entry.getValue());
            tables.add(newTable);
        }

        java.util.Collections.sort((List<Table>) tables);
        return tables;

    }

    public static List<Table> findSubcontainerPosition() {
        List<Table> tables = new ArrayList<Table>();
        Iterator<Map.Entry<String, String>> iterator = VSTOI.containerPosition.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Table newTable = new Table();
            newTable.setURL(entry.getKey());
            newTable.setValue(entry.getValue());
            tables.add(newTable);
        }

        java.util.Collections.sort((List<Table>) tables);
        return tables;

    }

    public static List<Table> findInstrumentPosition() {
        List<Table> tables = new ArrayList<Table>();
        Iterator<Map.Entry<String, String>> iterator = VSTOI.pagePosition.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            Table newTable = new Table();
            newTable.setURL(entry.getKey());
            newTable.setValue(entry.getValue());
            tables.add(newTable);
        }

        java.util.Collections.sort((List<Table>) tables);
        return tables;

    }

    @Override
    public int compareTo(Table another) {
        if (this.getValue() != null && another.getValue() != null) {
            return this.getValue().compareTo(another.getValue());
        }
        return this.getURL().compareTo(another.getURL());
    }

}
