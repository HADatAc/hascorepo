package org.hascoapi.ingestion;

import java.lang.String;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.hascoapi.entity.pojo.Stream;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.DOI;
import org.hascoapi.entity.pojo.DPL;
import org.hascoapi.entity.pojo.SDD;
import org.hascoapi.entity.pojo.SDDAttribute;
import org.hascoapi.entity.pojo.SDDObject;
import org.hascoapi.entity.pojo.SSDSheet;
import org.hascoapi.entity.pojo.Study;
import org.hascoapi.entity.pojo.StudyObjectCollection;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.NameSpaces;
import org.hascoapi.utils.SPARQLUtils;


public class IngestionWorker {

    public static void ingest(DataFile dataFile, File file, String templateFile) {

        System.out.println("Processing file with filename: " + dataFile.getFilename());
        System.out.println("Processing file with URI: " + dataFile.getUri());

        String studyUri = "";
        if (dataFile.getFilename().contains("DSG-")) {
            studyUri = dataFile.getUri().replace("DF","ST");
        }

        dataFile.setLastProcessTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        dataFile.getLogger().resetLog();
        dataFile.save();
 
        String fileName = dataFile.getFilename();

        dataFile.getLogger().println(String.format("Processing file: %s", fileName));

        // file is rejected if it has an invalid extension
        RecordFile recordFile = null;
        if (fileName.endsWith(".csv")) {
            recordFile = new CSVRecordFile(file);
        } else if (fileName.endsWith(".xlsx")) {
            recordFile = new SpreadsheetRecordFile(file,dataFile.getFilename(),"InfoSheet");
        } else {
            dataFile.getLogger().printExceptionByIdWithArgs("GBL_00003", fileName);
            System.out.println("[ERROR] IngestionWorker: invalid file extension.");
            return;
        }

        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("SDD_00001");
            System.out.println("[ERROR] IngestionWorker: No InfoSheet in provided file.");
            return;
        } 
       
        dataFile.setRecordFile(recordFile);

        boolean bSucceed = false;
        GeneratorChain chain = getGeneratorChain(dataFile, templateFile);
        if (studyUri == null || studyUri.isEmpty()) {
            chain.setStudyUri("");
        } else {
            chain.setStudyUri(studyUri);
        }

        if (chain != null) {
            System.out.println("IngestionWorker: chain.generate() STARTED.");
            bSucceed = chain.generate();
            System.out.println("IngestionWorker: chain.generate() ENDED.");
        }

        if (bSucceed) {
            
            // if chain includes PVGenerator, executes PVGenerator.generateOthers()
            if (chain.getPV()) {
                PVGenerator.generateOthers(chain.getCodebookFile(), chain.getSddName(), ConfigProp.getKbPrefix());
            }
            
            if (dataFile.getFileStatus().equals(DataFile.WORKING_STD)) {
                dataFile.setFileStatus(DataFile.PROCESSED_STD);
            } else {
                dataFile.setFileStatus(DataFile.PROCESSED);
            }
            dataFile.setCompletionTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            dataFile.setStudyUri(chain.getStudyUri());
            dataFile.save();

        }
        
        if (dataFile.getFileStatus().equals(DataFile.PROCESSED_STD)) {
            System.out.println("================> REINVOKING DSG for SDD processing");
            System.out.println("  DataFile Status: [" + dataFile.getFileStatus() + "]");
            IngestionWorker.ingest(dataFile, file, templateFile);
        }
    }

    public static GeneratorChain getGeneratorChain(DataFile dataFile, String templateFile) {
        GeneratorChain chain = null;
        String fileName = FilenameUtils.getBaseName(dataFile.getFilename());

        //if (fileName.startsWith("DA-")) {
        //    chain = annotateDAFile(dataFile);
        //    
        //} else 
        
        if (fileName.startsWith("DSG-") && 
            dataFile.getFileStatus().equals(DataFile.WORKING_STD)) {
            chain = annotateSTDFile(dataFile, templateFile);
            
        } else if (fileName.startsWith("DSG-") && 
            dataFile.getFileStatus().equals(DataFile.PROCESSED_STD)) {
            chain = annotateSSDFile(dataFile, templateFile);
            
        } else if (fileName.startsWith("DPL-")) {
            chain = annotateDPLFile(dataFile);
            
        } else if (fileName.startsWith("INS-")) {
            chain = annotateINSFile(dataFile, templateFile);
            
        } else if (fileName.startsWith("STR-")) {
            //checkSTRFile(dataFile);
            chain = annotateSTRFile(dataFile);
            
        } else if (fileName.startsWith("SDD-")) {
            chain = annotateSDDFile(dataFile, templateFile);
            
        } else if (fileName.startsWith("DOI-")) {
            chain = annotateDOIFile(dataFile);
            
        } else {
            dataFile.getLogger().printExceptionById("GBL_00001");
            return null;
        }

        return chain;
    }

    /* 
     * Move any file that isMediaFile() into a media folder in processed files.
     * At the moment, no other kind of processing is performed by this code. 
     */
    /* 
    public static void processMediaFile(DataFile dataFile, File file) {
    	//Move the file to the folder for processed files
        String new_path = ConfigProp.getPathMedia();
 
        File file = new File(dataFile.getAbsolutePath());

        File destFolder = new File(new_path);
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }

        dataFile.setFileStatus(DataFile.PROCESSED);
        dataFile.setCompletionTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        dataFile.setDir(ConfigProp.MEDIA_FOLDER);
        dataFile.setStudyUri("");
        dataFile.save();

        file.renameTo(new File(destFolder + "/" + dataFile.getStorageFileName()));
        file.delete();
    }
    */

    /*===========================================================================================*
     *                                  METADATA TEMPLATE ANNOTATORS                             *
     *===========================================================================================*/
    
    /****************************
     *    DSG                   *
     ****************************/    
    
    public static GeneratorChain annotateSTDFile(DataFile dataFile, String templateFile) {

        Map<String, String> mapCatalog = new HashMap<String, String>();
        for (Record record : dataFile.getRecordFile().getRecords()) {
            mapCatalog.put(record.getValueByColumnIndex(0), record.getValueByColumnIndex(1));
            System.out.println(record.getValueByColumnIndex(0) + ":" + record.getValueByColumnIndex(1));
        }

        if (dataFile.getFilename().endsWith(".xlsx")) {
            nameSpaceGen(dataFile, mapCatalog,templateFile);
        } else {
            System.out.println("[ERROR] StudyGenerator: DSG file needs to have suffix [.xlsx].");
            return null;
        }

        GeneratorChain chain = new GeneratorChain();
        chain.setNamedGraphUri(dataFile.getUri());
        chain.addGenerator(new NameSpaceGenerator(dataFile,templateFile));

        RecordFile studyRecordFile = null;

        if (mapCatalog.get("hasStudyDescription") != null) {
            System.out.print("Extracting STD sheet from spreadsheet... ");
            studyRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), dataFile.getFilename(), mapCatalog.get("hasStudyDescription"));
            if (studyRecordFile == null) {
                System.out.println("[ERROR] StudyGenerator: studyRecordFile is NULL.");
                return null;
            } else if (studyRecordFile.getRecords() == null) {
                System.out.println("[ERROR] StudyGenerator: studyRecordFile.getRecords() is NULL.");
                return null;
            } else{
                System.out.println("studyRecordFile has [" + studyRecordFile.getRecords().size() + "] rows");
            }
            dataFile.setRecordFile(studyRecordFile);
            System.out.print("Done extracting STD sheet. ");
        } else {
            System.out.println("[ERROR] StudyGenerator: could not find any sheet inside of DSG called [hasStudyDescription].");
            return null;
        }

        chain.addGenerator(new AgentGenerator(dataFile,null,templateFile));
        chain.addGenerator(new StudyGenerator(dataFile,null,templateFile));

        return chain;
    }

    /****************************
     *    SSD                   *
     ****************************/    
    
     public static GeneratorChain annotateSSDFile(DataFile dataFile, String templateFile) {
        String studyUri = dataFile.getUri().replaceAll("DF", "ST");
        System.out.println("Processing SSD file of " + studyUri + "...");

        Map<String, String> mapCatalog = new HashMap<String, String>();
        for (Record record : dataFile.getRecordFile().getRecords()) {
            mapCatalog.put(record.getValueByColumnIndex(0), record.getValueByColumnIndex(1));
            System.out.println(record.getValueByColumnIndex(0) + ":" + record.getValueByColumnIndex(1));
        }

        RecordFile ssdRecordFile = null;

        if (dataFile.getFilename().endsWith(".xlsx")) {

            if (mapCatalog.get("hasEntityDesign") != null) {
                System.out.print("Extracting SSD sheet from spreadsheet... ");
                ssdRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), dataFile.getFilename(), mapCatalog.get("hasEntityDesign"));
                if (ssdRecordFile == null) {
                    System.out.println("[ERROR] IngestionWorker: ssdRecordFile is NULL.");
                    return null;
                } else if (ssdRecordFile.getRecords() == null) {
                    System.out.println("[ERROR] IngestionWorker: ssdRecordFile.getRecords() is NULL.");
                    return null;
                } else{
                    System.out.println("ssdRecordFile has [" + ssdRecordFile.getRecords().size() + "] rows");
                }
                dataFile.setRecordFile(ssdRecordFile);
                System.out.print("Done extracting SSD sheet. ");
            } else {
                System.out.println("[ERROR] IngestionWorker: could not find any sheet inside of DSG called [hasEntityDesign].");
                return null;
            }
        } else {
            System.out.println("[ERROR] IngestionWorker: DSG file needs to have suffix [.xlsx].");
            return null;
        }

        // THERE IS AN SSD SHEET, BUT IT IS EMPTY. STOP EXECUTION OF THE CHAIN WITH A WARNING
        if (ssdRecordFile.getRecords().size() < 1) {
            System.out.println("[WARNING] IngestionWorker: SSD sheet is empty.");
            dataFile.getLogger().println("[WARNING] IngestionWorker: SSD sheet is empty.");
            return new SSDGeneratorChain();    
        }

        SSDSheet ssd = new SSDSheet(dataFile);
        //Map<String, String> mapCatalog = ssd.getCatalog();
        mapCatalog = ssd.getCatalog();
        Map<String, List<String>> mapContent = ssd.getMapContent();
        
        //RecordFile SSDsheet = new SpreadsheetRecordFile(dataFile.getFile(), "SSD");
        //dataFile.setRecordFile(SSDsheet);

        SSDGeneratorChain chain = new SSDGeneratorChain();
        chain.setNamedGraphUri(dataFile.getUri());

        Study study = null;

        //if (SSDsheet.isValid()) {
        if (ssdRecordFile.isValid()) {

            System.out.println("SSD Processing: adding VirtualColumnGenerator");
            VirtualColumnGenerator vcgen = new VirtualColumnGenerator(dataFile);
            vcgen.setStudyUri(studyUri);
            chain.addGenerator(vcgen);
            //System.out.println("added VirtualColumnGenerator for " + dataFile.getAbsolutePath());
            
            System.out.println("SSD Processing: adding SSDGenerator");
            SSDGenerator socgen = new SSDGenerator(dataFile);
            socgen.setStudyUri(studyUri);
            chain.addGenerator(socgen);
            //System.out.println("added SSDGenerator for " + dataFile.getAbsolutePath());

            //String studyUri = socgen.getStudyUri();
            if (studyUri == null || studyUri.isEmpty()) {
                return null;
            } else {
                chain.setStudyUri(studyUri);
                study = Study.find(studyUri);
                if (study != null) {
                    System.out.println("SSD Processing: Found study [" + study.getUri() + "]");
                    dataFile.getLogger().println("SSD ingestion: The study uri :" + studyUri + " is in the TripleStore.");
                    socgen.setStudyUri(studyUri);
                } else {
                    dataFile.getLogger().printExceptionByIdWithArgs("SSD_00005", studyUri);
                    return null;
                }
            }

            System.out.println("SSD Processing: SubjectGroup verification.");
            // check the rule for hasco:SubjectGroup, there should be one and only such type
            int subjectGroupCount = 0;
            for (Record record : dataFile.getRecordFile().getRecords()) {
                //String socName = record.getValueByColumnIndex(1);
                String socType = record.getValueByColumnIndex(2);
                if (socType.contains("SubjectGroup")) {
                    subjectGroupCount++;
                }
            }

            if ( subjectGroupCount == 0 ) {
                System.out.println("[ERROR] SSD Processing: NO SubjectGroup.");
                dataFile.getLogger().printExceptionById("SSD_00006");
                return null;
            }
            if ( subjectGroupCount > 1 ) {
                System.out.println("SSD Processing: More than one SubjectGroup.");
                dataFile.getLogger().printExceptionById("SSD_00007");
                return null;
            }

            chain.setNamedGraphUri(dataFile.getUri());
            chain.setDataFile(dataFile);

        } else {
            //chain.setInvalid();
            dataFile.getLogger().printException("Cannot locate SSD's sheet ");
        }

        System.out.println("IngestionWorker: pre-processing StudyObjectGenerator. Study Id is  " + study.getId());
        String study_uri = chain.getStudyUri();
        for (String i : mapCatalog.keySet()) {
            if (mapCatalog.get(i) != null && !mapCatalog.get(i).isEmpty()) {
                try {
                    System.out.println("Pre-processing SOC [" + mapCatalog.get(i) + "]");
                    RecordFile SOsheet = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get(i).replace("#", ""));
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(SOsheet);
                    if (mapContent == null || mapContent.get(i) == null) {
                        dataFile.getLogger().printException("No value for MapContent with index [" + i + "]");
                    } else {
                        System.out.println("SSD Processing: adding StudyObjectGenerator");
                        chain.addGenerator(new StudyObjectGenerator(dataFileForSheet, mapContent.get(i), mapContent, study_uri, study.getId()));
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("SSD Processing: Completed GeneratorChain()");
        return chain;
    }

    /****************************
     *    DOI                   *
     ****************************/    
    
    public static GeneratorChain annotateDOIFile(DataFile dataFile) {
        System.out.println("Processing DOI file ...");
        RecordFile recordFile = new SpreadsheetRecordFile(dataFile.getFile(), "InfoSheet");
        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("DOI_00001");
            return null;
        } else {
            dataFile.setRecordFile(recordFile);
        }
        
        DOI doi = new DOI(dataFile);
        Map<String, String> mapCatalog = doi.getCatalog();
        GeneratorChain chain = new GeneratorChain();

        String studyId = doi.getStudyId();
        if (studyId == null || studyId.isEmpty()) {
            dataFile.getLogger().printExceptionByIdWithArgs("DOI_00002", studyId);
            return null;
        } else {
            Study study = Study.findById(studyId);
            if (study != null) {
                chain.setStudyUri(study.getUri());
                dataFile.getLogger().println("DOI ingestion: Found study id [" + studyId + "]");
            } else {
                dataFile.getLogger().printExceptionByIdWithArgs("DOI_00003", studyId);
                return null;
            }
        }

        chain.setDataFile(dataFile);
        
        String doiVersion = doi.getVersion();
        if (doiVersion != null && !doiVersion.isEmpty()) {
            dataFile.getLogger().println("DOI ingestion: version is [" + doiVersion + "]");
        } else {
            dataFile.getLogger().printExceptionById("DOI_00004");
            return null;
        }

        if (mapCatalog.get("Filenames") == null) {
            dataFile.getLogger().printExceptionById("DOI_00005");
            return null;
        }

        String sheetName = mapCatalog.get("Filenames").replace("#", "");
        RecordFile sheet = new SpreadsheetRecordFile(dataFile.getFile(), sheetName);
                
        try {
        	DataFile dataFileForSheet = (DataFile)dataFile.clone();
        	dataFileForSheet.setRecordFile(sheet);
        	chain.addGenerator(new DOIGenerator(dataFileForSheet));
        } catch (CloneNotSupportedException e) {
        	e.printStackTrace();
            dataFile.getLogger().printExceptionById("DOI_00006");
            return null;
        }

        return chain;
    }

    /****************************
     *    DPL                   *
     ****************************/    
    
    public static GeneratorChain annotateDPLFile(DataFile dataFile) {
        RecordFile recordFile = new SpreadsheetRecordFile(dataFile.getFile(), "InfoSheet");
        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("DPL_00001");
            return null;
        } else {
            dataFile.setRecordFile(recordFile);
        }
        
        DPL dpl = new DPL(dataFile);
        Map<String, String> mapCatalog = dpl.getCatalog();

        GeneratorChain chain = new GeneratorChain();
        for (String key : mapCatalog.keySet()) {
            if (mapCatalog.get(key).length() > 0) {
                String sheetName = mapCatalog.get(key).replace("#", "");
                RecordFile sheet = new SpreadsheetRecordFile(dataFile.getFile(), sheetName);
                
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    chain.addGenerator(new DPLGenerator(dataFileForSheet));
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        return chain;
    }

    /****************************
     *    INS                   *
     ****************************/    
    
     public static GeneratorChain annotateINSFile(DataFile dataFile, String templateFile) {
        RecordFile recordFile = new SpreadsheetRecordFile(dataFile.getFile(), "InfoSheet");
        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("DPL_00001");
            return null;
        } else {
            dataFile.setRecordFile(recordFile);
        }
        
        Map<String, String> mapCatalog = new HashMap<String, String>();
        for (Record record : dataFile.getRecordFile().getRecords()) {
            mapCatalog.put(record.getValueByColumnIndex(0), record.getValueByColumnIndex(1));
            System.out.println(record.getValueByColumnIndex(0) + ":" + record.getValueByColumnIndex(1));
        }

        GeneratorChain chain = new GeneratorChain();
        RecordFile sheet = null;

        try {

            nameSpaceGen(dataFile, mapCatalog,templateFile);
            chain.setNamedGraphUri(dataFile.getUri());
            chain.addGenerator(new NameSpaceGenerator(dataFile,templateFile));
    
            String responseOptionSheet = mapCatalog.get("ResponseOptions");
            if (responseOptionSheet == null) {
                System.out.println("[WARNING] 'ResponseOptions' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'ResponseOptions' sheet is missing.");
            } else {
                responseOptionSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), responseOptionSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    INSGenerator respOptionGen = new INSGenerator("responseoption",dataFileForSheet);
                    respOptionGen.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(respOptionGen);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            String codeBookSheet = mapCatalog.get("CodeBooks");
            if (codeBookSheet == null) {
                System.out.println("[WARNING] 'CodeBooks' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'CodeBooks' sheet is missing.");
            } else {
                codeBookSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), codeBookSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    INSGenerator codeBookGen = new INSGenerator("codebook",dataFileForSheet);
                    codeBookGen.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(codeBookGen);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            String codeBookSlotSheet = mapCatalog.get("CodeBookSlots");
            if (codeBookSlotSheet == null) {
                System.out.println("[WARNING] 'CodeBookSlots' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'CodeBookSlots' sheet is missing.");
            } else {
                codeBookSlotSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), codeBookSlotSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    CodeBookSlotGenerator cbSlotGen = new CodeBookSlotGenerator(dataFileForSheet);
                    cbSlotGen.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(cbSlotGen);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            String detectorStemSheet = mapCatalog.get("DetectorStems");
            if (detectorStemSheet == null) {
                System.out.println("[WARNING] 'DetectorStems' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'DetectorStems' sheet is missing.");
            } else {
                detectorStemSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), detectorStemSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    INSGenerator detStemGen = new INSGenerator("detectorstem",dataFileForSheet);
                    detStemGen.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(detStemGen);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            String detectorSheet = mapCatalog.get("Detectors");
            if (detectorSheet == null) {
                System.out.println("[WARNING] 'Detectors' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'Detectors' sheet is missing.");
            } else {
                detectorSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), detectorSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    DetectorGenerator detGen = new DetectorGenerator(dataFileForSheet);
                    detGen.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(detGen);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            String containerSlotSheet = mapCatalog.get("ContainerSlots");
            if (containerSlotSheet == null) {
                System.out.println("[WARNING] 'ContainerSlots' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'ContainerSlots' sheet is missing.");
            } else {
                containerSlotSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), containerSlotSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    ContainerSlotGenerator slotGen = new ContainerSlotGenerator(dataFileForSheet);
                    slotGen.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(slotGen);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }

            String instrumentSheet = mapCatalog.get("Instruments");
            if (containerSlotSheet == null) {
                System.out.println("[WARNING] 'Instruments' sheet is missing.");
                dataFile.getLogger().println("[WARNING] 'Instruments' sheet is missing.");
            } else {
                instrumentSheet.replace("#", "");
                sheet = new SpreadsheetRecordFile(dataFile.getFile(), instrumentSheet);
                try {
                    DataFile dataFileForSheet = (DataFile)dataFile.clone();
                    dataFileForSheet.setRecordFile(sheet);
                    INSGenerator ins = new INSGenerator("instrument",dataFileForSheet);
                    ins.setNamedGraphUri(dataFileForSheet.getUri());
                    chain.addGenerator(ins);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chain;
    }

    /****************************
     *    STR                   *
     ****************************/    
    
    public static GeneratorChain annotateSTRFile(DataFile dataFile) {
        System.out.println("Processing STR file ...");
        
        // verifies if data file is an Excel spreadsheet
        String fileName = dataFile.getFilename();
        if (!fileName.endsWith(".xlsx")) {
            dataFile.getLogger().printExceptionById("STR_00004");
            return null;
        } 

        // verifies if data file contains an InfoSheet sheet
        RecordFile recordFile = new SpreadsheetRecordFile(dataFile.getFile(), "InfoSheet");
        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("STR_00001");
            return null;
        } else {
            dataFile.setRecordFile(recordFile);
        }

        STRInfoGenerator strInfo = new STRInfoGenerator(dataFile);        
        Study strStudy = strInfo.getStudy();
        String strVersion = strInfo.getVersion();
                
        // verifies if study is specified
        if (strStudy == null) {
            dataFile.getLogger().printExceptionByIdWithArgs("STR_00002", strInfo.getStudyId());
            return null;
        }
        // verifies if version is specified
        if (strVersion == "") {
            dataFile.getLogger().printExceptionById("STR_00003");
            return null;
        }
        Map<String, String> mapCatalog = strInfo.getCatalog();

        RecordFile fileStreamRecordFile = null;
        RecordFile messageStreamRecordFile = null;
        RecordFile messageTopicRecordFile = null;

        // verifies if filestream sheet is available, even if no file stream is specified
        if (mapCatalog.get(STRInfoGenerator.FILESTREAM) == null) { 
        	dataFile.getLogger().printExceptionById("STR_00005");
        	return null;
        }
        fileStreamRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get(STRInfoGenerator.FILESTREAM).replace("#", ""));
        
        // verifies if messagestream sheet is available, even if no message stream is specified
        if (mapCatalog.get(STRInfoGenerator.MESSAGESTREAM) == null) {
    		dataFile.getLogger().printExceptionById("STR_00006");
    		return null;
        }
        messageStreamRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get(STRInfoGenerator.MESSAGESTREAM).replace("#", ""));
        
        // verifies if messagetopic sheet is available, even if no message topic is specified
        if (mapCatalog.get(STRInfoGenerator.MESSAGETOPIC) == null) {
    		dataFile.getLogger().printExceptionById("STR_00016");
    		return null;
        }
        messageTopicRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get(STRInfoGenerator.MESSAGETOPIC).replace("#", ""));
        
        // verifies if not both fileStream sheet and messageStream sheet are empty
        if (fileStreamRecordFile.getNumberOfRows() <= 0 && messageStreamRecordFile.getNumberOfRows() <= 0) {
    		dataFile.getLogger().printExceptionById("STR_00007");
    		return null;        	
        }
        // verifies that there is info in messageTopics in case messageStream is not empty
        if ((messageStreamRecordFile.getNumberOfRows() <= 0 && messageTopicRecordFile.getNumberOfRows() > 0) ||
            (messageStreamRecordFile.getNumberOfRows() > 0 && messageTopicRecordFile.getNumberOfRows() <= 0)) {
    		dataFile.getLogger().printExceptionById("STR_00010");
    		return null;
        }

        GeneratorChain chain = new GeneratorChain();
        chain.setStudyUri(strStudy.getUri());
        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String startTime = isoFormat.format(new Date());
        if (fileStreamRecordFile.getNumberOfRows() > 1 && fileStreamRecordFile.getRecords().size() > 0) {
        	// TODO
            //chain.addGenerator(new STRFileGenerator(dataFile, strStudy, fileStreamRecordFile, startTime));
        }
        if (messageStreamRecordFile.getNumberOfRows() > 1 && messageStreamRecordFile.getRecords().size() > 0) {
        	STRMessageGenerator messageGen = new STRMessageGenerator(dataFile, strStudy, messageStreamRecordFile, startTime);
        	if (!messageGen.isValid()) {
        		dataFile.getLogger().printExceptionByIdWithArgs(messageGen.getErrorMessage(),messageGen.getErrorArgument());
            	return null;
        	}
        	chain.addGenerator(messageGen);
        }
        if (messageTopicRecordFile.getNumberOfRows() > 1 && messageTopicRecordFile.getRecords().size() > 0) {
        	STRTopicGenerator topicGen = new STRTopicGenerator(dataFile, messageTopicRecordFile, startTime);
        	if (!topicGen.isValid()) {
        		dataFile.getLogger().printExceptionByIdWithArgs(topicGen.getErrorMessage(),topicGen.getErrorArgument());
            	return null;
        	}
        	chain.addGenerator(topicGen);
        }
        return chain;
    }

    /****************************
     *    SDD                   *
     ****************************/    
    
    public static GeneratorChain annotateSDDFile(DataFile dataFile, String templateFile) {
        System.out.println("Processing SDD file ...");
        
        RecordFile recordFile = new SpreadsheetRecordFile(dataFile.getFile(), "InfoSheet");
        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("SDD_00001");
            return null;
        } else {
            dataFile.setRecordFile(recordFile);
        }

        String sddUri = dataFile.getUri().replace("DFL","SDDICT");

        Map<String, String> mapCatalog = new HashMap<String, String>();
        for (Record record : dataFile.getRecordFile().getRecords()) {
            mapCatalog.put(record.getValueByColumnIndex(0), record.getValueByColumnIndex(1));
            System.out.println(record.getValueByColumnIndex(0) + ":" + record.getValueByColumnIndex(1));
            if (record.getValueByColumnIndex(0).isEmpty() && record.getValueByColumnIndex(1).isEmpty()) {
                break;
            }
        }

        nameSpaceGen(dataFile, mapCatalog, templateFile);

        SDD sdd = new SDD(dataFile, templateFile);
        String fileName = dataFile.getFilename();
        if (mapCatalog.get("SDD_ID") == "") {
            dataFile.getLogger().printExceptionById("SDD_00003");
            return null;
        }
        String sddId = mapCatalog.get("SDD_ID");
        if (mapCatalog.get("Version") == "") {
            dataFile.getLogger().printExceptionById("SDD_00018");
            return null;
        }
        String sddVersion = mapCatalog.get("Version");

        RecordFile codeMappingRecordFile = null;
        RecordFile dictionaryRecordFile = null;
        RecordFile codeBookRecordFile = null;
        //RecordFile timelineRecordFile = null;

        File codeMappingFile = null;
        if (fileName.endsWith(".xlsx")) {
            codeMappingFile = sdd.downloadFile(mapCatalog.get("Code_Mappings"), 
                    "sddtmp/" + fileName.replace(".xlsx", "") + "-code-mappings.csv");

            if (mapCatalog.get("Codebook") != null) { 
                codeBookRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get("Codebook").replace("#", ""));
                System.out.println("IngestionWorker: read codeBookRecordFile with " + codeBookRecordFile.getRecords().size() + " records.");
            }
            
            if (mapCatalog.get("Data_Dictionary") != null) {
                dictionaryRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get("Data_Dictionary").replace("#", ""));
                System.out.println("IngestionWorker: read dictionaryRecordFile with " + dictionaryRecordFile.getRecords().size() + " records.");
            }
            
            //if (mapCatalog.get("Timeline") != null) {
            //    timelineRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get("Timeline").replace("#", ""));
            //}
        }

        if (codeMappingFile != null) {
            codeMappingRecordFile = new CSVRecordFile(codeMappingFile);
            if (!sdd.readCodeMapping(codeMappingRecordFile)) {
                dataFile.getLogger().printWarningById("SDD_00016");
            } else {
                dataFile.getLogger().println(String.format("Codemappings: " + sdd.getCodeMapping().get("U"), fileName));
            }
        } else {
            dataFile.getLogger().printWarningById("SDD_00017");
        }

        if (!sdd.readDataDictionary(dictionaryRecordFile, dataFile)) {
            dataFile.getLogger().printExceptionById("SDD_00004");
            //return null;
        }
        if (codeBookRecordFile == null || !sdd.readCodebook(codeBookRecordFile)) {
            dataFile.getLogger().printWarningById("SDD_00005");
        }
        //if (timelineRecordFile == null || !sdd.readTimeline(timelineRecordFile)) {
        //    dataFile.getLogger().printWarningById("SDD_00006");
        //}

        GeneratorChain chain = new GeneratorChain();
        chain.setNamedGraphUri(dataFile.getUri());
        chain.setPV(true);
        
        System.out.println("DictionaryRecordFile: " + dictionaryRecordFile.isValid());
        if (dictionaryRecordFile != null && dictionaryRecordFile.isValid()) {
            DataFile dictionaryFile;
            try {
                dictionaryFile = (DataFile)dataFile.clone();
                dictionaryFile.setRecordFile(dictionaryRecordFile);
                chain.addGenerator(new SDDAttributeGenerator(dictionaryFile, sddUri, sddId, sdd.getCodeMapping(), sdd.readDDforEAmerge(dictionaryRecordFile), templateFile));
                chain.addGenerator(new SDDObjectGenerator(dictionaryFile, sddUri, sddId, sdd.getCodeMapping()));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // codebook needs to be processed after data dictionary because codebook relies on 
        // data dictionary's attributes (DASAs) to group codes for categorical variables
        
        System.out.println("CodeBookRecordFile: " + codeBookRecordFile.isValid());
        if (codeBookRecordFile != null && codeBookRecordFile.isValid()) {
            DataFile codeBookFile;
            try {
                codeBookFile = (DataFile)dataFile.clone();
                codeBookFile.setRecordFile(codeBookRecordFile);
                chain.setCodebookFile(codeBookFile);
                chain.setSddName(URIUtils.replacePrefixEx(sddUri));
                chain.addGenerator(new PVGenerator(codeBookFile, sddUri, sddId, sdd.getMapAttrObj(), sdd.getCodeMapping()));
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        GeneralGenerator generalGenerator = new GeneralGenerator(dataFile, "SemanticDataDictionary");
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("hasURI", sddUri);
        row.put("a", "hasco:SemanticDataDictionary");
        row.put("hasco:hascoType", "hasco:SemanticDataDictionary");
        row.put("rdfs:label", sddId);
        row.put("rdfs:comment", "Generated from SDD file [" + dataFile.getFilename() + "]");
        row.put("vstoi:hasVersion", sddVersion);
        row.put("vstoi:hasSIRManagerEmail", dataFile.getHasSIRManagerEmail());
        generalGenerator.addRow(row);
        chain.setNamedGraphUri(URIUtils.replacePrefixEx(dataFile.getUri()));
        chain.addGenerator(generalGenerator);
        dataFile.getLogger().println("This SDD is assigned with uri: " + sddUri + " and is of type hasco:SemanticDataDictionary");

        return chain;
    }

    /****************************
     *    DA                    *
     ****************************/    
    
    /* 
    public static GeneratorChain annotateDAFile(DataFile dataFile) {
        System.out.println("Processing DA file " + dataFile.getFilename());

        GeneratorChain chain = new GeneratorChain();

        STR str = null;
        String str_uri = null;
        String deployment_uri = null;
        String schema_uri = null;
        String study_uri = null;

        if (dataFile != null) {
            str_uri = URIUtils.replacePrefixEx(dataFile.getDataAcquisitionUri());
            str = STR.findByUri(str_uri);
            if (str != null) {
                if (!str.isComplete()) {
                    dataFile.getLogger().printWarningByIdWithArgs("DA_00003", str_uri);
                    chain.setInvalid();
                    return chain;
                } else {
                    dataFile.getLogger().println(String.format("STR <%s> has been located", str_uri));
                }
                study_uri = str.getStudy().getUri();
                deployment_uri = str.getDeploymentUri();
                schema_uri = str.getSchemaUri();
            } else {
                dataFile.getLogger().printWarningByIdWithArgs("DA_00004", str_uri);
                chain.setInvalid();
                return chain;
            }
        }

        if (study_uri == null || study_uri.isEmpty()) {
            dataFile.getLogger().printExceptionByIdWithArgs("DA_00008", str_uri);
            chain.setInvalid();
            return chain;
        } else {
            try {
                study_uri = URLDecoder.decode(study_uri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                dataFile.getLogger().printException(String.format("URL decoding error for study uri <%s>", study_uri));
                chain.setInvalid();
                return chain;
            }
            dataFile.getLogger().println(String.format("Study <%s> specified for data acquisition <%s>", study_uri, str_uri));
        }

        if (schema_uri == null || schema_uri.isEmpty()) {
            dataFile.getLogger().printExceptionByIdWithArgs("DA_00005", str_uri);
            chain.setInvalid();
            return chain;
        } else {
            dataFile.getLogger().println(String.format("Schema <%s> specified for data acquisition: <%s>", schema_uri, str_uri));
        }
        
        if (deployment_uri == null || deployment_uri.isEmpty()) {
            dataFile.getLogger().printExceptionByIdWithArgs("DA_00006", str_uri);
            chain.setInvalid();
            return chain;
        } else {
            try {
                deployment_uri = URLDecoder.decode(deployment_uri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                dataFile.getLogger().printException(String.format("URL decoding error for deployment uri <%s>", deployment_uri));
                chain.setInvalid();
                return chain;
            }
            dataFile.getLogger().println(String.format("Deployment <%s> specified for data acquisition <%s>", deployment_uri, str_uri));
        }

        if (str != null) {
            dataFile.setStudyUri(str.getStudy().getUri());
            // TODO
            //dataFile.setDatasetUri(DataFactory.getNextDatasetURI(str.getUri()));
            str.addDatasetUri(dataFile.getDatasetUri());

            SDD schema = SDD.find(str.getSchemaUri());
            if (schema == null) {
                dataFile.getLogger().printExceptionByIdWithArgs("DA_00007", str.getSchemaUri());
                chain.setInvalid();
                return chain;
            }

            if (!str.hasCellScope()) {
            	// Need to be fixed here by getting codeMap and codebook from sparql query
            	DASOInstanceGenerator dasoInstanceGen = new DASOInstanceGenerator(
            			dataFile, str, dataFile.getFileName());
            	chain.addGenerator(dasoInstanceGen);	
            	chain.addGenerator(new MeasurementGenerator(MeasurementGenerator.FILEMODE, dataFile, str, schema, dasoInstanceGen));
            } else {
                chain.addGenerator(new MeasurementGenerator(MeasurementGenerator.FILEMODE, dataFile, str, schema, null));
            }
            chain.setNamedGraphUri(URIUtils.replacePrefixEx(dataFile.getDataAcquisitionUri()));
        }

        return chain;
    }
    */

    public static boolean nameSpaceGen(DataFile dataFile, Map<String, String> mapCatalog, String templateFile) {
        RecordFile nameSpaceRecordFile = null;
        if (mapCatalog.get("hasDependencies") != null) {
            System.out.print("Extracting NameSpace sheet from spreadsheet... ");
            nameSpaceRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), dataFile.getFilename(), mapCatalog.get("hasDependencies"));
            if (nameSpaceRecordFile == null) {
                System.out.println("[WARNING] NameSpaceGenerator: nameSpaceRecordFile is NULL.");
            } else if (nameSpaceRecordFile.getRecords() == null) {
                System.out.println("[WARNING] NameSpaceGenerator: nameSpaceRecordFile.getRecords() is NULL.");
            } else {
                System.out.println("nameSpaceRecordFile has [" + nameSpaceRecordFile.getRecords().size() + "] rows");
                dataFile.setRecordFile(nameSpaceRecordFile);

                GeneratorChain chain = new GeneratorChain();
                chain.setNamedGraphUri(dataFile.getUri());
                chain.addGenerator(new NameSpaceGenerator(dataFile,templateFile));
                boolean isSuccess = false;
                if (chain != null) {
                    isSuccess = chain.generate();
                }
                if (isSuccess) {                                
                    System.out.println("Done extracting NameSpace sheet. ");
                } else {
                    System.out.println("Failed to extract NameSpace sheet. ");
                }
                return isSuccess;
            }
        } else {
            System.out.println("[WARNING] NameSpaceGenerator: could not find any sheet inside of Metadata Template called [hasDependencies].");
        }
        return false;
    }

}
