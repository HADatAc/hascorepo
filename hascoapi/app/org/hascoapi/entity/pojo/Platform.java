package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.apache.commons.text.WordUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.FirstLabel;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDF;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.VSTOI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonFilter("platformFilter")
public class Platform extends HADatAcClass implements Comparable<Platform> {

    private static final Logger log = LoggerFactory.getLogger(Platform.class);
    public static String LAT = SIO.LATITUDE;
	public static String LONG = SIO.LONGITUDE;
	
    private String location;
    
    @PropertyField(uri="hasco:hasFirstCoordinate")
    private Float  firstCoordinate;
    
    @PropertyField(uri="hasco:hasFirstCoordinateUnit")
    private String firstCoordinateUnit;

    @PropertyField(uri="hasco:hasFirstCoordinateCharacteristic")
    private String firstCoordinateCharacteristic;

    @PropertyField(uri="hasco:hasSecondCoordinate")
    private Float  secondCoordinate;

    @PropertyField(uri="hasco:hasSecondCoordinateUnit")
    private String secondCoordinateUnit;

    @PropertyField(uri="hasco:hasSecondCoordinateCharacteristic")
    private String secondCoordinateCharacteristic;

    @PropertyField(uri="hasco:hasThirdCoordinate")
    private Float  thirdCoordinate;

    @PropertyField(uri="hasco:hasThirdCoordinateUnit")
    private String thirdCoordinateUnit;

    @PropertyField(uri="hasco:hasThirdCoordinateCharecteritic")
    private String thirdCoordinateCharacteristic;

    private String elevation;

    @PropertyField(uri="hasco:partOf")
    private String partOf;

    @PropertyField(uri="hasco:hasImage")
    private String image;

    @PropertyField(uri="hasco:hasLayout")
    private String layout;

    @PropertyField(uri="hasco:hasReferenceLayout")
    private String referenceLayout;

    @PropertyField(uri="hasco:hasUrl")
    private String url;

    @PropertyField(uri="hasco:hasLayoutWidth")
    private Float  width;

    @PropertyField(uri="hasco:hasLayoutWidthUnit")
    private String widthUnit;

    @PropertyField(uri="hasco:hasLayoutDepth")
    private Float  depth;

    @PropertyField(uri="hasco:hasLayoutDepthUnit")
    private String depthUnit;

    @PropertyField(uri="hasco:hasLayoutHeight")
    private Float  height;

    @PropertyField(uri="hasco:hasLayoutHeightUnit")
    private String heightUnit;

    @PropertyField(uri="hasco:hasVersion")
    private String hasVersion;

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public Platform(String uri,
            String typeUri,
            String label,
            String comment) {
        this.uri = uri;
        this.typeUri = typeUri;
        this.label = label;
        this.comment = comment;
    }

    public Platform() {
        this.uri = "";
        this.typeUri = "";
        this.label = "";
        this.comment = "";
        this.location = "";
        this.elevation = "";
        this.partOf = "";
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getElevation() {
        return elevation;
    }
    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public Float getFirstCoordinate() {
        return firstCoordinate;
    }
    public void setFirstCoordinate(Float firstCoordinate) {
        this.firstCoordinate = firstCoordinate;
    }
    public String getFirstCoordinateUnit() {
        return firstCoordinateUnit;
    }
    public String getFirstCoordinateUnitLabel() {
        if (firstCoordinateUnit == null || firstCoordinateUnit.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(firstCoordinateUnit);
    }

    public void setFirstCoordinateUnit(String firstCoordinateUnit) {
        this.firstCoordinateUnit = firstCoordinateUnit;
    }

    public String getFirstCoordinateCharacteristic() {
        return firstCoordinateCharacteristic;
    }

    public String getFirstCoordinateCharacteristicLabel() {
        if (firstCoordinateCharacteristic == null || firstCoordinateCharacteristic.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(firstCoordinateCharacteristic);
    }

    public void setFirstCoordinateCharacteristic(String firstCoordinateCharacteristic) {
        this.firstCoordinateCharacteristic = firstCoordinateCharacteristic;
    }

    public Float getSecondCoordinate() {
        return secondCoordinate;
    }

    public void setSecondCoordinate(Float secondCoordinate) {
        this.secondCoordinate = secondCoordinate;
    }

    public String getSecondCoordinateUnit() {
        return secondCoordinateUnit;
    }

    public String getSecondCoordinateUnitLabel() {
        if (secondCoordinateUnit == null || secondCoordinateUnit.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(secondCoordinateUnit);
    }

    public void setSecondCoordinateUnit(String secondCoordinateUnit) {
        this.secondCoordinateUnit = secondCoordinateUnit;
    }

    public String getSecondCoordinateCharacteristic() {
        return secondCoordinateCharacteristic;
    }

    public String getSecondCoordinateCharacteristicLabel() {
        if (secondCoordinateCharacteristic == null || secondCoordinateCharacteristic.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(secondCoordinateCharacteristic);
    }

    public void setSecondCoordinateCharacteristic(String secondCoordinateCharacteristic) {
        this.secondCoordinateCharacteristic = secondCoordinateCharacteristic;
    }

    public Float getThirdCoordinate() {
        return thirdCoordinate;
    }

    public void setThirdCoordinate(Float thirdCoordinate) {
        this.thirdCoordinate = thirdCoordinate;
    }

    public String getThirdCoordinateUnit() {
        return thirdCoordinateUnit;
    }

    public String getThirdCoordinateUnitLabel() {
        if (thirdCoordinateUnit == null || thirdCoordinateUnit.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(thirdCoordinateUnit);
    }

    public void setThirdCoordinateUnit(String thirdCoordinateUnit) {
        this.thirdCoordinateUnit = thirdCoordinateUnit;
    }

    public String getThirdCoordinateCharacteristic() {
        return thirdCoordinateCharacteristic;
    }

    public String getThirdCoordinateCharacteristicLabel() {
        if (thirdCoordinateCharacteristic == null || thirdCoordinateCharacteristic.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(thirdCoordinateCharacteristic);
    }

    public void setThirdCoordinateCharacteristic(String thirdCoordinateCharacteristic) {
        this.thirdCoordinateCharacteristic = thirdCoordinateCharacteristic;
    }

    public Float getWidth() {
        return width;
    }
    public void setWidth(Float width) {
        this.width = width;
    }

    public String getWidthUnit() {
        return widthUnit;
    }
    public String getWidthUnitLabel() {
        if (widthUnit == null || widthUnit.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(widthUnit);
    }

    public void setWidthUnit(String widthUnit) {
        this.widthUnit = widthUnit;
    }

    public Float getDepth() {
        return depth;
    }

    public void setDepth(Float depth) {
        this.depth = depth;
    }

    public String getDepthUnit() {
        return depthUnit;
    }

    public String getDepthUnitLabel() {
        if (depthUnit == null || depthUnit.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(depthUnit);
    }

    public void setDepthUnit(String depthUnit) {
        this.depthUnit = depthUnit;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getHeightUnit() {
        return heightUnit;
    }

    public String getHeightUnitLabel() {
        if (heightUnit == null || heightUnit.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(heightUnit);
    }

    public void setHeightUnit(String heightUnit) {
        this.heightUnit = heightUnit;
    }

    public String getURL() {
        return url;
    }
    public void setURL(String url) {
        this.url = url;
    }

    public String getPartOf() {
        return partOf;
    }

    public String getHasVersion() {
        return this.hasVersion;
    }
    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getHasSIRManagerEmail() {
        return this.hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    public List<Platform> getImmediateSubPlatforms() {
        List<Platform> subPlatforms = new ArrayList<Platform>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri hasco:partOf <" + uri + "> . " + 
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            Platform platform = find(soln.getResource("uri").getURI());
            subPlatforms.add(platform);
        }			

        if (subPlatforms.size() > 1) {
        	java.util.Collections.sort((List<Platform>) subPlatforms);
        }
        
        return subPlatforms;
    }
    
    public String getPartOfLabel() {
        if (partOf == null || partOf.isEmpty()) {
        	return "";
        }
        return FirstLabel.getPrettyLabel(partOf);
    }

    public void setPartOf(String partOf) {
        this.partOf = partOf;
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
    
    public String getReferenceLayout() {
        return referenceLayout;
    }

    public void setReferenceLayout(String referenceLayout) {
        this.referenceLayout = referenceLayout;
    }
    
    public String getTypeLabel() {
    	PlatformType pltType = PlatformType.find(getTypeUri());
    	if (pltType == null || pltType.getLabel() == null) {
    		return "";
    	}
    	return pltType.getLabel();
    }

    public boolean hasGeoReference() {
    	return getFirstCoordinate() != null && getSecondCoordinate() != null &&
    		   getFirstCoordinateCharacteristic() != null && getSecondCoordinate() != null &&
    		   getFirstCoordinateCharacteristic().equals(LAT) &&
    		   getSecondCoordinateCharacteristic().equals(LONG);
    }
    
    @Override
    public boolean equals(Object o) {
        if((o instanceof Platform) && (((Platform)o).getUri().equals(this.getUri()))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getUri().hashCode();
    }
    
    public static Platform find(String uri) {
 
    	//System.out.println("Platform.find <" + uri + ">");
    	
    	Platform platform = null;
        Statement statement;
        RDFNode object;

        String queryString = "DESCRIBE <" + uri + ">";
        Model model = SPARQLUtils.describe(CollectionUtil.getCollectionPath(
                CollectionUtil.Collection.SPARQL_QUERY), queryString);

        platform = new Platform();
        StmtIterator stmtIterator = model.listStatements();

        while (stmtIterator.hasNext()) {
            statement = stmtIterator.next();
            object = statement.getObject();
            String str = URIUtils.objectRDFToString(object);
            if (statement.getPredicate().getURI().equals(RDFS.LABEL)) {
                platform.setLabel(str);
            } else if (statement.getPredicate().getURI().equals(RDF.TYPE)) {
                platform.setTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HASCO_TYPE)) {
                platform.setHascoTypeUri(str);
            } else if (statement.getPredicate().getURI().equals(RDFS.COMMENT)) {
                platform.setComment(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FIRST_COORDINATE)) {
                platform.setFirstCoordinate(Float.parseFloat(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FIRST_COORDINATE_UNIT)) {
                platform.setFirstCoordinateUnit(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_FIRST_COORDINATE_CHARACTERISTIC)) {
                platform.setFirstCoordinateCharacteristic(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_SECOND_COORDINATE)) {
                platform.setSecondCoordinate(Float.parseFloat(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_SECOND_COORDINATE_UNIT)) {
                platform.setSecondCoordinateUnit(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_SECOND_COORDINATE_CHARACTERISTIC)) {
                platform.setSecondCoordinateCharacteristic(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_THIRD_COORDINATE)) {
                platform.setThirdCoordinate(Float.parseFloat(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_THIRD_COORDINATE_UNIT)) {
            	platform.setThirdCoordinateUnit(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_THIRD_COORDINATE_CHARACTERISTIC)) {
            	platform.setThirdCoordinateCharacteristic(str);
            } else if (statement.getSubject().getURI().equals(uri) && statement.getPredicate().getURI().equals(HASCO.PART_OF)) {
            	platform.setPartOf(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_IMAGE)) {
                platform.setImage(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT)) {
                platform.setLayout(str);
            } else if (statement.getSubject().getURI().equals(uri) && statement.getPredicate().getURI().equals(HASCO.HAS_REFERENCE_LAYOUT)) {
                platform.setReferenceLayout(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT_WIDTH)) {
                platform.setWidth(Float.parseFloat(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT_WIDTH_UNIT)) {
                platform.setWidthUnit(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT_DEPTH)) {
                platform.setDepth(Float.parseFloat(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT_DEPTH_UNIT)) {
                platform.setDepthUnit(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT_HEIGHT)) {
                platform.setHeight(Float.parseFloat(str));
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_LAYOUT_HEIGHT_UNIT)) {
                platform.setHeightUnit(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_URL)) {
                platform.setURL(str);
            } else if (statement.getPredicate().getURI().equals(HASCO.HAS_VERSION)) {
                platform.setHasVersion(str);
            } else if (statement.getPredicate().getURI().equals(VSTOI.HAS_SIR_MANAGER_EMAIL)) {
                platform.setHasSIRManagerEmail(str);
            }
        }

        platform.setUri(uri);

    	//System.out.println("AFTER Platform.find <" + platform + ">");

        return platform;
    }

    public static int getNumberPlatforms() {
        String query = "";
        query += NameSpaces.getInstance().printSparqlNameSpaceList();
        query += " select (count(?uri) as ?tot) where { " + 
                " ?uri hasco:hascoType <" + VSTOI.PLATFORM + "> . " +
                //" ?platModel rdfs:subClassOf* vstoi:Platform . " + 
                //" ?uri a ?platModel ." + 
                "}";

        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), query);

            if (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                return Integer.parseInt(soln.getLiteral("tot").getString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<Platform> findWithPages(int pageSize, int offset) {
        List<Platform> platforms = new ArrayList<Platform>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() + 
        		"SELECT ?uri WHERE { " + 
                " ?uri hasco:hascoType <" + VSTOI.PLATFORM + "> . " +
                //" ?platModel rdfs:subClassOf* vstoi:Platform . " + 
                //" ?uri a ?platModel . } " + 
                " LIMIT " + pageSize + 
                " OFFSET " + offset;

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null && soln.getResource("uri").getURI() != null) {
                Platform platform = Platform.find(soln.getResource("uri").getURI());
                platforms.add(platform);
            }
        }
        return platforms;
    }

    public static List<Platform> find() {
        List<Platform> platforms = new ArrayList<Platform>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri hasco:hascoType <" + VSTOI.PLATFORM + "> . " +
                //" ?platModel rdfs:subClassOf* vstoi:Platform . " + 
                //" ?uri a ?platModel ." + 
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            Platform platform = find(soln.getResource("uri").getURI());
            platforms.add(platform);
        }			

        java.util.Collections.sort((List<Platform>) platforms);

        return platforms;
    }

    public static List<Platform> findWithGeoReferenceAndDeployment() {
        List<Platform> platforms = new ArrayList<Platform>();
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                //" ?platModel rdfs:subClassOf* vstoi:Platform . " + 
                //" ?uri a ?platModel ." +
                " ?uri hasco:hascoType <" + VSTOI.PLATFORM + "> . " +
                " ?uri hasco:hasFirstCoordinate ?lat . " +
                " ?uri hasco:hasSecondCoordinate ?lon . " +
                " ?uri hasco:hasFirstCoordinateCharacteristic <" + LAT + "> . " +
                " ?uri hasco:hasSecondCoordinateCharacteristic <" + LONG + "> . " +
                "} ";

        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            Platform platform = find(soln.getResource("uri").getURI());
            platforms.add(platform);
        }			

        java.util.Collections.sort((List<Platform>) platforms);

        return platforms;
    }

    @Override
    public int compareTo(Platform another) {
        return this.getLabel().compareTo(another.getLabel());
    }

    @Override
    public void save() {
        System.out.println("Saving platform [" + uri + "]");
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }


}
