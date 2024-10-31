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
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.DSG;
import org.hascoapi.utils.SPARQLUtils;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.utils.CollectionUtil;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.NameSpaces;

import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

public class IngestDSG {

    public static void exec(DSG dsg, DataFile dataFile, File file, String templateFile) {

        System.out.println("IngestDSG.exec(): Step 1 of 3: " + 
            "Processing file [" + dataFile.getFilename() + "] " + 
            "Last process time [" + dataFile.getLastProcessTime() + "]");
        String fileName = dataFile.getFilename();
        dataFile.getLogger().println(String.format("Processing file: %s", fileName));

        System.out.println("IngestDSG.exec() Step 2 of 3: Adding file content into RecordFile");

        // file is rejected if it has an invalid extension. Otherwise, it is added into RecordFile
        RecordFile recordFile = null;
        if (fileName.endsWith(".xlsx")) {
            recordFile = new SpreadsheetRecordFile(file);
        } else {
            dataFile.getLogger().printExceptionByIdWithArgs("GBL_00003", fileName);
            return;
        }
        dataFile.setRecordFile(recordFile);
        
        boolean bSucceed = false;
        GeneratorChain chain = null;

        System.out.println("IngestDSG.exec(): Step 3 of 3: Calling IngestDSG.buildChain()");
        if (fileName.startsWith("DSG-")) {
            chain = buildChain(dsg, dataFile, file, templateFile);           
        } 

        if (chain != null) {
            System.out.println("Executing generation chain...");
            //bSucceed = chain.generate();
            bSucceed = chain.generateImmediateCommit();
            System.out.println("Generation chain has been executed.");
        }

        if (bSucceed) {
            dataFile.setFileStatus(DataFile.PROCESSED);
            dataFile.setCompletionTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            dataFile.save();
            return;
        } 
        return;
    }
    
    /****************************
     *    DSG                   *
     ****************************/    
    
    public static GeneratorChain buildChain(DSG dsg, DataFile dataFile, File file, String templateFile) {
        System.out.println("IngestDSG.buildChain(): Processing DSG file ...");
        
        String fileName = dataFile.getFilename();

        RecordFile recordFile = new SpreadsheetRecordFile(file, "InfoSheet");
        if (!recordFile.isValid()) {
            dataFile.getLogger().printExceptionById("SDD_00001");
            return null;
        } 

        System.out.println("IngestDSG.buildChain(): Build chain 1 of 10 - Reading catalog and template");

        Map<String, String> mapCatalog = dsg.getCatalog();
        for (Record record : recordFile.getRecords()) {
            mapCatalog.put(record.getValueByColumnIndex(0), record.getValueByColumnIndex(1));
            System.out.println(record.getValueByColumnIndex(0) + ":" + record.getValueByColumnIndex(1));
        }

        // the template is needed to process individual sheets
        dsg.setTemplates(templateFile);

        System.out.println("IngestDSG.buildChain(): Build chain 2 of 10 - Separating sheets apart");

        try {
            System.out.println("hascoapi.templates.template_filename:" + templateFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecordFile stdRecordFile = null;
        DataFile stdFile = null;
        boolean stdOkay = false;

        if (mapCatalog.get("hasStudyDescription") != null) {
            System.out.print("Extracting STD sheet from spreadsheet... ");
            stdRecordFile = new SpreadsheetRecordFile(dataFile.getFile(), mapCatalog.get("hasStudyDescription"));
            System.out.println("SheetName: [" + stdRecordFile.getSheetName() + "]");
            try {
                stdFile = (DataFile)dataFile.clone();
                stdFile.setRecordFile(stdRecordFile);
                stdOkay = true;
            } catch (Exception e) {
                stdOkay = false;
            }
        }
        
        /* 
        if (stdOkay) { 
            GeneratorChain chain = new GeneratorChain();
            System.out.println("IngestDSG.exec(): Adding StudyGenerator.");
            chain.addGenerator(new StudyGenerator(stdFile, templateFile));
            System.out.println("IngestDSG.exec(): Adding AgentGenerator.");
            chain.addGenerator(new AgentGenerator(stdFile));
            System.out.println("IngestDSG.exec(): chain is BUILT.");
            chain.setNamedGraphUri(dsg.getUri());
            return chain;
        }
        */
        return null;
    }

}
