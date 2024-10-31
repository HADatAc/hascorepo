package org.hascoapi.utils;

import org.apache.commons.configuration2.INIConfiguration;
import java.util.Set;

public class Templates {

    public String templateFile = null;
    public INIConfiguration iniConfig = null;
    
    public Templates(String templateFile) {
        try {
            this.templateFile = templateFile;
            this.iniConfig = new HASCOConfig(templateFile); 

            // Print all section names
            //Set<String> sections = this.iniConfig.getSections();
            //System.out.println("Sections:");
            //for (String section: sections) {
            //    System.out.println(section);
            //}
        } catch (Exception e) {
            System.out.println("[ERROR] Templates.java: could not process templateFile. This is often cause bu obsolete template files.");
            e.printStackTrace();
        }
    }

    // STD Template (Study)
    public String getSTUDYID() {
        return iniConfig.getSection("STD").getString("studyID");  // also in ACQ, PID and SID
    }
    public String getSTUDYTITLE() { 
        return iniConfig.getSection("STD").getString("studyTitle"); 
    }
    public String getSTUDYAIMS() { 
        return iniConfig.getSection("STD").getString("studyAims");
    }
    public String getSTUDYSIGNIFICANCE() { 
        return iniConfig.getSection("STD").getString("studySignificance"); 
    }
    public String getNUMSUBJECTS() { 
        return iniConfig.getSection("STD").getString("numSubjects"); 
    }
    public String getNUMSAMPLES() { 
        return iniConfig.getSection("STD").getString("numSamples"); 
    }
    public String getINSTITUTION() { 
        return iniConfig.getSection("STD").getString("institution"); }
    public String getPI() { 
        return iniConfig.getSection("STD").getString("PI"); }
    public String getPIADDRESS() { 
        return iniConfig.getSection("STD").getString("PIAddress"); }
    public String getPICITY() { 
        return iniConfig.getSection("STD").getString("PICity"); }
    public String getPISTATE() { 
        return iniConfig.getSection("STD").getString("PIState"); }
    public String getPIZIPCODE() { 
        return iniConfig.getSection("STD").getString("PIZipCode"); }
    public String getPIEMAIL() { 
        return iniConfig.getSection("STD").getString("PIEmail"); }
    public String getPIPHONE() { 
        return iniConfig.getSection("STD").getString("PIPhone"); }
    public String getCPI1FNAME() { 
        return iniConfig.getSection("STD").getString("CPI1FName"); }
    public String getCPI1LNAME() { 
        return iniConfig.getSection("STD").getString("CPI1LName"); }
    public String getCPI1EMAIL() { 
        return iniConfig.getSection("STD").getString("CPI1Email"); }
    public String getCPI1INSTITUTION() { 
        return iniConfig.getSection("STD").getString("CPI1institution"); }
    public String getCPI1ADDRESS() { 
        return iniConfig.getSection("STD").getString("CPI1Address"); }
    public String getCPI1CITY() { 
        return iniConfig.getSection("STD").getString("CPI1City"); }
    public String getCPI1STATE() { 
        return iniConfig.getSection("STD").getString("CPI1State"); }
    public String getCPI1ZIPCODE() { 
        return iniConfig.getSection("STD").getString("CPI1ZipCode"); }
    public String getCPI2FNAME() { 
        return iniConfig.getSection("STD").getString("CPI2FName"); }
    public String getCPI2LNAME() { 
        return iniConfig.getSection("STD").getString("CPI2LName"); }
    public String getCPI2EMAIL() { 
        return iniConfig.getSection("STD").getString("CPI2Email"); }
    public String getCPI2INSTITUTION() { 
        return iniConfig.getSection("STD").getString("CPI2institution"); }
    public String getCPI2ADDRESS() { 
        return iniConfig.getSection("STD").getString("CPI2Address"); }
    public String getCPI2CITY() { 
        return iniConfig.getSection("STD").getString("CPI2City"); }
    public String getCPI2STATE() { 
        return iniConfig.getSection("STD").getString("CPI2State"); }
    public String getCPI2ZIPCODE() { 
        return iniConfig.getSection("STD").getString("CPI2ZipCode"); }
    public String getCONTACTFNAME() { 
        return iniConfig.getSection("STD").getString("contactFName"); }
    public String getCONTACTLNAME() { 
        return iniConfig.getSection("STD").getString("contactLName"); }
    public String getCONTACTEMAIL() { 
        return iniConfig.getSection("STD").getString("contactEmail"); }
    public String getCREATEDDATE() { 
        return iniConfig.getSection("STD").getString("createdDate"); }
    public String getUPDATEDDATE() { 
        return iniConfig.getSection("STD").getString("updatedDate"); }
    public String getDCACCESSBOOL() { 
        return iniConfig.getSection("STD").getString("DCAccessBool"); }
    public String getEXTSRC() { 
        return iniConfig.getSection("STD").getString("externalSource"); }
    public String getNSNAME() { 
        return iniConfig.getSection("STD").getString("nsName"); }
    public String getNSABBREV() { 
        return iniConfig.getSection("STD").getString("nsAbbrev"); }
    public String getNSFORMAT() { 
        return iniConfig.getSection("STD").getString("nsFormat"); } 
    public String getNSSOURCE() { 
        return iniConfig.getSection("STD").getString("nsSource"); }

    // ACQ Template
    public String getACQ_DATAACQUISITIONNAME() { 
        return iniConfig.getSection("ACQ").getString("DataAcquisitionName"); }
    public String getACQ_METHOD() { 
        return iniConfig.getSection("ACQ").getString("Method"); }
    public String getACQ_SDDTUDYID() { 
        return iniConfig.getSection("ACQ").getString("SDDtudyName"); }
    public String getACQ_DATADICTIONARYNAME() { 
        return iniConfig.getSection("ACQ").getString("DataDictionaryName"); }
    public String getACQ_EPILAB() { 
        return iniConfig.getSection("ACQ").getString("Epi/Lab"); }
    public String getACQ_OWNEREMAIL() { 
        return iniConfig.getSection("ACQ").getString("OwnerEmail"); }
    public String getACQ_PERMISSIONURI() { 
        return iniConfig.getSection("ACQ").getString("PermissionURI"); }

    // STR Template
    public String getDATAACQUISITIONNAME() { 
        return iniConfig.getSection("STR").getString("DataAcquisitionName"); }
    public String getMETHOD() { 
        return iniConfig.getSection("STR").getString("Method"); }
    public String getDATADICTIONARYNAME() { 
        return iniConfig.getSection("STR").getString("DataDictionaryName"); }
    public String getSDDTUDYID() { return iniConfig.getSection("STR").getString("SDDtudyName"); }
    public String getTOPICNAME() { return iniConfig.getSection("STR").getString("TopicName"); }
    public String getEPILAB() { return iniConfig.getSection("STR").getString("Epi/Lab"); }
    public String getOWNEREMAIL() { return iniConfig.getSection("STR").getString("OwnerEmail"); }
    public String getPERMISSIONURI() { return iniConfig.getSection("STR").getString("PermissionURI"); }
    public String getDEPLOYMENTURI() { return iniConfig.getSection("STR").getString("DeploymentUri"); }
    public String getROWSCOPE() { return iniConfig.getSection("STR").getString("RowScope"); }
    public String getCELLSCOPE() { return iniConfig.getSection("STR").getString("CellScope"); }
    public String getMESSAGESTREAMURI() { return iniConfig.getSection("STR").getString("StreamURI"); }
    public String getMESSAGEURI() { return iniConfig.getSection("STR").getString("hasURI"); }
    public String getMESSAGEPROTOCOL() { return iniConfig.getSection("STR").getString("MessageProtocol"); }
    public String getMESSAGEIP() { return iniConfig.getSection("STR").getString("MessageIP"); }
    public String getMESSAGEPORT() { return iniConfig.getSection("STR").getString("MessagePort"); }
    public String getMESSAGENAME() { return iniConfig.getSection("STR").getString("MessageName"); }

    // SDDA, SDDE, SDDO Template (Part of SDD)
    public String getLABEL() { return iniConfig.getSection("SDDA").getString("Label"); }     // also in PV
    public String getATTRIBUTETYPE() { return iniConfig.getSection("SDDA").getString("AttributeType"); }
    public String getATTTRIBUTEOF() { return iniConfig.getSection("SDDA").getString("AttributeOf"); }
    public String getUNIT() { return iniConfig.getSection("SDDA").getString("Unit"); }
    public String getTIME() { return iniConfig.getSection("SDDA").getString("Time"); }
    public String getENTITY() { return iniConfig.getSection("SDDA").getString("Entity"); }
    public String getROLE() { return iniConfig.getSection("SDDA").getString("Role"); }
    public String getRELATION() { return iniConfig.getSection("SDDA").getString("Relation"); }
    public String getINRELATIONTO() { return iniConfig.getSection("SDDA").getString("InRelationTo"); }
    public String getWASDERIVEDFROM() { return iniConfig.getSection("SDDA").getString("WasDerivedFrom"); }
    public String getWASGENERATEDBY() { return iniConfig.getSection("SDDA").getString("WasGeneratedBy"); }

    // PV Template (Part of SDD)
    public String getCODE() { return iniConfig.getSection("PV").getString("Code"); }
    public String getCODEVALUE() { return iniConfig.getSection("PV").getString("CodeValue"); }
    public String getCLASS() { return iniConfig.getSection("PV").getString("Class"); }

    // SID Template
    public String getSAMPLEID() { return iniConfig.getSection("SID").getString("sampleID"); }
    public String getSAMPLESTUDYID() { return iniConfig.getSection("SID").getString("sampleStudyID"); }
    public String getSAMPLESUFFIX() { return iniConfig.getSection("SID").getString("sampleSuffix"); }
    public String getSUBJECTID() { return iniConfig.getSection("SID").getString("subjectID"); }  // also in PID
    public String getSAMPLETYPE() { return iniConfig.getSection("SID").getString("sampleType"); }
    public String getSAMPLINGMETHOD() { return iniConfig.getSection("SID").getString("samplingMethod"); }
    public String getSAMPLINGVOL() { return iniConfig.getSection("SID").getString("samplingVol"); }
    public String getSAMPLINGVOLUNIT() { return iniConfig.getSection("SID").getString("samplingVolUnit"); }
    public String getSTORAGETEMP() { return iniConfig.getSection("SID").getString("storageTemp"); }
    public String getFTCOUNT() { return iniConfig.getSection("SID").getString("FTcount"); }

    // MAP Template
    public String getORIGINALPID() { return iniConfig.getSection("MAP").getString("originalPID"); }
    public String getORIGINALSID() { return iniConfig.getSection("MAP").getString("originalSID"); }
    public String getOBJECTTYPE() { return iniConfig.getSection("MAP").getString("objecttype"); }
    public String getMAPSTUDYID() { return iniConfig.getSection("MAP").getString("studyId"); }
    public String getTIMESCOPEID() { return iniConfig.getSection("MAP").getString("timeScope"); }

    // KGR Template
    public String getAgentOriginalID() { return iniConfig.getSection("KGR").getString("agentOriginalID"); }
    public String getAgentType() { return iniConfig.getSection("KGR").getString("agentType"); }
    public String getAgentShortName() { return iniConfig.getSection("KGR").getString("agentShortName"); }
    public String getAgentName() { return iniConfig.getSection("KGR").getString("agentName"); }
    public String getAgentGivenName() { return iniConfig.getSection("KGR").getString("agentGivenName"); }
    public String getAgentFamilyName() { return iniConfig.getSection("KGR").getString("agentFamilyName"); }
    public String getAgentEmail() { return iniConfig.getSection("KGR").getString("agentEmail"); }
    public String getAgentHasAffiliationUri() { return iniConfig.getSection("KGR").getString("agentHasAffiliationUri"); }
    public String getManagerEmail() { return iniConfig.getSection("KGR").getString("managerEmail"); }
    public String getAgentTelephone() { return iniConfig.getSection("KGR").getString("agentTelephone"); }
    public String getAgentUrl() { return iniConfig.getSection("KGR").getString("agentUrl"); }
    public String getAgentJobTitle() { return iniConfig.getSection("KGR").getString("agentJobTitle"); }
    public String getAgentParentOrganization() { return iniConfig.getSection("KGR").getString("agentParentOrganization"); }
    public String getAgentAddress() { return iniConfig.getSection("KGR").getString("agentAddress"); }
    public String getPlaceOriginalID() { return iniConfig.getSection("KGR").getString("agentOriginalID"); }
    public String getPlaceType() { return iniConfig.getSection("KGR").getString("placeType"); }
    public String getPlaceName() { return iniConfig.getSection("KGR").getString("placeName"); }
    public String getPlaceImage() { return iniConfig.getSection("KGR").getString("placeImage"); }
    public String getPlaceContainedIn() { return iniConfig.getSection("KGR").getString("placeContainedIn"); }
    public String getPlaceIdentifier() { return iniConfig.getSection("KGR").getString("placeIdentifier"); }
    public String getPlaceGeo() { return iniConfig.getSection("KGR").getString("placeGeo"); }
    public String getPlaceLatitude() { return iniConfig.getSection("KGR").getString("placeLatitude"); }
    public String getPlaceLongitude() { return iniConfig.getSection("KGR").getString("placeLongitude"); }
    public String getPlaceUrl() { return iniConfig.getSection("KGR").getString("placeUrl"); }
    public String getPostalAddressType() { return iniConfig.getSection("KGR").getString("postalAddressType"); }
    public String getPostalAddressStreet() { return iniConfig.getSection("KGR").getString("postalAddressStreet"); }
    public String getPostalAddressPostalCode() { return iniConfig.getSection("KGR").getString("postalAddressPostalCode"); }
    public String getPostalAddressCountry() { return iniConfig.getSection("KGR").getString("postalAddressCountry"); }
    public String getPostalAddressLocality() { return iniConfig.getSection("KGR").getString("postalAddressLocality"); }
    public String getPostalAddressRegion() { return iniConfig.getSection("KGR").getString("postalAddressRegion"); }
}    
