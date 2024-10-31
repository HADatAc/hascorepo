package org.hascoapi;

public class Constants {

    public static final String REPOSITORY_VERSION = "0.6";

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";

    public static final String CONTAINER_SLOT_PREFIX = "CTS";
    public static final String CODEBOOK_SLOT_PREFIX = "CBS";
    public static final String TEST_KB = "http://hadatac.org/kb/test/";

    public static final String TEST_INSTRUMENT_URI = TEST_KB + "TestInstrument";
    public static final int    TEST_INSTRUMENT_TOT_CONTAINER_SLOTS = 2;
    public static final String TEST_SUBCONTAINER1_URI = TEST_KB + "TestSubcontainer1";
    public static final String TEST_SUBCONTAINER2_URI = TEST_KB + "TestSubcontainer2";
    public static final int    TEST_SUBCONTAINER_TOT_CONTAINER_SLOTS = 2;
    public static final String TEST_CONTAINER_SLOT1_URI = TEST_INSTRUMENT_URI + "/" + CONTAINER_SLOT_PREFIX + "/0001";
    public static final String TEST_CONTAINER_SLOT2_URI = TEST_INSTRUMENT_URI + "/" + CONTAINER_SLOT_PREFIX + "/0002";
    public static final String TEST_CONTAINER_SLOT3_URI = TEST_SUBCONTAINER1_URI + "/" + CONTAINER_SLOT_PREFIX + "/0001";
    public static final String TEST_CONTAINER_SLOT4_URI = TEST_SUBCONTAINER1_URI + "/" + CONTAINER_SLOT_PREFIX + "/0002";
    public static final String TEST_DETECTOR_STEM1_URI = TEST_KB + "TestDetectorStem1";
    public static final String TEST_DETECTOR_STEM2_URI = TEST_KB + "TestDetectorStem2";
    public static final String TEST_DETECTOR1_URI = TEST_KB + "TestDetector1";  // For Instrument with Stem 1
    public static final String TEST_DETECTOR2_URI = TEST_KB + "TestDetector2";  // For Instrument with Stem 2
    public static final String TEST_DETECTOR3_URI = TEST_KB + "TestDetector3";  // For Subcontainer with Stem 1
    public static final String TEST_DETECTOR4_URI = TEST_KB + "TestDetector4";  // For Subcontainer with Stem 2
    public static final String TEST_CODEBOOK_URI = TEST_KB + "TestCodebook";
    public static final String TEST_CODEBOOK_TOT_CODEBOOK_SLOTS = "2";
    public static final String TEST_CODEBOOK_SLOT1_URI = TEST_CODEBOOK_URI + "/" + CODEBOOK_SLOT_PREFIX + "/0001";
    public static final String TEST_CODEBOOK_SLOT2_URI = TEST_CODEBOOK_URI + "/" + CODEBOOK_SLOT_PREFIX + "/0002";
    public static final String TEST_RESPONSE_OPTION1_URI = TEST_KB + "TestResponseOption1";
    public static final String TEST_RESPONSE_OPTION2_URI = TEST_KB + "TestResponseOption2";
    public static final String TEST_ANNOTATION_STEM1_URI = TEST_KB + "TestAnnotationStem1";
    public static final String TEST_ANNOTATION_STEM2_URI = TEST_KB + "TestAnnotationStem2";
    public static final String TEST_ANNOTATION_STEM_INSTRUCTION_URI = TEST_KB + "TestAnnotationStemInstruction";
    public static final String TEST_ANNOTATION_STEM_PAGE_URI = TEST_KB + "TestAnnotationStemPage";
    public static final String TEST_ANNOTATION_STEM_DATEFIELD_URI = TEST_KB + "TestAnnotationStemDateField";
    public static final String TEST_ANNOTATION_STEM_COPYRIGHT_URI = TEST_KB + "TestAnnotationStemCopyright";
    public static final String TEST_ANNOTATION1_URI = TEST_KB + "TestAnnotation1";
    public static final String TEST_ANNOTATION2_URI = TEST_KB + "TestAnnotation2";
    public static final String TEST_ANNOTATION_INSTRUCTION_URI = TEST_KB + "TestAnnotationInstruction";
    public static final String TEST_ANNOTATION_PAGE_URI = TEST_KB + "TestAnnotationPage";
    public static final String TEST_ANNOTATION_DATEFIELD_URI = TEST_KB + "TestAnnotationDateField";
    public static final String TEST_ANNOTATION_COPYRIGHT_URI = TEST_KB + "TestAnnotationCopyright";
    public static final String TEST_SEMANTIC_VARIABLE1_URI = TEST_KB + "TestSemanticVariable1";
    public static final String TEST_SEMANTIC_VARIABLE2_URI = TEST_KB + "TestSemanticVariable2";
    public static final String TEST_ENTITY_URI = TEST_KB + "TestEntity";
    public static final String TEST_ATTRIBUTE1_URI = TEST_KB + "TestAttribute1";
    public static final String TEST_ATTRIBUTE2_URI = TEST_KB + "TestAttribute2";
    public static final String TEST_UNIT_URI = TEST_KB + "TestUnit";

    public static final String PREFIX_ANNOTATION                 = "AN";
    public static final String PREFIX_ANNOTATION_STEM            = "AS";
    public static final String PREFIX_CODEBOOK                   = "CB";
    public static final String PREFIX_DATA_ACQUISITION           = "DA";
    public static final String PREFIX_DATAFILE                   = "DF";
    public static final String PREFIX_DD                         = "DD";
    public static final String PREFIX_DEPLOYMENT                 = "DP";
    public static final String PREFIX_DESIGN                     = "DG";
    public static final String PREFIX_DETECTOR_STEM              = "DS";
    public static final String PREFIX_DETECTOR                   = "DT";
    public static final String PREFIX_DETECTOR_INSTANCE          = "DTI";
    public static final String PREFIX_DP2                        = "D2";
    public static final String PREFIX_DSG                        = "DG";
    public static final String PREFIX_INS                        = "IS";
    public static final String PREFIX_INSTRUMENT                 = "IN";
    public static final String PREFIX_INSTRUMENT_INSTANCE        = "INI";
    public static final String PREFIX_ORGANIZATION               = "OR";
    public static final String PREFIX_PERSON                     = "PS";
    public static final String PREFIX_PLACE                      = "PL";
    public static final String PREFIX_PLATFORM                   = "PF";
    public static final String PREFIX_PLATFORM_INSTANCE          = "PFI";
    public static final String PREFIX_POSTAL_ADDRESS             = "PA";
    public static final String PREFIX_RESPONSE_OPTION            = "RO";
    public static final String PREFIX_SEMANTIC_DATA_DICTIONARY   = "SY";
    public static final String PREFIX_SEMANTIC_VARIABLE          = "SV";
    public static final String PREFIX_SDD                        = "SD";
    public static final String PREFIX_STR                        = "SR";
    public static final String PREFIX_STREAM                     = "SM";
    public static final String PREFIX_STUDY                      = "ST";
    public static final String PREFIX_STUDY_OBJECT_COLLECTION    = "OC";
    public static final String PREFIX_STUDY_OBJECT               = "OB";
    public static final String PREFIX_STUDY_ROLE                 = "RL";
    public static final String PREFIX_SUBCONTAINER               = "SC";
    public static final String PREFIX_VIRTUAL_COLUMN             = "VC";
  
    public static final String DEFAULT_KB = "http://hadatac.org/kb/default/";
    public static final String DEFAULT_REPOSITORY = DEFAULT_KB + "repository";
    /**
     * Any submissions without a valid graph uri will be stored under this graph.
     *
     * We aren't posting to the standard default graph
     * because it isn't included in default queries with fuseki's tdb2 unionDefaultGraph setting
     */
    //public static final String DEFAULT_GRAPH_URI = "http://hadatac.org/ont/graph#DEFAULT";

    public static final String META_VARIABLE_CONTENT = "%%CONTENT%%";
    public static final String META_VARIABLE_PAGE = "%%PAGE%%";

}
