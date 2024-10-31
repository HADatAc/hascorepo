package org.hascoapi.vocabularies;

import org.hascoapi.annotations.PropertyField;

import java.util.HashMap;
import java.util.Map;

public class VSTOI {

    public static final String VSTOI = "http://hadatac.org/ont/vstoi#";

    /*
     * CLASSES
     */

    public static final String CONTAINER = VSTOI + "Container";
    public static final String DEPLOYMENT = VSTOI + "Deployment";
    public static final String DETECTOR = VSTOI + "Detector";
    public static final String INSTRUMENT = VSTOI + "Instrument";
    public static final String PHYSICAL_INSTRUMENT = VSTOI + "PhysicalInstrument";
    public static final String PLATFORM = VSTOI + "Platform";
    public static final String QUESTIONNAIRE = VSTOI + "Questionnaire";
    public static final String SIMULATION_MODEL = VSTOI + "SimulationModel";
    public static final String SUBCONTAINER = VSTOI + "Subcontainer";

    /*
     * PROPERTIES
     */

    public static final String IS_INSTRUMENT_ATTACHMENT = VSTOI + "isInstrumentContainerSlot";
    public static final String HAS_PLATFORM_INSTANCE = VSTOI + "hasPlatformInstance";
    public static final String HAS_SERIAL_NUMBER = VSTOI + "hasSerialNumber";
    public static final String HAS_WEB_DOCUMENTATION = VSTOI + "hasWebDocumentation";

    /**************************************************************************************************
     * NEW ADDITIONS TO VSTOI IN SUPPORT OF QUESTIONNAIRES
     **************************************************************************************************/

    /*
     * CLASSES
     */

    public static final String ANNOTATION_STEM = "http://hadatac.org/ont/vstoi#AnnotationStem";
    public static final String ANNOTATION = "http://hadatac.org/ont/vstoi#Annotation";
    public static final String CODEBOOK = "http://hadatac.org/ont/vstoi#Codebook";
    public static final String DETECTOR_STEM = "http://hadatac.org/ont/vstoi#DetectorStem";
    public static final String DETECTOR_INSTANCE = "http://hadatac.org/ont/vstoi#DetectorInstance";
    public static final String CONTAINER_SLOT = "http://hadatac.org/ont/vstoi#ContainerSlot";
    public static final String INFORMANT = "http://hadatac.org/ont/vstoi#Informant";
    public static final String ITEM = "http://hadatac.org/ont/vstoi#Item";
    public static final String INSTRUMENT_INSTANCE = "http://hadatac.org/ont/vstoi#InstrumentInstance";
    public static final String PLATFORM_INSTANCE = "http://hadatac.org/ont/vstoi#PlatformInstance";
    public static final String PSYCHOMETRIC_QUESTIONNAIRE = "http://hadatac.org/ont/vstoi#PsychometricQuestionnaire";
    public static final String RESPONSE_OPTION = "http://hadatac.org/ont/vstoi#ResponseOption";
    public static final String CODEBOOK_SLOT = "http://hadatac.org/ont/vstoi#CodebookSlot";
    public static final String TABLE = "http://hadatac.org/ont/vstoi#Table";
    public static final String VSTOI_INSTANCE = "http://hadatac.org/ont/vstoi#VSTOIInstance";

    /*
     * PROPERTIES
     */

    public static final String BELONGS_TO = "http://hadatac.org/ont/vstoi#belongsTo";
    public static final String DESIGNED_AT_TIME = "http://hadatac.org/ont/vstoi#designedAtTime";
    public static final String HAS_ACQUISITION_DATE = "http://hadatac.org/ont/vstoi#hasAcquisitionDate";    
    public static final String HAS_ANNOTATION_STEM = "http://hadatac.org/ont/vstoi#hasAnnotationStem";
    public static final String HAS_CODEBOOK = "http://hadatac.org/ont/vstoi#hasCodebook";
    public static final String HAS_CONTENT = "http://hadatac.org/ont/vstoi#hasContent";
    public static final String HAS_CONTENT_WITH_STYLE = "http://hadatac.org/ont/vstoi#hasContentWithStyle";
    public static final String HAS_DAMAGE_DATE = "http://hadatac.org/ont/vstoi#hasDamageDate";
    public static final String HAS_DETECTOR = "http://hadatac.org/ont/vstoi#hasDetector";
    public static final String HAS_DETECTOR_INSTANCE = "http://hadatac.org/ont/vstoi#hasDetectorInstance";
    public static final String HAS_DETECTOR_STEM = "http://hadatac.org/ont/vstoi#hasDetectorStem";
    public static final String HAS_FIRST = "http://hadatac.org/ont/vstoi#hasFirst";
    public static final String HAS_INFORMANT = "http://hadatac.org/ont/vstoi#hasInformant";
    public static final String HAS_INSTRUMENT_INSTANCE = "http://hadatac.org/ont/vstoi#hasInstrumentInstance";
    public static final String HAS_LANGUAGE = "http://hadatac.org/ont/vstoi#hasLanguage";
    public static final String HAS_NEXT = "http://hadatac.org/ont/vstoi#hasNext";
    public static final String HAS_PREVIOUS = "http://hadatac.org/ont/vstoi#hasPrevious";
    public static final String HAS_POSITION = "http://hadatac.org/ont/vstoi#hasPosition";
    public static final String HAS_PRIORITY = "http://hadatac.org/ont/vstoi#hasPriority";
    public static final String HAS_RESPONSE_OPTION = "http://hadatac.org/ont/vstoi#hasResponseOption";
    public static final String HAS_SHORT_NAME = "http://hadatac.org/ont/vstoi#hasShortName";
    public static final String HAS_STATUS = "http://hadatac.org/ont/vstoi#hasStatus";
    public static final String HAS_SIR_MANAGER_EMAIL = "http://hadatac.org/ont/vstoi#hasSIRManagerEmail";
    public static final String HAS_SUBCONTAINER = "http://hadatac.org/ont/vstoi#hasSubcontainer";
    public static final String HAS_VERSION = "http://hadatac.org/ont/vstoi#hasVersion";
    public static final String HOST_TYPE = "http://hadatac.org/ont/vstoi#hostType";
    public static final String IS_DAMAGED = "http://hadatac.org/ont/vstoi#isDamaged";
    public static final String OF_CODEBOOK = "http://hadatac.org/ont/vstoi#ofCodebook";

    /*
     * POSITIONS
     */

    public static final String NOT_VISIBLE            = "http://hadatac.org/ont/vstoi#NotVisible";
    public static final String TOP_LEFT               = "http://hadatac.org/ont/vstoi#TopLeft";
    public static final String TOP_CENTER             = "http://hadatac.org/ont/vstoi#TopCenter";
    public static final String TOP_RIGHT              = "http://hadatac.org/ont/vstoi#TopRight";
    public static final String LINE_BELOW_TOP         = "http://hadatac.org/ont/vstoi#LineBelowTop";
    public static final String BOTTOM_LEFT            = "http://hadatac.org/ont/vstoi#BotomLeft";
    public static final String BOTTOM_CENTER          = "http://hadatac.org/ont/vstoi#BottomCenter";
    public static final String BOTTOM_RIGHT           = "http://hadatac.org/ont/vstoi#BottomRight";
    public static final String LINE_ABOVE_BOTTOM      = "http://hadatac.org/ont/vstoi#LineAboveBottom";
    public static final String PAGE_TOP_LEFT          = "http://hadatac.org/ont/vstoi#PageTopLeft";
    public static final String PAGE_TOP_CENTER        = "http://hadatac.org/ont/vstoi#PageTopCenter";
    public static final String PAGE_TOP_RIGHT         = "http://hadatac.org/ont/vstoi#PageTopRight";
    public static final String PAGE_LINE_BELOW_TOP    = "http://hadatac.org/ont/vstoi#PageLineBelowTop";
    public static final String PAGE_BOTTOM_LEFT       = "http://hadatac.org/ont/vstoi#PageBottomLeft";
    public static final String PAGE_BOTTOM_CENTER     = "http://hadatac.org/ont/vstoi#PageBottomCenter";
    public static final String PAGE_BOTTOM_RIGHT      = "http://hadatac.org/ont/vstoi#PageBottomRight";
    public static final String PAGE_LINE_ABOVE_BOTTOM = "http://hadatac.org/ont/vstoi#PageLineAboveBottom";

    /**
     * INSTANCES
     */

    public static final String DEFAULT_LANGUAGE = "en";

    public static final String DEFAULT_WAS_GENERATED_BY = "http://hadatac.org/ont/vstoi#Original";
    public static Map<String, String> wasGeneratedBy;
    static {
        wasGeneratedBy = new HashMap<>();
        wasGeneratedBy.put(DEFAULT_WAS_GENERATED_BY, "Original");
        wasGeneratedBy.put("http://hadatac.org/ont/vstoi#Translation", "Translation");
        wasGeneratedBy.put("http://hadatac.org/ont/vstoi#Generalization", "Generalization");
        wasGeneratedBy.put("http://hadatac.org/ont/vstoi#Specialization", "Specialization");
    }

    public static final String DEFAULT_INFORMANT = "http://hadatac.org/ont/vstoi#Self";
    public static Map<String, String> informant;
    static {
        informant = new HashMap<>();
        informant.put(DEFAULT_INFORMANT, "Self");
        informant.put("http://hadatac.org/ont/vstoi#Youth", "Youth");
        informant.put("http://hadatac.org/ont/vstoi#Caregiver", "Caregiver");
        informant.put("http://hadatac.org/ont/vstoi#HouseholdReferencePerson", "HouseholdReferencePerson");
        informant.put("http://hadatac.org/ont/vstoi#PhysicalWorld", "PhysicalWorld");
    }

    public static final String DEFAULT_CONTAINER_POSITION = TOP_CENTER;
    public static Map<String, String> containerPosition;
    static {
        containerPosition = new HashMap<>();
        containerPosition.put(TOP_CENTER, "TopCenter");
        containerPosition.put(TOP_LEFT, "TopLeft");
        containerPosition.put(TOP_RIGHT, "TopRight");
        containerPosition.put(LINE_BELOW_TOP, "LineBelowTop");
        containerPosition.put(BOTTOM_CENTER, "BottomCenter");
        containerPosition.put(BOTTOM_LEFT, "BottomLeft");
        containerPosition.put(BOTTOM_RIGHT, "BottomRight");
        containerPosition.put(LINE_ABOVE_BOTTOM, "LineAboveBottom");
        containerPosition.put(NOT_VISIBLE, "NotVisible");
    }

    public static final String DEFAULT_INSTRUMENT_TYPE = INSTRUMENT;
    public static Map<String, String> instrumentType;
    static {
        instrumentType = new HashMap<>();
        instrumentType.put(QUESTIONNAIRE, "Questionnaire");
        instrumentType.put(PHYSICAL_INSTRUMENT, "PhysicalInstrument");
        instrumentType.put(SIMULATION_MODEL, "SimulationModel");
    }

    public static final String DEFAULT_PAGE_POSITION = "http://hadatac.org/ont/vstoi#PageTopCenter";
    public static Map<String, String> pagePosition;
    static {
        pagePosition = new HashMap<>();
        pagePosition.put(PAGE_TOP_CENTER, "PageTopCenter");
        pagePosition.put(PAGE_TOP_LEFT, "PageTopLeft");
        pagePosition.put(PAGE_TOP_RIGHT, "PageTopRight");
        pagePosition.put(PAGE_LINE_BELOW_TOP, "PageLineBelowTop");
        pagePosition.put(PAGE_BOTTOM_CENTER, "PageBottomCenter");
        pagePosition.put(PAGE_BOTTOM_LEFT, "PageBottomLeft");
        pagePosition.put(PAGE_BOTTOM_RIGHT, "PageBottomRight");
        pagePosition.put(PAGE_LINE_ABOVE_BOTTOM, "PageLineAboveBottom");
        pagePosition.put(NOT_VISIBLE, "NotVisible");
    }

}
