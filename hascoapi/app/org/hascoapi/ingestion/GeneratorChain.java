package org.hascoapi.ingestion;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.hascoapi.entity.pojo.DataFile;
//import org.hascoapi.entity.pojo.StudyObjectCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratorChain {

    private static final Logger log = LoggerFactory.getLogger(GeneratorChain.class);

    private List<BaseGenerator> chain = new ArrayList<BaseGenerator>();
    private Map<String,String> uris = new HashMap<String,String>();
    private DataFile dataFile = null;
    private DataFile codebookFile = null;
    private boolean bValid = true;
    private boolean pv = false;
    private String sddName = "";
    private String studyUri = "";
    private String namedGraphUri = "";

    public String getStudyUri() {
        return studyUri;
    }

    public void setStudyUri(String studyUri) {
        this.studyUri = studyUri;
    }
    
    public String getNamedGraphUri() {
        return namedGraphUri;
    }

    public void setNamedGraphUri(String namedGraphUri) {
        this.namedGraphUri = namedGraphUri;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public DataFile getCodebookFile() {
        return codebookFile;
    }

    public void setCodebookFile(DataFile codebookFile) {
        this.codebookFile = codebookFile;
    }

    public boolean isValid() {
        return bValid;
    }

    public void setInvalid() {
        bValid = false;
    }

    public boolean getPV() {
        return pv;
    }

    public void setPV(boolean pv) {
        this.pv = pv;
    }

    public String getSddName() {
        return sddName;
    }

    public void setSddName(String sddName) {
        this.sddName = sddName;
    }
    
    public void addGenerator(BaseGenerator generator) {
        chain.add(generator);
    }
    
    public boolean generate() {
        return generate(true);
    }

    public boolean generate(boolean bCommit) {
        if (!isValid()) {
            return false;
        }
        System.out.println("GeneratorChain: Executing [NORMAL] generator chain.");

        //int i = 0;
        //for (BaseGenerator generator : chain) {
        //	log.info("GeneratorChain: Position " + i++ + " has generator of type [" + generator.getClass(). getSimpleName() + "]");
        //}

        for (BaseGenerator generator : chain) {
            String elementType = generator.getElementType();
            if (elementType == null || elementType.isEmpty()) {
                elementType = "";
            } else {
                elementType = " (" + elementType + ")";
            }
        	System.out.println("GeneratorChain: Executing generator of type [" + generator.getClass().getSimpleName() + elementType + "]");
        	//System.out.println("GeneratorChain: Named Graph is [" + generator.getNamedGraphUri() + "]");
            try {
                //System.out.println("  - GenerationChain: PreProcess");
                generator.preprocess();
                generator.preprocessuris(uris);
                //System.out.println("  - GenerationChain: CreateRows");
                generator.createRows();
                //System.out.println("  - GenerationChain: CreateObjects");
                generator.createObjects();
                //System.out.println("  - GenerationChain:PostProcess");
                generator.postprocess();
                uris = generator.postprocessuris();
            } catch (Exception e) {
                System.out.println("[ERROR] GenerationChain: " + generator.getErrorMsg(e));
                e.printStackTrace();
                
                generator.getLogger().printException(generator.getErrorMsg(e));
                return false;
            }
        	System.out.println("GeneratorChain: Ended execution of generator of type [" + generator.getClass().getSimpleName() + elementType + "]");
        }
        
        if (!bCommit) {
            return true;
        }

        System.out.println("GeneratorChain: Starting commits.");

        // Commit if no errors occurred
        for (BaseGenerator generator : chain) {
        	System.out.println("GeneratorChain: Started commit of generator of type [" + generator.getClass().getSimpleName() + "]");
            if (!getNamedGraphUri().isEmpty()) {
                generator.setNamedGraphUri(getNamedGraphUri());
            }             
            if (!generator.getStudyUri().isEmpty()) {
                setStudyUri(generator.getStudyUri());
            }
            
            if (generator.getStudyUri().isEmpty() && !getStudyUri().isEmpty()) {
                generator.setStudyUri(getStudyUri());
            }
                        
            try {
                generator.commitRowsToTripleStore(generator.getRows());
                generator.commitObjectsToTripleStore(generator.getObjects());
            } catch (Exception e) {
                System.out.println(generator.getErrorMsg(e));
                e.printStackTrace();
                
                generator.getLogger().printException(generator.getErrorMsg(e));
                return false;
            }
        	System.out.println("GeneratorChain: Finished commit of generator of type [" + generator.getClass().getSimpleName() + "]");
        }

        for (BaseGenerator generator : chain) {
            if (!generator.getStudyUri().equals("")) {
                setStudyUri(generator.getStudyUri());
            }
        }

        postprocess();
        System.out.println("GeneratorChain: Ended [NORMAL] execution of generator chain.");

        return true;
    }

    public boolean generateImmediateCommit() {
        if (!isValid()) {
            return false;
        }
        System.out.println("GeneratorChain: Executing [IMMEDIATE COMMIT] generator chain.");

        for (BaseGenerator generator : chain) {
        	System.out.println("GeneratorChain: Executing generator of type [" + generator.getClass().getSimpleName() + "]");
            if (!getNamedGraphUri().isEmpty()) {
                generator.setNamedGraphUri(getNamedGraphUri());
            }             
            try {
                //System.out.println("  - GenerationChain: PreProcess");
                generator.preprocess();
                generator.preprocessuris(uris);
                //System.out.println("  - GenerationChain: CreateRows");
                generator.createRows();
                //System.out.println("  - GenerationChain: CreateObjects");
                generator.createObjects();
                //System.out.println("  - GenerationChain:PostProcess");
                generator.postprocess();
                uris = generator.postprocessuris();
                generator.commitRowsToTripleStore(generator.getRows());
                generator.commitObjectsToTripleStore(generator.getObjects());
            } catch (Exception e) {
                System.out.println("[ERROR] GenerationChain: " + generator.getErrorMsg(e));
                e.printStackTrace();                
                generator.getLogger().printException(generator.getErrorMsg(e));
                return false;
            }
        }
        postprocess();
        return true;
    }

    public void delete() {
        for (BaseGenerator generator : chain) {

            if (!getNamedGraphUri().isEmpty()) {
                generator.setNamedGraphUri(getNamedGraphUri());
                log.info("deleting ... and setting the graph names...");
            } else if (!generator.getStudyUri().isEmpty()) {
                generator.setNamedGraphUri(generator.getStudyUri());
                log.info("deleting ... and setting the graph names...");
            }

            try {

                generator.preprocess();
                generator.createRows();
                generator.createObjects();
                generator.postprocess();

                generator.deleteRowsFromTripleStore(generator.getRows());
                generator.deleteObjectsFromTripleStore(generator.getObjects());

            } catch (Exception e) {
                System.out.println(generator.getErrorMsg(e));
                e.printStackTrace();
                
                generator.getLogger().printException(e);
            }
        }
    }

    public void postprocess() {}
}
