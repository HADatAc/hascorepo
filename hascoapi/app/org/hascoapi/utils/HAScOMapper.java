package org.hascoapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.List;

import org.hascoapi.annotations.PropertyField;
import org.hascoapi.entity.pojo.*;
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.HASCO;
import org.hascoapi.vocabularies.SIO;
import org.hascoapi.vocabularies.SCHEMA;
import org.hascoapi.vocabularies.VSTOI;

public class HAScOMapper {

    public static final String FULL = "full";

    public static final String ESSENTIAL = "essential";

    /**
     *
     * This method requests a typeResult that is the main HAScO concept to be
     * serialized. According to this
     * main concept, this method will include filter for all the HAScO classes that
     * are currently filtered. In
     * general, a serializeAll filter will be added for the main concept, and a more
     * restricted filter that
     * includes just the core properties of each concept is serialized for the
     * non-main concept.
     *
     * @param typeResult
     * @param mode 'full' 'essential'
     * @return filtered Jackson's ObjectMapper
     */
    public static ObjectMapper getFiltered(String mode, String typeResult) {
        //System.out.println("HAScO.getFiltered() with mode [" + mode + "] and typeResult [" + typeResult +"]");
        ObjectMapper mapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();

        // ANNOTATION
        if (mode.equals(FULL) && typeResult.equals(VSTOI.ANNOTATION)) {
            filterProvider.addFilter("annotationFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("annotationFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "belongsTo", "container",
                            "hasAnnotationStem", "annotationStem", "hasPosition", "hasStyle"));
        }

        // ANNOTATION_STEM
        if (mode.equals(FULL) && typeResult.equals(VSTOI.ANNOTATION_STEM)) {
            filterProvider.addFilter("annotationStemFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("annotationStemFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasContent", "hasLanguage", "hasVersion",
                            "wasDerivedFrom", "wasGeneratedBy", "hasSIRManagerEmail"));
        }

        // ATTRIBUTE
        if (mode.equals(FULL) && typeResult.equals(SIO.ATTRIBUTE)) {
            filterProvider.addFilter("variableFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("variableFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "variableSpec"));
        }

        // CODEBOOK
        if (mode.equals(FULL) && typeResult.equals(VSTOI.CODEBOOK)) {
            filterProvider.addFilter("codebookFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("codebookFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasSerialNumber", "responseOptions", "hasLanguage",
                            "hasVersion", "hasSIRManagerEmail", "CodebookSlots"));
        }

        // CODEBOOK SLOT
        if (mode.equals(FULL) && typeResult.equals(VSTOI.CODEBOOK_SLOT)) {
            filterProvider.addFilter("CodebookSlotFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("CodebookSlotFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasPriority", "hasResponseOption", "responseOption"));
        }

        // CONTAINER
        if (mode.equals(FULL) && typeResult.equals(VSTOI.CONTAINER)) {
            filterProvider.addFilter("containerFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("containerFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasInformant", "comment", "belongsTo", "hasFirst", "hasSerialNumber", "hasLanguage", 
                            "hasVersion", "hasPriority", "hasNext", "hasPrevious", "hasSIRManagerEmail"));
        }

        // CONTAINER_SLOT
        if (mode.equals(FULL) && typeResult.equals(VSTOI.CONTAINER_SLOT)) {
            filterProvider.addFilter("containerSlotFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("containerSlotFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasNext", "hasPrevious", "hasPriority", "hasDetector", "hasSubcontainer", "detector", 
                            "subcontainer", "belongsTo"));
        }

        // DA
        if (mode.equals(FULL) && typeResult.equals(HASCO.DATA_ACQUISITION)) {
            filterProvider.addFilter("daFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("daFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion",  "isMemberOf", "hasDD", "hasSDD", "comment", 
                            "hasDataFileUri", "hasDataFile", "hasDD", "hasDDUri", "hasSDD", "hasSDDUri"));
        }

        // DATA FILE
        if (mode.equals(FULL) && typeResult.equals(HASCO.DATAFILE)) {
            filterProvider.addFilter("dataFileFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("dataFileFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "id", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "filename", "fileStatus", "lastProcessTime", "file"));
        }

        // DD
        if (mode.equals(FULL) && typeResult.equals(HASCO.DD)) {
            filterProvider.addFilter("ddFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("ddFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion",  "comment", "hasDataFileUri", "hasDataFile"));
        }

        // DEPLOYMENT
        if (mode.equals(FULL) && typeResult.equals(VSTOI.DEPLOYMENT)) {
            filterProvider.addFilter("deploymentFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("deploymentFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasVersion", "platformInstanceUri", "instrumentInstanceUri", "detectorinstanceUri",
                            "designedAt", "startedAt", "endedAt"));
        }

        // DP2
        if (mode.equals(FULL) && typeResult.equals(HASCO.DP2)) {
            filterProvider.addFilter("dp2Filter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("dp2Filter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion",  "comment", "hasDataFileUri", "hasDataFile"));
        }

        // DETECTOR
        if (mode.equals(FULL) && typeResult.equals(VSTOI.DETECTOR)) {
            filterProvider.addFilter("detectorFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("detectorFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "superUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasSerialNumber", "hasVersion",
                            "wasDerivedFrom", "wasGeneratedBy", "hasSIRManagerEmail", "hasDetectorStem", "detectorStem", "hasCodebook", "codebook"));
        }

        // DETECTOR_INSTANCE
        if (mode.equals(FULL) && typeResult.equals(VSTOI.DETECTOR_INSTANCE)) {
            filterProvider.addFilter("detectorInstanceFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("detectorInstanceFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasSerialNumber", "hasAcquisitionDate", "isDamaged", 
                            "hasDamageDate", "hasSIRManagerEmail"));
        }

        // DETECTOR_STEM
        if (mode.equals(FULL) && typeResult.equals(VSTOI.DETECTOR_STEM)) {
            filterProvider.addFilter("detectorStemFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("detectorStemFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "superUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasContent", "hasLanguage", "hasVersion",
                            "wasDerivedFrom", "wasGeneratedBy", "hasSIRManagerEmail", "detects", "detectsSemanticVariable"));
        }

        // DETECTOR_STEM_TYPE
        filterProvider.addFilter("detectorStemTypeFilter", 
            SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "className", "superUri", "superLabel", "comment"));

        // DSG
        if (mode.equals(FULL) && typeResult.equals(HASCO.DSG)) {
            filterProvider.addFilter("dsgFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("dsgFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasDataFileUri", "hasDataFile"));
        }

        // ENTITY
        if (mode.equals(FULL) && typeResult.equals(SIO.ENTITY)) {
            filterProvider.addFilter("entityFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("entityFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment"));
        }

        // HASCO_CLASS
        if (mode.equals(FULL) && typeResult.equals(HASCO.HASCO_CLASS)) {
            filterProvider.addFilter("hascoClassFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("hascoClassFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "superUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "nodeId", "comment", "isDomainOf", "isRangeOf", "isDisjointWith", "subClasses"));
        }

        // INS
        if (mode.equals(FULL) && typeResult.equals(HASCO.INS)) {
            filterProvider.addFilter("insFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("insFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion", "comment", "hasDataFileUri", "hasDataFile"));
        }

        // INSTRUMENT
        if (mode.equals(FULL) && typeResult.equals(VSTOI.INSTRUMENT)) {
            filterProvider.addFilter("instrumentFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("instrumentFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "superUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasInformant", "comment", "hasFirst", "hasSerialNumber", "hasLanguage", 
                            "hasVersion", "hasSIRManagerEmail"));
        }

        // INSTRUMENT_INSTANCE
        if (mode.equals(FULL) && typeResult.equals(VSTOI.INSTRUMENT_INSTANCE)) {
            filterProvider.addFilter("instrumentInstanceFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("instrumentInstanceFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasSerialNumber", "hasAcquisitionDate", "isDamaged", 
                            "hasDamageDate", "hasSIRManagerEmail"));
        }

        // INSTRUMENT_TYPE
        filterProvider.addFilter("instrumentTypeFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "className", "superUri", "superLabel", "comment"));

        // KGR
        if (mode.equals(FULL) && typeResult.equals(HASCO.KNOWLEDGE_GRAPH)) {
            filterProvider.addFilter("kgrFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("kgrFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasDataFile", "dataFile"));
        }

        // ORGANIZATION
        if (mode.equals(FULL) && typeResult.equals(FOAF.ORGANIZATION)) {
            filterProvider.addFilter("organizationFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("organizationFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "name", "mbox", "telephone", "url", "parentOrganizationUri", "childrenOrganizations"));
        }

        // PERSON
        if (mode.equals(FULL) && typeResult.equals(FOAF.PERSON)) {
            filterProvider.addFilter("personFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("personFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "name", "mbox", "telephone", "member", "givenName", "familyName", 
                            "hasAffiliation", "hasUrl", "jobTitle"));
        }

        // PLACE
        if (mode.equals(FULL) && typeResult.equals(SCHEMA.PLACE)) {
            filterProvider.addFilter("placeFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("placeFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "name", "hasAddress", "containedInPlace", "hasIdentifier", 
                            "hasGeo", "hasLatitude", "hasLongitude", "hasUrl"));
        }
 
        // PLATFORM
        if (mode.equals(FULL) && typeResult.equals(VSTOI.PLATFORM)) {
            filterProvider.addFilter("platformFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("platformFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasVersion"));
        }

        // PLATFORM_INSTANCE
        if (mode.equals(FULL) && typeResult.equals(VSTOI.PLATFORM_INSTANCE)) {
            filterProvider.addFilter("platformInstanceFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("platformInstanceFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasSerialNumber", "hasAcquisitionDate", "isDamaged", 
                            "hasDamageDate", "hasSIRManagerEmail"));
        }

        // POSSIBLE_VALUE
        if (mode.equals(FULL) && typeResult.equals(HASCO.POSSIBLE_VALUE)) {
            filterProvider.addFilter("possibleValueFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("possibleValueFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "superUri", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "partOfSchema", "listPosition", "isPossibleValueOf", 
                            "hasCode", "hasCodeLabel", "hasClass", "hasSIRManagementEmail"));
        }

        // POSTAL_ADDRESS
        if (mode.equals(FULL) && typeResult.equals(SCHEMA.POSTAL_ADDRESS)) {
            filterProvider.addFilter("postalAddressFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("postalAddressFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "name", "hasStreetAddress", "hasPostalCode", "hasAddressLocalityUri",
                            "hasAddressRegionUri", "hasAddressCountryUri", "hasAddressLocality", "hasAddressRegion", "hasAddressCountry"));
        }
 
        // RESPONSE OPTION
        if (mode.equals(FULL) && typeResult.equals(VSTOI.RESPONSE_OPTION)) {
            filterProvider.addFilter("responseOptionFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("responseOptionFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "hasContent", "hasSerialNumber", "hasLanguage", "hasVersion",
                            "hasSIRManagerEmail"));
        }

        // SDD
        if (mode.equals(FULL) && typeResult.equals(HASCO.SDD)) {
            filterProvider.addFilter("sddFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("sddFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion",  "comment", "hasDataFileUri", "hasDataFile"));
        }

        // SDD_ATTRIBUTE
        if (mode.equals(FULL) && typeResult.equals(HASCO.SDD_ATTRIBUTE)) {
            filterProvider.addFilter("sddAttributeFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("sddAttributeFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "partOfSchema", "listPosition", "attribute", "objectUri", 
                            "unit", "eventUri", "inRelationTo", "wasDerivedFrom", "hasSIRManagerEmail"));
        }

        // SDD_OBJECT
        if (mode.equals(FULL) && typeResult.equals(HASCO.SDD_OBJECT)) {
            filterProvider.addFilter("sddObjectFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("sddObjectFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "partOfSchema", "listPosition", "entity", "role", "relation", 
                            "inRelationTo", "wasDerivedFrom", "hasSIRManagementEmail"));
        }

        // SEMANTIC_DATA_DICTIONARY
        if (mode.equals(FULL) && typeResult.equals(HASCO.SEMANTIC_DATA_DICTIONARY)) {
            filterProvider.addFilter("semanticDataDictionaryFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("semanticDataDictionaryFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion",  "comment"));
        }

        // SEMANTIC_VARIABLE
        if (mode.equals(FULL) && typeResult.equals(HASCO.SEMANTIC_VARIABLE)) {
            filterProvider.addFilter("semanticVariableFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("semanticVariableFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "entityUri", "attributeUri", "inRelationToUri", "unitUri", "timeUri"));
        }

        // STR
        if (mode.equals(FULL) && typeResult.equals(HASCO.STR)) {
            filterProvider.addFilter("strFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("strFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion", "comment", "hasDataFileUri", "hasDataFile"));
        }

        // STREAM
        if (mode.equals(FULL) && typeResult.equals(HASCO.STREAM)) {
            filterProvider.addFilter("streamFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("streamFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasVersion",  "comment", "hasDeployment", "hasStudy", "hasSDD",
                            "designedAt", "startedAt", "endedAt", "method", "messageProtocol", "messageIP", "messagePort"));
        }

        // STUDY
        if (mode.equals(FULL) && typeResult.equals(HASCO.STUDY)) {
            filterProvider.addFilter("studyFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("studyFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment"));
        }

        // STUDY OBJECT
        if (mode.equals(FULL) && typeResult.equals(HASCO.STUDY_OBJECT)) {
            filterProvider.addFilter("studyObjectFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("studyObjectFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "isMemberOf"));
        }

        // STUDY OBJECT COLLECTION
        //if (mode.equals(FULL) && typeResult.equals(HASCO.OBJECT_COLLECTION)) {
        //    filterProvider.addFilter("studyObjectFilter", SimpleBeanPropertyFilter.serializeAllExcept("measurements"));
        //} else if (typeResult.equals(HASCO.STUDY_OBJECT)) {
        //    filterProvider.addFilter("studyObjectFilter", SimpleBeanPropertyFilter.serializeAll());
        //} else {
        //    filterProvider.addFilter("studyObjectFilter",
        //            SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
        //                    "hascoTypeLabel", "comment"));
        //}

        // STUDY OBJECT COLLECTION
        if (mode.equals(FULL) && typeResult.equals(HASCO.STUDY_OBJECT_COLLECTION)) {
            filterProvider.addFilter("studyObjectCollectionFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("studyObjectCollectionFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "isMemberOfUri", "isMemberOf"));
        }

        // STUDY ROLE
        if (mode.equals(FULL) && typeResult.equals(HASCO.STUDY_ROLE)) {
            filterProvider.addFilter("studyRoleFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("studyRoleFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "isMemberOf"));
        }

        // SUBCONTAINER
        if (mode.equals(FULL) && typeResult.equals(VSTOI.SUBCONTAINER)) {
            filterProvider.addFilter("subcontainerFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("subcontainerFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "superUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "hasInformant", "comment", "belongsTo", "hasFirst", "hasNext", "hasPrevious", 
                            "hasPriority", "hasSerialNumber", "hasLanguage", "hasVersion", "hasSIRManagerEmail"));
        }

        // VALUE
        if (mode.equals(FULL) && typeResult.equals(HASCO.VALUE)) {
            filterProvider.addFilter("valueFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("valueFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "studyObjectUri", "variable"));
        }

        // VIRTUAL COLUMN
        if (mode.equals(FULL) && typeResult.equals(HASCO.VIRTUAL_COLUMN)) {
            filterProvider.addFilter("virtualColumnFilter", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filterProvider.addFilter("virtualColumnFilter",
                    SimpleBeanPropertyFilter.filterOutAllExcept("uri", "label", "typeUri", "typeLabel", "hascoTypeUri",
                            "hascoTypeLabel", "comment", "socreference", "groundingLabel", "isMemberOf", "isMemberOfUri"));
        }

        mapper.setFilterProvider(filterProvider);

        return mapper;
    }

    public static ObjectMapper getFilteredByClass(String mode, Class clazz) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();

        if (clazz == Annotation.class) {
            return getFiltered(mode, VSTOI.ANNOTATION);
        } if (clazz == AnnotationStem.class) {
            return getFiltered(mode, VSTOI.ANNOTATION_STEM);
        } if (clazz == Attribute.class) {   // CANNOT FIND IT ABOVE
            return getFiltered(mode, SIO.ATTRIBUTE);
        } else if (clazz == Codebook.class) {
            return getFiltered(mode, VSTOI.CODEBOOK);
        } else if (clazz == CodebookSlot.class) {
            return getFiltered(mode, VSTOI.CODEBOOK_SLOT);
        } else if (clazz == Container.class) {
            return getFiltered(mode, VSTOI.CONTAINER);
        } else if (clazz == ContainerSlot.class) {
            return getFiltered(mode, VSTOI.CONTAINER_SLOT);
        } else if (clazz == DA.class) {
            return getFiltered(mode, HASCO.DATA_ACQUISITION);
        } else if (clazz == DataFile.class) {
            return getFiltered(mode, HASCO.DATAFILE);
        } else if (clazz == DD.class) {
            return getFiltered(mode, HASCO.DD);
        } else if (clazz == Detector.class) {
            return getFiltered(mode, VSTOI.DETECTOR);
        } else if (clazz == DetectorStem.class) {
            return getFiltered(mode, VSTOI.DETECTOR_STEM);
        } else if (clazz == DetectorStemType.class) {
            return getFiltered(mode, VSTOI.DETECTOR_STEM);  // DETECTOR_STEM_TYPE == DETECTOR_STEM
        } else if (clazz == Deployment.class) {
            return getFiltered(mode, VSTOI.DEPLOYMENT);
        } else if (clazz == DP2.class) {
            return getFiltered(mode, HASCO.DP2);
        } else if (clazz == Entity.class) {
            return getFiltered(mode, SIO.ENTITY);
        } else if (clazz == INS.class) {
            return getFiltered(mode, HASCO.INS);
        } else if (clazz == Instrument.class) {
            return getFiltered(mode, VSTOI.INSTRUMENT);
        } else if (clazz == InstrumentType.class) {
            return getFiltered(mode, VSTOI.INSTRUMENT);  // INSTRUMENT_TYPE == INSTRUMENT
        } else if (clazz == KGR.class) {
            return getFiltered(mode, HASCO.KNOWLEDGE_GRAPH);
        } else if (clazz == Organization.class) {
            return getFiltered(mode, FOAF.ORGANIZATION);
        } else if (clazz == Person.class) {
            return getFiltered(mode, FOAF.PERSON);
        } else if (clazz == Place.class) {
            return getFiltered(mode, SCHEMA.PLACE);
        } else if (clazz == Platform.class) {
            return getFiltered(mode, VSTOI.PLATFORM);
        } else if (clazz == PostalAddress.class) {
            return getFiltered(mode, SCHEMA.POSTAL_ADDRESS);
        } else if (clazz == ResponseOption.class) {
            return getFiltered(mode, VSTOI.RESPONSE_OPTION);
        } else if (clazz == SDD.class) {
            return getFiltered(mode, HASCO.SDD);
        } else if (clazz == SemanticVariable.class) {
            return getFiltered(mode, HASCO.SEMANTIC_VARIABLE);
        } else if (clazz == STR.class) {
            return getFiltered(mode, HASCO.STR);
        } else if (clazz == Study.class) {
            return getFiltered(mode, HASCO.STUDY);
        } else if (clazz == StudyObject.class) {
            return getFiltered(mode, HASCO.STUDY_OBJECT);
        } else if (clazz == StudyObjectCollection.class) {
            return getFiltered(mode, HASCO.STUDY_OBJECT_COLLECTION);
        } else if (clazz == StudyRole.class) {
            return getFiltered(mode, HASCO.STUDY_ROLE);
        } else if (clazz == Subcontainer.class) {
            return getFiltered(mode, VSTOI.SUBCONTAINER);
        //} else if (clazz == Value.class) {
        //    return getFiltered(mode, VSTOI.VALUE);
        } else if (clazz == VirtualColumn.class) {
            return getFiltered(mode, HASCO.VIRTUAL_COLUMN);
        } 
        return getFiltered(mode, "NONE");
    }

}
