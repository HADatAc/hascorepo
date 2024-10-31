package test;

public class Responses {

    /** 
     *   RESET IS WHEN WE DELETE EVERYTHING FIRST REGARDLESS WHETHER THINGS EXIST OR NOT, AND THEN TRY TO RE-DELETE EACH ONE OF THEM  
     */
    public static final String RESPONSE_NOK_RESET_INSTRUMENT                
        = "{\"isSuccessful\":false,\"body\":\"There is no Test instrument to be deleted.\"}";
    public static final String RESPONSE_NOK_RESET_DETECTOR_STEMS            
        = "{\"isSuccessful\":false,\"body\":\"There is no Test Detector Stem 1 to be deleted.\"}";
    public static final String RESPONSE_NOK_RESET_DETECTORS                 
        = "{\"isSuccessful\":false,\"body\":\"There is no Test Detector 1 to be deleted.\"}";
    public static final String RESPONSE_NOK_RESET_CONTAINER_SLOTS            
        = "{\"isSuccessful\":false,\"body\":\"Test instrument <http://hadatac.org/kb/test/TestInstrument> needs to exist before its containerSlots can be deleted.\"}";
    public static final String RESPONSE_NOK_RESET_CODEBOOK                  
        = "{\"isSuccessful\":false,\"body\":\"There is no Test Codebook to be deleted.\"}";
    public static final String RESPONSE_NOK_RESET_RESPONSE_OPTIONS          
        = "{\"isSuccessful\":false,\"body\":\"There is no Test ResponseOption 1 to be deleted.\"}";
    public static final String RESPONSE_NOK_RESET_CODEBOOK_SLOTS     
        = "{\"isSuccessful\":false,\"body\":\"Test codebook <http://hadatac.org/kb/test/TestCodebook> needs to exist before its codebook slots can be deleted.\"}";
    
    /** 
     *   DELETE IS WHEN THE THING TO BE DELETED EXISTS AND IT IS ACTUALLY DELETED. TO BE EXECUTED AFTER IT HAS BEEN CREATED
     */
    public static final String RESPONSE_OK_CREATE_INSTRUMENT  
        = "{\"isSuccessful\":true,\"body\":\"Instrument <http://hadatac.org/kb/test/TestInstrument> has been CREATED.\"}";
    public static final String RESPONSE_OK_CREATE_DETECTOR_STEMS  
        = "{\"isSuccessful\":true,\"body\":\"Test Detector Stems 1 and 2 have been CREATED.\"}";
    public static final String RESPONSE_OK_CREATE_DETECTOr_SLOTS 
        = "{\"isSuccessful\":true,\"body\":\"A total of 2 containerSlots have been created for instrument <http://hadatac.org/kb/test/TestInstrument>.\"}";
    public static final String RESPONSE_OK_CREATE_DETECTORS 
        = "{\"isSuccessful\":true,\"body\":\"Test Detectors 1 and 2 have been CREATED.\"}";


    /** 
     *   CREATE IS WHEN THE THING TO BE CREATE DOES NOT EXISTS AND IT IS ACTUALLY CREATED. TO BE EXECUTED AFTER IT HAS BENN RESET
     */
    public static final String RESPONSE_OK_DELETE_INSTRUMENT  
        = "{\"isSuccessful\":true,\"body\":\"Instrument <http://hadatac.org/kb/test/TestInstrument> has been DELETED.\"}";
    public static final String RESPONSE_OK_DELETE_DETECTOR_STEMS  
        = "{\"isSuccessful\":true,\"body\":\"Test Detector Stems 1 and 2 have been DELETED.\"}";
    public static final String RESPONSE_OK_DELETE_CONTAINER_SLOTS 
        = "{\"isSuccessful\":true,\"body\":\"ContainerSlots for Instrument <http://hadatac.org/kb/test/TestInstrument> have been deleted.\"}";
    public static final String RESPONSE_OK_DELETE_DETECTORS  
        = "{\"isSuccessful\":true,\"body\":\"Test Detectors 1 and 2 have been DELETED.\"}";

}
