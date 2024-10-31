package org.hascoapi.entity.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.RDFS;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.SKOS;
import org.hascoapi.vocabularies.VSTOI;

public class GenericFind<T> {

    public static Class getElementClass(String elementType) {
        
        if (elementType.equals("instrument")) {
            return Instrument.class;
        } else if (elementType.equals("instrumentinstance")) {
            return InstrumentInstance.class;
        } else if (elementType.equals("detectorinstance")) {
            return DetectorInstance.class;
        } else if (elementType.equals("platforminstance")) {
            return PlatformInstance.class;
        } else if (elementType.equals("subcontainer")) {
            return Subcontainer.class;
        } else if (elementType.equals("slotelement")) {
            return SlotElement.class;
        } else if (elementType.equals("detectorstem")) {
            return DetectorStem.class;
        } else if (elementType.equals("detector")) {
            return Detector.class;
        } else if (elementType.equals("containerslot")) {
            return ContainerSlot.class;
        } else if (elementType.equals("codebook")) {
            return Codebook.class;
        } else if (elementType.equals("responseoption")) {
            return ResponseOption.class;
        } else if (elementType.equals("codebookslot")) {
            return CodebookSlot.class;
        } else if (elementType.equals("annotationstem")) {
            return AnnotationStem.class;
        } else if (elementType.equals("annotation")) {
            return Annotation.class;
        } else if (elementType.equals("semanticvariable")) {
            return SemanticVariable.class;
        } else if (elementType.equals("instrumenttype")) {
            return InstrumentType.class;
        } else if (elementType.equals("detectorstemtype")) {
            return DetectorStemType.class;
        } else if (elementType.equals("entity")) {
            return Entity.class;
        } else if (elementType.equals("attribute")) {
            return Attribute.class;
        } else if (elementType.equals("unit")) {
            return Unit.class;
        } else if (elementType.equals("agent")) {
            return Agent.class;
        } else if (elementType.equals("ins")) {
            return INS.class;
        } else if (elementType.equals("da")) {
            return DA.class;
        } else if (elementType.equals("dd")) {
            return DD.class;
        } else if (elementType.equals("sdd")) {
            return SDD.class;
        } else if (elementType.equals("sddattribute")) {
            return SDDAttribute.class;
        } else if (elementType.equals("sddobject")) {
            return SDDObject.class;
        } else if (elementType.equals("possiblevalue")) {
            return PossibleValue.class;
        } else if (elementType.equals("semanticdatadictionary")) {
            return SemanticDataDictionary.class;
        } else if (elementType.equals("dp2")) {
            return DP2.class;
        } else if (elementType.equals("str")) {
            return STR.class;
        } else if (elementType.equals("datafile")) {
            return DataFile.class;
        } else if (elementType.equals("dsg")) {
            return DSG.class;
        } else if (elementType.equals("study")) {
            return Study.class;
        } else if (elementType.equals("studyobjectcollection")) {
            return StudyObjectCollection.class;
        } else if (elementType.equals("studyobject")) {
            return StudyObject.class;
        } else if (elementType.equals("studyrole")) {
            return StudyRole.class;
        } else if (elementType.equals("virtualcolumn")) {
            return VirtualColumn.class;
        } else if (elementType.equals("platform")) {
            return Platform.class;
        } else if (elementType.equals("stream")) {
            return Stream.class;
        } else if (elementType.equals("deployment")) {
            return Deployment.class;
        } else if (elementType.equals("person")) {
            return Person.class;
        } else if (elementType.equals("organization")) {
            return Organization.class;
        } else if (elementType.equals("place")) {
            return Place.class;
        } else if (elementType.equals("postaladdress")) {
            return PostalAddress.class;
        } else if (elementType.equals("kgr")) {
            return KGR.class;
        } 
        return null;
    }


    private static String classNameWithNamespace (Class clazz) {
        if (clazz == InstrumentInstance.class) {
            return URIUtils.replaceNameSpace(VSTOI.INSTRUMENT_INSTANCE);
        } else if (clazz == DetectorInstance.class) {
            return URIUtils.replaceNameSpace(VSTOI.DETECTOR_INSTANCE);
        } else if (clazz == PlatformInstance.class) {
            return URIUtils.replaceNameSpace(VSTOI.PLATFORM_INSTANCE);
        } else if (clazz == ContainerSlot.class) {
            return URIUtils.replaceNameSpace(VSTOI.CONTAINER_SLOT);
        } else if (clazz == Codebook.class) {
            return URIUtils.replaceNameSpace(VSTOI.CODEBOOK);
        } else if (clazz == CodebookSlot.class) {
            return URIUtils.replaceNameSpace(VSTOI.CODEBOOK_SLOT);
        } else if (clazz == ResponseOption.class) {
            return URIUtils.replaceNameSpace(VSTOI.RESPONSE_OPTION);
        } else if (clazz == AnnotationStem.class) {
            return URIUtils.replaceNameSpace(VSTOI.ANNOTATION_STEM);
        } else if (clazz == Annotation.class) {
            return URIUtils.replaceNameSpace(VSTOI.ANNOTATION);
        } else if (clazz == SemanticVariable.class) {
            return URIUtils.replaceNameSpace(HASCO.SEMANTIC_VARIABLE);
        } else if (clazz == Agent.class) {
            return URIUtils.replaceNameSpace(HASCO.AGENT);
        } else if (clazz == INS.class) {
            return URIUtils.replaceNameSpace(HASCO.INS);
        } else if (clazz == DA.class) {
            return URIUtils.replaceNameSpace(HASCO.DATA_ACQUISITION);
        } else if (clazz == DD.class) {
            return URIUtils.replaceNameSpace(HASCO.DD);
        } else if (clazz == SDD.class) {
            return URIUtils.replaceNameSpace(HASCO.SDD);
        } else if (clazz == SemanticDataDictionary.class) {
            return URIUtils.replaceNameSpace(HASCO.SEMANTIC_DATA_DICTIONARY);
        } else if (clazz == SDDAttribute.class) {
            return URIUtils.replaceNameSpace(HASCO.SDD_ATTRIBUTE);
        } else if (clazz == SDDObject.class) {
            return URIUtils.replaceNameSpace(HASCO.SDD_OBJECT);
        } else if (clazz == PossibleValue.class) {
            return URIUtils.replaceNameSpace(HASCO.POSSIBLE_VALUE);
        } else if (clazz == DP2.class) {
            return URIUtils.replaceNameSpace(HASCO.DP2);
        } else if (clazz == STR.class) {
            return URIUtils.replaceNameSpace(HASCO.STR);
        } else if (clazz == DataFile.class) {
            return URIUtils.replaceNameSpace(HASCO.DATAFILE);
        } else if (clazz == DSG.class) {
            return URIUtils.replaceNameSpace(HASCO.DSG);
        } else if (clazz == Study.class) {
            return URIUtils.replaceNameSpace(HASCO.STUDY);
        } else if (clazz == StudyObjectCollection.class) {
            return URIUtils.replaceNameSpace(HASCO.STUDY_OBJECT_COLLECTION);
        } else if (clazz == StudyObject.class) {
            return URIUtils.replaceNameSpace(HASCO.STUDY_OBJECT);
        } else if (clazz == StudyRole.class) {
            return URIUtils.replaceNameSpace(HASCO.STUDY_ROLE);
        } else if (clazz == VirtualColumn.class) {
            return URIUtils.replaceNameSpace(HASCO.VIRTUAL_COLUMN);
        } else if (clazz == Stream.class) {
            return URIUtils.replaceNameSpace(HASCO.STREAM);
        } else if (clazz == Deployment.class) {
            return URIUtils.replaceNameSpace(VSTOI.DEPLOYMENT);
        } else if (clazz == Person.class) {
            return URIUtils.replaceNameSpace(FOAF.PERSON);
        } else if (clazz == Organization.class) {
            return URIUtils.replaceNameSpace(FOAF.ORGANIZATION);
        } else if (clazz == Place.class) {
            return URIUtils.replaceNameSpace(SCHEMA.PLACE);
        } else if (clazz == PostalAddress.class) {
            return URIUtils.replaceNameSpace(SCHEMA.POSTAL_ADDRESS);
        } else if (clazz == KGR.class) {
            return URIUtils.replaceNameSpace(HASCO.KNOWLEDGE_GRAPH);
        }
        return null;
    }

    private static boolean isSIR (Class clazz) {
        // Instrument/Container is not SIR Element
        if (clazz == DetectorStem.class ||
            clazz == Detector.class ||
            clazz == ResponseOption.class ||
            clazz == AnnotationStem.class) {
            return true;
        }
        return false;
    }
 
    public static boolean isMT (Class clazz) {
        // MT is Metadata Template
        if (clazz == SDD.class ||
            clazz == DP2.class || 
            clazz == STR.class ||
            clazz == INS.class ||
            clazz == DA.class ||
            clazz == DD.class ||
            clazz == KGR.class ||
            clazz == DSG.class) {
            return true;
        }
        return false;
    }

    private static String superclassNameWithNamespace (Class clazz) {
        //if (clazz == InstrumentType.class) {
        //    return URIUtils.replaceNameSpace(VSTOI.INSTRUMENT);
        //} else 
        if (clazz == Instrument.class) {
            return URIUtils.replaceNameSpace(VSTOI.INSTRUMENT);
        //} else if (clazz == DetectorStemType.class) {
        //    return URIUtils.replaceNameSpace(VSTOI.DETECTOR_STEM);
        } else if (clazz == DetectorStem.class) {
            return URIUtils.replaceNameSpace(VSTOI.DETECTOR_STEM);
        } else if (clazz == Detector.class) {
            return URIUtils.replaceNameSpace(VSTOI.DETECTOR);
        } else if (clazz == Platform.class) {
            return URIUtils.replaceNameSpace(VSTOI.PLATFORM);
        } else if (clazz == Entity.class) {
            return URIUtils.replaceNameSpace(SIO.ENTITY);
        } else if (clazz == Attribute.class) {
            return URIUtils.replaceNameSpace(SIO.ATTRIBUTE);
        } else if (clazz == Unit.class) {
            return URIUtils.replaceNameSpace(SIO.UNIT);
        } 
        return null;
    }

    /**
     *     FIND ELEMENTS (AND THEIR TOTALS) WITH PAGES
     */

    public static <T> List<T> findWithPages(Class clazz, int pageSize, int offset) {
        if (clazz == null) {
            return null;
        }
        String className = classNameWithNamespace(clazz);
        if (className != null) {
            if (clazz == Detector.class) {
              return findDetectorInstancesWithPages(clazz, className, pageSize, offset);
            } else if (isSIR(clazz)) {
              return findSIRInstancesWithPages(clazz, className, pageSize, offset);
            } else if (isMT(clazz)) {
              return findMTInstancesWithPages(clazz, className, pageSize, offset);
            } else {
              return findInstancesWithPages(clazz, className, pageSize, offset);
            }
        }
        String superClassName = superclassNameWithNamespace(clazz);
        if (superClassName != null) {
            return findSubclassesWithPages(clazz, superClassName, pageSize, offset);
        }
        return null;
    }

    private static <T> List<T> findDetectorInstancesWithPages(Class clazz, String className, int pageSize, int offset) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* " + className + " . " +
                " ?uri a ?type ." +
                " ?uri vstoi:hasDetectorStem ?stem . " +
                " ?stem vstoi:hasContent ?content . " +
                "} " +
                " ORDER BY ASC(?content) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findByQuery(clazz, queryString);
    }
    
    private static <T> List<T> findSIRInstancesWithPages(Class clazz, String className, int pageSize, int offset) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* " + className + " . " +
                " ?uri a ?type ." +
                " ?uri vstoi:hasContent ?content . " +
                "} " +
                " ORDER BY ASC(?content) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findByQuery(clazz, queryString);
    }
        
    private static <T> List<T> findMTInstancesWithPages(Class clazz, String className, int pageSize, int offset) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* " + className + " . " +
                " ?uri a ?type ." +
                " ?uri hasco:hasDataFile ?dataFile . " +   // a MT concept requires an associated DataFile
                " OPTIONAL { ?uri rdfs:label ?label . } " +
                "} " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findByQuery(clazz, queryString);
    }
    
    private static <T> List<T> findInstancesWithPages(Class clazz, String className, int pageSize, int offset) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?type rdfs:subClassOf* " + className + " . " +
                " ?uri a ?type ." +
                " OPTIONAL { ?uri rdfs:label ?label . } " +
                "} " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findByQuery(clazz, queryString);
    }
    
    private static <T> List<T> findSubclassesWithPages(Class clazz, String superClassName, int pageSize, int offset) {
        //System.out.println("subClassName: " + subClassName);
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + superClassName + " . " +
                " ?uri rdfs:label ?label . " +
                "} " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        return findByQuery(clazz, queryString);
    }
    
    public static int getNumberElements(String elementType) {
        if (elementType == null || elementType.isEmpty()) {
            return -1   ;
        }
        Class clazz = getElementClass(elementType);
        if (clazz == null) {        
            return -1;
        }
        return GenericFind.findTotal(clazz);
    }

    public static int findTotal(Class clazz) {
        String className = classNameWithNamespace(clazz);
        if (className != null) {
            return findTotalInstances(className);
        }
        String superClassName = superclassNameWithNamespace(clazz);
        if (superClassName != null) {
            return findTotalSubclasses(superClassName);
        }
        return -1;
    }

    private static int findTotalInstances(String className) {
        String queryString = "";
        queryString += NameSpaces.getInstance().printSparqlNameSpaceList();
        queryString += " select (count(?uri) as ?tot) where { " +
                //" ?type rdfs:subClassOf* " + className + " . " +
                //" ?uri a ?type ." +
                " ?uri hasco:hascoType " + className + " . " +
                "}";
        //System.out.println("findTotalInstances: " + queryString);
        return findTotalByQuery(queryString);
    }

	private static int findTotalSubclasses(String superClassName) {
        String queryString = "";
        queryString += NameSpaces.getInstance().printSparqlNameSpaceList();
        queryString += " select (count(?type) as ?tot) where { " +
                " ?type rdfs:subClassOf* " + superClassName + " . " +
                "}";
        return findTotalByQuery(queryString);
	}

    /**
     *     FIND ELEMENTS BY KEYWORD (AND THEIR TOTALS) WITH PAGES
     */

    public static <T> List<T> findByKeywordWithPages(Class clazz, String keyword, int pageSize, int offset) {
        if (clazz == null) {
            return null;
        }
        String superclassName = null;
        String className = classNameWithNamespace(clazz);
        if (className == null) {
            superclassName = superclassNameWithNamespace(clazz);
        }
        if (className == null && superclassName == null) {
            return null;
        }
        //System.out.println("findByKeywordWithPages: hascoType=[" + hascoType+ "]");
        if (clazz == Detector.class) {
            return findDetectorClassesByKeywordWithPages(clazz, superclassName, keyword, pageSize, offset);
        } else if (isSIR(clazz) && superclassName != null) {
            return findSIRClassesByKeywordWithPages(clazz, superclassName, keyword, pageSize, offset);
        } else if (isSIR(clazz) && className != null) {
            return findSIRInstancesByKeywordWithPages(clazz, className, keyword, pageSize, offset);
        } else if (superclassName != null) {
            return findClassesByKeywordWithPages(clazz, superclassName, keyword, pageSize, offset);
        }             
        return findInstancesByKeywordWithPages(clazz, className, keyword, pageSize, offset);
    }

    public static <T> List<T> findDetectorClassesByKeywordWithPages(Class clazz, String superclassName, String keyword, int pageSize, int offset) {
        System.out.println("GenericFind.findDetectorClassesByKeywordWithPages: " + superclassName + "  " + keyword + "  " + pageSize + "  " + offset);
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + superclassName + " . " +
                //" ?uri hasco:hascoType " + hascoType + " . " +
                //" ?uri vstoi:hasDetectorStem ?stem . " +
                " ?stem vstoi:hasContent ?content . " +
                "   FILTER regex(?content, \"" + keyword + "\", \"i\") " +
                "} " +
                " ORDER BY ASC(?content) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;

        //System.out.println("GenericFind.findSIRInstancesByKeywordWithPages: [" + queryString + "]");
        return findByQuery(clazz, queryString);
    }

    public static <T> List<T> findSIRClassesByKeywordWithPages(Class clazz, String superclassName, String keyword, int pageSize, int offset) {
        //System.out.println("GenericFind.findSIRInstancesByKeywordWithPages: " + superclassName + " " + keyword + "  " + pageSize + "  " + offset);
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + superclassName + " . " +
                //" ?uri hasco:hascoType " + hascoType + " . " +
                " ?uri vstoi:hasContent ?content . " +
                "   FILTER regex(?content, \"" + keyword + "\", \"i\") " +
                "} " +
                " ORDER BY ASC(?content) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;

        //System.out.println("GenericFind.findSIRInstancesByKeywordWithPages: [" + queryString + "]");
        return findByQuery(clazz, queryString);
    }

    public static <T> List<T> findSIRInstancesByKeywordWithPages(Class clazz, String className, String keyword, int pageSize, int offset) {
        //System.out.println("GenericFind.findSIRInstancesByKeywordWithPages: " + className + " " + keyword + "  " + pageSize + "  " + offset);
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                //" ?type rdfs:subClassOf* " + className + " . " +
                //" ?uri a ?type ." +
                " ?uri hasco:hascoType " + className + " . " +
                " ?uri vstoi:hasContent ?content . " +
                "   FILTER regex(?content, \"" + keyword + "\", \"i\") " +
                "} " +
                " ORDER BY ASC(?content) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;

        //System.out.println("GenericFind.findSIRInstancesByKeywordWithPages: [" + queryString + "]");
        return findByQuery(clazz, queryString);
    }

    public static <T> List<T> findClassesByKeywordWithPages(Class clazz, String superclassName, String keyword, int pageSize, int offset) {
        //System.out.println("GenericFind.findClassesByKeywordWithPages: " + superclassName + "  " + keyword + " " + pageSize + "  " + offset);
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                " ?uri rdfs:subClassOf* " + superclassName + " . " +
                //" ?uri hasco:hascoType " + hascoType + " . " +
                " ?uri rdfs:label ?label . " +
                "   FILTER regex(?label, \"" + keyword + "\", \"i\") " +
                "} " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;

        //System.out.println("GenericFind.findInstancesByKeywordWithPages: [" + queryString + "]");
        return findByQuery(clazz, queryString);
    }

    public static <T> List<T> findInstancesByKeywordWithPages(Class clazz, String className, String keyword, int pageSize, int offset) {
        //System.out.println("GenericFind.findInstancesByKeywordWithPages: " + superclassName + "  " + keyword + " " + pageSize + "  " + offset);
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT ?uri WHERE { " +
                //" ?type rdfs:subClassOf* " + className + " . " +
                //" ?uri a ?type ." +
                " ?uri hasco:hascoType " + className + " . " +
                " ?uri rdfs:label ?label . " +
                "   FILTER regex(?label, \"" + keyword + "\", \"i\") " +
                "} " +
                " ORDER BY ASC(?label) " +
                " LIMIT " + pageSize +
                " OFFSET " + offset;
        //System.out.println("GenericFind.findInstancesByKeywordWithPages: [" + queryString + "]");
        return findByQuery(clazz, queryString);
    }

    public static int findTotalByKeyword(Class clazz, String keyword) {
        String hascoType = classNameWithNamespace(clazz);
        if (hascoType == null) {
            hascoType = superclassNameWithNamespace(clazz);
        }
        if (hascoType == null) {
            return -1;
        }
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
            " SELECT (count(?uri) as ?tot) WHERE { " +
            //" ?type rdfs:subClassOf* " + className + " . " +
            //" ?uri a ?type . " +
            " ?uri hasco:hascoType " + hascoType + " . ";
        if (keyword != null) {
            queryString += " ?uri rdfs:label ?label . " +
                "   FILTER regex(?label, \"" + keyword + "\", \"i\") ";
        }
        queryString += "} ";
        return findTotalByQuery(queryString);
    }

    /* 
    public static int findTotalElementsByKeyword(String hascoType, String keyword) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT (count(?uri) as ?tot) WHERE { " +
                //" ?type rdfs:subClassOf* " + className + " . " +
                //" ?uri a ?type . " +
                " ?uri hasco:hascoType " + hascoType + " . ";
        if (keyword != null) {
            queryString += " ?uri rdfs:label ?label . " +
                "   FILTER regex(?label, \"" + keyword + "\", \"i\") ";
        }
        queryString += "} ";

        return findTotalByQuery(queryString);
    }
    */

    /* 
    public static int findTotalSubclassesByKeyword(String superClassName, String keyword) {
        String queryString = NameSpaces.getInstance().printSparqlNameSpaceList() +
                " SELECT (count(?uri) as ?tot) WHERE { " +
                //" ?uri rdfs:subClassOf* " + superClassName + " . " +
                " ?uri hasco:hascoType " + superClassName + " . " +
                " ?uri rdfs:label ?label . " +
                "   FILTER regex(?label, \"" + keyword + "\", \"i\") " +
                "} ";

        return findTotalByQuery(queryString);
    }
    */

    /**
     *     FIND ELEMENTS BY KEYWORD AND LANGUAGE (AND THEIR TOTALS) WITH PAHES
     */

    public static <T> List<T> findByKeywordAndLanguageWithPages(Class clazz, String keyword, String language, int pageSize, int offset) {
        if (clazz == null) {
            return null;
        }
        String hascoType = classNameWithNamespace(clazz);
        if (hascoType == null) {
            hascoType = superclassNameWithNamespace(clazz);
        }
        if (hascoType == null) {
            return null;
        }
        if (clazz == Detector.class) {
            return findDetectorInstancesByKeywordAndLanguageWithPages(clazz, hascoType, keyword, language, pageSize, offset);
        } else if (isSIR(clazz)) {
            return findSIRElementsByKeywordAndLanguageWithPages(clazz, hascoType, keyword, language, pageSize, offset);
        } else {
            return findElementsByKeywordAndLanguageWithPages(clazz, hascoType, keyword, language, pageSize, offset);
        }
    }

	public static <T> List<T> findDetectorInstancesByKeywordAndLanguageWithPages(Class clazz, String hascoType, String keyword, String language, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				//" ?type rdfs:subClassOf* " + className + " . " +
				//" ?uri a ?type . " +
                " ?uri hasco:hascoType " + hascoType + " . " +
                " ?uri vstoi:hasDetectorStem ?stem . ";
		if (!language.isEmpty()) {
			queryString += " ?stem vstoi:hasLanguage ?language . ";
		}
		queryString += " OPTIONAL { ?stem vstoi:hasContent ?content . } ";
		if (!keyword.isEmpty() && !language.isEmpty()) {
			queryString += "   FILTER (regex(?content, \"" + keyword + "\", \"i\") && (?language = \"" + language + "\")) ";
		} else if (!keyword.isEmpty()) {
			queryString += "   FILTER (regex(?content, \"" + keyword + "\", \"i\")) ";
		} else if (!language.isEmpty()) {
			queryString += "   FILTER ((?language = \"" + language + "\")) ";
		}
		queryString += "} " +
                " ORDER BY ASC(?content) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public static <T> List<T> findSIRElementsByKeywordAndLanguageWithPages(Class clazz, String hascoType, String keyword, String language, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				//" ?type rdfs:subClassOf* " + className + " . " +
				//" ?uri a ?type .";
                " ?uri hasco:hascoType " + hascoType + " . ";
		if (!language.isEmpty()) {
			queryString += " ?uri vstoi:hasLanguage ?language . ";
		}
		queryString += " OPTIONAL { ?uri vstoi:hasContent ?content . } ";
		if (!keyword.isEmpty() && !language.isEmpty()) {
			queryString += "   FILTER (regex(?content, \"" + keyword + "\", \"i\") && (?language = \"" + language + "\")) ";
		} else if (!keyword.isEmpty()) {
			queryString += "   FILTER (regex(?content, \"" + keyword + "\", \"i\")) ";
		} else if (!language.isEmpty()) {
			queryString += "   FILTER ((?language = \"" + language + "\")) ";
		}
		queryString += "} " +
                " ORDER BY ASC(?content) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
        //System.out.println("GenericFind: KeywordAndLangauge - " + className + " query [" + queryString + "]");
		return findByQuery(clazz, queryString);
	}

	public static <T> List<T> findElementsByKeywordAndLanguageWithPages(Class clazz, String hascoType, String keyword, String language, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				//" ?type rdfs:subClassOf* " + className + " . " +
				//" ?uri a ?type .";
                " ?uri hasco:hascoType " + hascoType + " . ";
		if (!language.isEmpty()) {
			queryString += " ?uri vstoi:hasLanguage ?language . ";
		}
		queryString += " OPTIONAL { ?uri rdfs:label ?label . } ";
		if (!keyword.isEmpty() && !language.isEmpty()) {
			queryString += "   FILTER (regex(?label, \"" + keyword + "\", \"i\") && (?language = \"" + language + "\")) ";
		} else if (!keyword.isEmpty()) {
			queryString += "   FILTER (regex(?label, \"" + keyword + "\", \"i\")) ";
		} else if (!language.isEmpty()) {
			queryString += "   FILTER ((?language = \"" + language + "\")) ";
		}
		queryString += "} " +
                " ORDER BY ASC(?label) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

    /* 
	public static <T> List<T> findSubClassesByKeywordAndLanguageWithPages(Class clazz, String subClassName, String keyword, String language, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				" ?uri rdfs:subClassOf* " + subClassName + " . ";
		if (!language.isEmpty()) {
			queryString += " ?uri vstoi:hasLanguage ?language . ";
		}
		queryString += " OPTIONAL { ?uri rdfs:label ?label . } ";
		if (!keyword.isEmpty() && !language.isEmpty()) {
			queryString += "   FILTER (regex(?label, \"" + keyword + "\", \"i\") && (?language = \"" + language + "\")) ";
		} else if (!keyword.isEmpty()) {
			queryString += "   FILTER (regex(?label, \"" + keyword + "\", \"i\")) ";
		} else if (!language.isEmpty()) {
			queryString += "   FILTER ((?language = \"" + language + "\")) ";
		}
		queryString += "} " +
                " ORDER BY ASC(?label) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}
    */

	public static int findTotalByKeywordAndLanguage(Class clazz, String keyword, String language) {
        String hascoType = classNameWithNamespace(clazz);
        if (hascoType == null) {
            hascoType = superclassNameWithNamespace(clazz);
        }
        if (hascoType == null) {
            return -1;
        }
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
//				" ?type rdfs:subClassOf* " + classNameWithNamespace(clazz) + " . " +
//				" ?uri a ?type .";
                " ?uri hasco:hascoType " + hascoType + " . ";
		if (!language.isEmpty()) {
			queryString += " ?uri vstoi:hasLanguage ?language . ";
		}
		if (!keyword.isEmpty()) {
			queryString += " ?uri rdfs:label ?label . ";
		}
		if (!keyword.isEmpty() && !language.isEmpty()) {
			queryString += "   FILTER (regex(?label, \"" + keyword + "\", \"i\") && (?language = \"" + language + "\")) ";
		} else if (!keyword.isEmpty()) {
			queryString += "   FILTER (regex(?label, \"" + keyword + "\", \"i\")) ";
		} else if (!language.isEmpty()) {
			queryString += "   FILTER ((?language = \"" + language + "\")) ";
		}
		queryString += "}";
        return findTotalByQuery(queryString);
	}

    /**
     *     FIND ELEMENTS BY MANAGER (AND THEIR TOTALS)
     */

	public List<T> findByManagerEmailWithPages(Class clazz, String managerEmail, int pageSize, int offset) {
        //System.out.println("findByManagerEmailWithPages: Clazz=[" + clazz + "]");
        String hascoTypeStr = classNameWithNamespace(clazz);
        if (hascoTypeStr == null || hascoTypeStr.isEmpty()) {
            hascoTypeStr = superclassNameWithNamespace(clazz);
        }
        if (hascoTypeStr == null || hascoTypeStr.isEmpty()) {
            return null;
        }
        //System.out.println("findByManagerEmailWithPages: hascoTypeStr=[" + hascoTypeStr + "]");
        if (clazz == Detector.class) {
            return findDetectorInstancesByManagerEmailWithPages(clazz, hascoTypeStr, managerEmail, pageSize, offset);
        } else if (isSIR(clazz)) {
            return findSIRInstancesByManagerEmailWithPages(clazz, hascoTypeStr, managerEmail, pageSize, offset);
        } else if (isMT(clazz)) {
            return findMTInstancesByManagerEmailWithPages(clazz, hascoTypeStr, managerEmail, pageSize, offset);
        } else {
            return findElementsByManagerEmailWithPages(clazz, hascoTypeStr, managerEmail, pageSize, offset);
        }
    }

	public List<T> findDetectorInstancesByManagerEmailWithPages(Class clazz, String hascoTypeStr, String managerEmail, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				//" ?model rdfs:subClassOf* " + className + " . " +
				//" ?uri a ?model ." +
                " ?uri hasco:hascoType " + hascoTypeStr + " . " +
                " ?uri vstoi:hasDetectorStem ?stem . " +
                " OPTIONAL { ?stem vstoi:hasContent ?content . } " +
				" ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"}" +
				" ORDER BY ASC(?content) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public List<T> findSIRInstancesByManagerEmailWithPages(Class clazz, String hascoTypeStr, String managerEmail, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				//" ?model rdfs:subClassOf* " + className + " . " +
				//" ?uri a ?model ." +
				" ?uri hasco:hascoType " + hascoTypeStr + " . " +
                " OPTIONAL { ?uri vstoi:hasContent ?content . } " +
				" ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"}" +
				" ORDER BY ASC(?content) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public List<T> findMTInstancesByManagerEmailWithPages(Class clazz, String hascoTypeStr, String managerEmail, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				" ?uri hasco:hascoType " + hascoTypeStr + " . " +
				" OPTIONAL { ?uri rdfs:label ?label . } " +
                " ?uri hasco:hasDataFile ?dataFile . " +   // a MT concept requires an associated DataFile
				" ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"}" +
				" ORDER BY ASC(?label) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public List<T> findElementsByManagerEmailWithPages(Class clazz, String hascoTypeStr, String managerEmail, int pageSize, int offset) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
				//" ?model rdfs:subClassOf* " + className + " . " +
				//" ?uri a ?model ." +
				" ?uri hasco:hascoType " + hascoTypeStr + " . " +
				" OPTIONAL { ?uri rdfs:label ?label . } " +
				" ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"}" +
				" ORDER BY ASC(?label) " +
				" LIMIT " + pageSize +
				" OFFSET " + offset;
        //System.out.println(queryString);
		return findByQuery(clazz, queryString);
	}

	public List<T> findByManagerEmailWithPagesByStudy(Class clazz, String studyuri, String managerEmail, int pageSize, int offset) {
        String className = classNameWithNamespace(clazz);
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
            //" ?model rdfs:subClassOf* " + className + " . " +
            //" ?uri a ?model ." +
            " ?uri hasco:hascoType " + className + " . " +
            " OPTIONAL { ?uri rdfs:label ?label . } " +
            " ?uri vstoi:hasSIRManagerEmail ?managerEmail . ";
        if (clazz.equals(StudyObject.class)) {
            queryString += "   ?uri hasco:isMemberOf ?socuri . " +
                "   ?socuri hasco:isMemberOf <" + studyuri + "> . "; 
        } else {
            queryString += "   ?uri hasco:isMemberOf <" + studyuri + "> . "; 
        }
        queryString += "   FILTER (?managerEmail = \"" + managerEmail + "\") " +
            "}" +
            " ORDER BY ASC(?label) " +
            " LIMIT " + pageSize +
            " OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public List<T> findByManagerEmailWithPagesBySOC(Class clazz, String studyobjectcollectionuri, String managerEmail, int pageSize, int offset) {
        String className = classNameWithNamespace(clazz);
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
            "  ?uri hasco:hascoType " + classNameWithNamespace(clazz) + " . " +
            "  OPTIONAL { ?uri rdfs:label ?label . } " +
            "  ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
            "  ?uri hasco:isMemberOf <" + studyobjectcollectionuri + "> . " + 
            "  FILTER (?managerEmail = \"" + managerEmail + "\") " +
            "}" +
            "  ORDER BY ASC(?label) " +
            "  LIMIT " + pageSize +
            "  OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public List<T> findByManagerEmailWithPagesByContainer(Class clazz, String containeruri, String managerEmail, int pageSize, int offset) {
        String className = classNameWithNamespace(clazz);
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT ?uri WHERE { " +
            "  ?uri hasco:hascoType " + classNameWithNamespace(clazz) + " . " +
            "  OPTIONAL { ?uri rdfs:label ?label . } " +
            "  ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
            "  ?uri vstoi:belongsTo <" + containeruri + "> . " + 
            "  FILTER (?managerEmail = \"" + managerEmail + "\") " +
            "}" +
            "  ORDER BY ASC(?label) " +
            "  LIMIT " + pageSize +
            "  OFFSET " + offset;
		return findByQuery(clazz, queryString);
	}

	public static int findTotalByManagerEmail(Class clazz, String managerEmail) {
        if (isMT(clazz)) {
            return findTotalMTByManagerEmail(clazz,managerEmail);
        } else {
            String hascoTypeStr = classNameWithNamespace(clazz);
            if (hascoTypeStr == null || hascoTypeStr.isEmpty()) {
                hascoTypeStr = superclassNameWithNamespace(clazz);
            }
            if (hascoTypeStr == null || hascoTypeStr.isEmpty()) {
                return -1;
            }
            return findTotalElementsByManagerEmail(hascoTypeStr,managerEmail);
        }
    }

	public static int findTotalElementsByManagerEmail(String hascoTypeStr, String managerEmail) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
				//" ?model rdfs:subClassOf* " + classNameWithNamespace(clazz) + " . " +
				//" ?uri a ?model ." +
				" ?uri hasco:hascoType " + hascoTypeStr + " . " +
				" ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"}";
        return findTotalByQuery(queryString);
	}

	public static int findTotalByManagerEmailByStudy(Class clazz, String studyuri, String manageremail) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
			" ?uri hasco:hascoType " + classNameWithNamespace(clazz) + " . ";
        if (clazz.equals(StudyObject.class)) {
            queryString += "   ?uri hasco:isMemberOf ?socuri . " +
                "   ?socuri hasco:isMemberOf <" + studyuri + "> . "; 
        } else {
            queryString += "   ?uri hasco:isMemberOf <" + studyuri + "> . "; 
        }
        queryString += " ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
			"   FILTER (?managerEmail = \"" + manageremail + "\") " +
			"}";
        return findTotalByQuery(queryString);
	}

	public static int findTotalByManagerEmailBySOC(Class clazz, String studyobjectcollectionuri, String manageremail) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
			" ?uri hasco:hascoType " + classNameWithNamespace(clazz) + " . " +
            " ?uri hasco:isMemberOf <" + studyobjectcollectionuri + "> . " +
            " ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
			"   FILTER (?managerEmail = \"" + manageremail + "\") " +
			"}";
        return findTotalByQuery(queryString);
	}

	public static int findTotalByManagerEmailByContainer(Class clazz, String containeruri, String manageremail) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
			" ?uri hasco:hascoType " + classNameWithNamespace(clazz) + " . " +
            " ?uri vstoi:belongsTo <" + containeruri + "> . " +
            " ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
			"   FILTER (?managerEmail = \"" + manageremail + "\") " +
			"}";
        return findTotalByQuery(queryString);
	}

	public static int findTotalMTByManagerEmail(Class clazz, String managerEmail) {
		String queryString = NameSpaces.getInstance().printSparqlNameSpaceList();
		queryString += " SELECT (count(?uri) as ?tot) WHERE { " +
				//" ?model rdfs:subClassOf* " + classNameWithNamespace(clazz) + " . " +
				//" ?uri a ?model ." +
				" ?uri hasco:hascoType " + classNameWithNamespace(clazz) + " . " +
                " ?uri hasco:hasDataFile ?dataFile . " +   // a MT concept requires an associated DataFile
				" ?uri vstoi:hasSIRManagerEmail ?managerEmail . " +
				"   FILTER (?managerEmail = \"" + managerEmail + "\") " +
				"}";
        return findTotalByQuery(queryString);
	}


    /** 
     *    QUERY EXECUTION
     */

    private static <T> List<T> findByQuery(Class clazz,String queryString) {
        //System.out.println("FindByQuery: query = [" + queryString + "]");
        List<T> list = new ArrayList<T>();
        ResultSetRewindable resultsrw = SPARQLUtils.select(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

        if (!resultsrw.hasNext()) {
            return null;
        }

        while (resultsrw.hasNext()) {
            QuerySolution soln = resultsrw.next();
            if (soln != null) {
                if (soln.getResource("uri") != null && soln.getResource("uri").getURI() != null) {
                    String uri = soln.getResource("uri").getURI();
                    //System.out.println("FindByQuery: retrieved uri = [" + uri + "]");
                    T element = findElement(clazz, uri);
                    if (element != null) {                        
                      list.add(element);
                    }
                } else {
                    System.out.println("[ERROR] Failed to retrieve URI for objects selected in a query.");
                }
            }
        }
        return list;
    }

    private static <T> T findElement(Class clazz, String uri) {

        // List of subclasses 
        if (clazz == InstrumentType.class) {
            return (T)InstrumentType.find(uri);
        } else if (clazz == DetectorStemType.class) {
            return (T)DetectorStemType.find(uri);
        } else if (clazz == Entity.class) {
            return (T)Entity.find(uri);
        } else if (clazz == Attribute.class) {
            return (T)Attribute.find(uri);
        } else if (clazz == Unit.class) {
            return (T)Unit.find(uri);
        } else if (clazz == StudyObjectCollectionType.class) {
            return (T)StudyObjectCollectionType.find(uri);
        } else if (clazz == StudyObjectType.class) {
            return (T)StudyObjectType.find(uri);
        } else if (clazz == Instrument.class) {
            return (T)Instrument.find(uri);
        } else if (clazz == Subcontainer.class) {
            return (T)Subcontainer.find(uri);
        } else if (clazz == DetectorStem.class) {
            return (T)DetectorStem.find(uri);
        } else if (clazz == Detector.class) {
            return (T)Detector.findDetector(uri);
        } else if (clazz == Platform.class) {
            return (T)Platform.find(uri);

        // List of instances
        } else if (clazz == InstrumentInstance.class) {
            return (T)InstrumentInstance.find(uri);
        } else if (clazz == DetectorInstance.class) {
            return (T)DetectorInstance.find(uri);
        } else if (clazz == PlatformInstance.class) {
            return (T)PlatformInstance.find(uri);
        } else if (clazz == ContainerSlot.class) {
            return (T)ContainerSlot.find(uri);
        } else if (clazz == Codebook.class) {
            return (T)Codebook.find(uri);
        } else if (clazz == CodebookSlot.class) {
            return (T)CodebookSlot.find(uri);
        } else if (clazz == ResponseOption.class) {
            return (T)ResponseOption.find(uri);
        } else if (clazz == AnnotationStem.class) {
            return (T)AnnotationStem.find(uri);
        } else if (clazz == Annotation.class) {
            return (T)Annotation.find(uri);
        } else if (clazz == SemanticVariable.class) {
            return (T)SemanticVariable.find(uri);
        } else if (clazz == INS.class) {
            return (T)INS.find(uri);
        } else if (clazz == DA.class) {
            return (T)DA.find(uri);
        } else if (clazz == DD.class) {
            return (T)DD.find(uri);
        } else if (clazz == SDD.class) {
            return (T)SDD.find(uri);
        } else if (clazz == SDDAttribute.class) {
            return (T)SDDAttribute.find(uri);
        } else if (clazz == SDDObject.class) {
            return (T)SDDObject.find(uri);
        } else if (clazz == PossibleValue.class) {
            return (T)PossibleValue.find(uri);
        } else if (clazz == SemanticDataDictionary.class) {
            return (T)SemanticDataDictionary.find(uri);
        } else if (clazz == DP2.class) {
            return (T)DP2.find(uri);
        } else if (clazz == STR.class) {
            return (T)STR.find(uri);
        } else if (clazz == DataFile.class) {
            return (T)DataFile.find(uri);
        } else if (clazz == DSG.class) {
            return (T)DSG.find(uri);
        } else if (clazz == Study.class) {
            return (T)Study.find(uri);
        } else if (clazz == StudyObjectCollection.class) {
            return (T)StudyObjectCollection.find(uri);
        } else if (clazz == StudyObject.class) {
            return (T)StudyObject.find(uri);
        } else if (clazz == StudyRole.class) {
            return (T)StudyRole.find(uri);
        } else if (clazz == VirtualColumn.class) {
            return (T)VirtualColumn.find(uri);
        } else if (clazz == Stream.class) {
            return (T)Stream.find(uri);
        } else if (clazz == Deployment.class) {
            return (T)Deployment.find(uri);
        } else if (clazz == Person.class) {
            return (T)Person.find(uri);
        } else if (clazz == Organization.class) {
            return (T)Organization.find(uri);
        } else if (clazz == Place.class) {
            return (T)Place.find(uri);
        } else if (clazz == PostalAddress.class) {
            return (T)PostalAddress.find(uri);
        } else if (clazz == KGR.class) {
            return (T)KGR.find(uri);
        }
        return null;
    
    }

    public static int findTotalByQuery(String queryString) {
        try {
            ResultSetRewindable resultsrw = SPARQLUtils.select(
                    CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_QUERY), queryString);

            if (resultsrw.hasNext()) {
                QuerySolution soln = resultsrw.next();
                return Integer.parseInt(soln.getLiteral("tot").getString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}

