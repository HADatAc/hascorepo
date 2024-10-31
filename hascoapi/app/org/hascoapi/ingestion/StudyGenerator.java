package org.hascoapi.ingestion;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.HASCO;

public class StudyGenerator extends BaseGenerator {

    private String fileName;

    private String piUri;

    private String institutionUri;

    private String cpi1Uri;

    private String cpi2Uri;

    private String contactUri;

    public StudyGenerator(DataFile dataFile, String studyUri, String templateFile) {
        super(dataFile, studyUri, templateFile);
        this.fileName = dataFile.getFilename(); 
    }

    @Override
    public void initMapping() {
        //System.out.println("initMapping of StudyGenerator");
        try {
        mapCol.clear();
        mapCol.put("studyID", templates.getSTUDYID());
        mapCol.put("studyTitle", templates.getSTUDYTITLE());
        mapCol.put("studyAims", templates.getSTUDYAIMS());
        mapCol.put("studySignificance", templates.getSTUDYSIGNIFICANCE());
        mapCol.put("numSubjects", templates.getNUMSUBJECTS());
        mapCol.put("numSamples", templates.getNUMSAMPLES());
        mapCol.put("institution", templates.getINSTITUTION());
        mapCol.put("PI", templates.getPI());
        mapCol.put("PIAddress", templates.getPIADDRESS());
        mapCol.put("PICity", templates.getPICITY());
        mapCol.put("PIState", templates.getPISTATE());
        mapCol.put("PIZipCode", templates.getPIZIPCODE());
        mapCol.put("PIEmail", templates.getPIEMAIL());
        mapCol.put("PIPhone", templates.getPIPHONE());
        mapCol.put("CPI1FName", templates.getCPI1FNAME());
        mapCol.put("CPI1LName", templates.getCPI1LNAME());
        mapCol.put("CPI1Email", templates.getCPI1EMAIL());
        mapCol.put("CPI2FName", templates.getCPI2FNAME());
        mapCol.put("CPI2LName", templates.getCPI2LNAME());
        mapCol.put("CPI2Email", templates.getCPI2EMAIL());
        mapCol.put("contactFName", templates.getCONTACTFNAME());
        mapCol.put("contactLName", templates.getCONTACTLNAME());
        mapCol.put("contactEmail", templates.getCONTACTEMAIL());
        mapCol.put("createdDate", templates.getCREATEDDATE());
        mapCol.put("updatedDate", templates.getUPDATEDDATE());
        mapCol.put("DCAccessBool", templates.getDCACCESSBOOL());
        mapCol.put("externalSource", templates.getEXTSRC());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("end initMapping");
    }

    private String getId(Record rec) {
        //System.out.println("Value of mapCol.get(\"studyID\"): " + mapCol.get("studyID"));
        //System.out.println("Value of getValueByColumnName(): " + rec.getValueByColumnName(mapCol.get("studyID")));
        return rec.getValueByColumnName(mapCol.get("studyID"));
    }

    private String getUri() {
        return this.getDataFile().getUri().replace("DF","ST");
    }

    private String getType() {
        return "hasco:Study";
    }

    private String getTitle(Record rec) {
        return rec.getValueByColumnName(mapCol.get("studyTitle"));
    }

    private String getAims(Record rec) {
        return rec.getValueByColumnName(mapCol.get("studyAims"));
    }

    private String getSignificance(Record rec) {
        return rec.getValueByColumnName(mapCol.get("studySignificance"));
    }

    //private String getInstitutionUri(Record rec) {
    //    return kbPrefix + "/OR" + rec.getValueByColumnName(mapCol.get("institution")).replaceAll(" ", "-").replaceAll(",", "").replaceAll("'", ""); 
    //}

    //private String getAgentUri(Record rec) {
    //    return kbPrefix + "/PS" + rec.getValueByColumnName(mapCol.get("PI")).replaceAll(" ", "-"); 
    //}

    private String getExtSource(Record rec) {
        return rec.getValueByColumnName(mapCol.get("externalSource")); 
    }

    @Override
    public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {
        String uri = getUri();
        String id = getId(rec);
        String title = getTitle(rec);
        if (uri == null || uri.isEmpty()) {
            //throw new Exception("[ERROR] StudyGenerator: No URI value has been found");
            System.out.println("[ERROR] StudyGenerator: No URI value has been found");
            return null;
        }
        if (id == null || id.isEmpty()) {
            //throw new Exception("[ERROR] StudyGenerator: No ID value has been found");
            System.out.println("[ERROR] StudyGenerator: No ID value has been found");
            return null;
        }
        if (title == null || title.isEmpty()) {
            //throw new Exception("[ERROR] StudyGenerator: No TITLE value has been found");
            System.out.println("[ERROR] StudyGenerator: No TITLE value has been found");
            return null;
        }
        //System.out.println("Inside of StudyGenerator.createRow()");
        Map<String, Object> row = new HashMap<String, Object>();
        if (getUri().length() > 0) {
            //System.out.println("getUri()=[" + uri + "] getID()=[" + id + "] getTitle()=[" + title + "]");
            row.put("hasco:hasId", id);
            row.put("hasURI", uri);
            row.put("a", getType());
            row.put("hasco:hascoType", HASCO.STUDY);
            row.put("rdfs:label", getId(rec));
            row.put("hasco:hasTitle", title);
            row.put("skos:definition", getAims(rec));
            row.put("rdfs:comment", getSignificance(rec));
            row.put("hasco:hasDataFile", dataFile.getUri());
            row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());
            if (piUri != null && !piUri.isEmpty()) {
                row.put("hasco:hasPI", piUri);
            }
            if (institutionUri != null && !institutionUri.isEmpty()) {
                row.put("hasco:hasInstitution", institutionUri);
            }
            if(mapCol.get("externalSource") != null && rec.getValueByColumnName(mapCol.get("externalSource")) != null && 
                    rec.getValueByColumnName(mapCol.get("externalSource")).length() > 0) {
                row.put("hasco:hasExternalSource", getExtSource(rec));
            }
            setStudyUri(URIUtils.replacePrefixEx(getUri()));
        }

        return row;
    }

    @Override
    public String getTableName() {
        return "Study";
    }

    @Override
    public void preprocessuris(Map<String,String> uris) throws Exception {
        this.piUri = uris.get("piUri");
		this.institutionUri = uris.get("piInstitutionUri");
		this.cpi1Uri = uris.get("cpi1Uri");
		this.cpi2Uri = uris.get("cpi2Uri");
		this.contactUri = uris.get("contactUri");
	}
	
    @Override
    public String getErrorMsg(Exception e) {
        return "Error in StudyGenerator: " + e.getMessage();
    }
}

