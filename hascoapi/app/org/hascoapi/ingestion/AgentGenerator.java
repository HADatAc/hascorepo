package org.hascoapi.ingestion;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.utils.Utils;
import org.hascoapi.vocabularies.FOAF;

public class AgentGenerator extends BaseGenerator {

	private int counter = 1; //starting index number

    private String piUri;

	private String piInstitutionUri;

    private String cpi1Uri;

	private String cpi1InstitutionUri;

    private String cpi2Uri;

	private String cpi2InstitutionUri;

    private String contactUri;

    public String createPIUri() {
		if (this.piUri == null) {
			this.piUri = Utils.uriGen("person");
		}
        return this.piUri;
    }

    public String getPIUri() {
        return this.piUri;
    }

	public String createPIInstitutionUri() {
		if (this.piInstitutionUri == null) {
			this.piInstitutionUri = Utils.uriGen("organization");
		}
        return this.piInstitutionUri;
    }

	public String getPiInstitutionUri() {
        return this.piInstitutionUri;
    }

    public String createCPI1Uri() {
		if (this.cpi1Uri == null) {
			this.cpi1Uri = Utils.uriGen("person");
		}
        return this.cpi1Uri;
    }

    public String getCPI1Uri() {
        return this.cpi1Uri;
    }

    public String createCPI1InstitutionUri() {
		if (this.cpi1InstitutionUri == null) {
			this.cpi1InstitutionUri = Utils.uriGen("organization");
		}
        return this.cpi1InstitutionUri;
    }

    public String getCPI1InstitutionUri() {
        return this.cpi1InstitutionUri;
    }

    public String createCPI2Uri() {
		if (this.cpi2Uri == null) {
			this.cpi2Uri = Utils.uriGen("person");
		}
        return this.cpi2Uri;
    }

    public String getCPI2Uri() {
        return this.cpi2Uri;
    }

    public String createCPI2InstitutionUri() {
		if (this.cpi2InstitutionUri == null) {
			this.cpi2InstitutionUri = Utils.uriGen("organization");
		}
        return this.cpi2InstitutionUri;
    }

    public String getCPI2InstitutionUri() {
        return this.cpi2InstitutionUri;
    }

    public String createContactUri() {
		if (this.contactUri == null) {
			this.contactUri = Utils.uriGen("person");
		}
        return this.contactUri;
    }

    public String getContactUri() {
        return this.contactUri;
    }

    public AgentGenerator(DataFile dataFile, String studyUri, String templateFile) {
        super(dataFile, studyUri, templateFile);
        this.fileName = dataFile.getFilename();
		piUri = null;
		piInstitutionUri = null;
		cpi1Uri = null;
		cpi1InstitutionUri = null;
		cpi2Uri = null;
		cpi2InstitutionUri = null;
		contactUri = null;
	}
	
	@Override
	public void initMapping() {
		try {
			//System.out.println("initMapping of AgentGenerator");
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
			mapCol.put("CPI1institution", templates.getCPI1INSTITUTION());
			mapCol.put("CPI1Address", templates.getCPI1ADDRESS());
			mapCol.put("CPI1City", templates.getCPI1CITY());
			mapCol.put("CPI1State", templates.getCPI1STATE());
			mapCol.put("CPI1ZipCode", templates.getCPI1ZIPCODE());
			mapCol.put("CPI2FName", templates.getCPI2FNAME());
			mapCol.put("CPI2LName", templates.getCPI2LNAME());
			mapCol.put("CPI2Email", templates.getCPI2EMAIL());
			mapCol.put("CPI2institution", templates.getCPI2INSTITUTION());
			mapCol.put("CPI2Address", templates.getCPI2ADDRESS());
			mapCol.put("CPI2City", templates.getCPI2CITY());
			mapCol.put("CPI2State", templates.getCPI2STATE());
			mapCol.put("CPI2ZipCode", templates.getCPI2ZIPCODE());
			mapCol.put("contactFName", templates.getCONTACTFNAME());
			mapCol.put("contactLName", templates.getCONTACTLNAME());
			mapCol.put("contactEmail", templates.getCONTACTEMAIL());
			mapCol.put("createdDate", templates.getCREATEDDATE());
			mapCol.put("updatedDate", templates.getUPDATEDDATE());
			mapCol.put("DCAccessBool", templates.getDCACCESSBOOL());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("ended initMapping of AgentGenerator");
	}
	
	private String getInstitutionName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("institution"));
	}
	
	private String getPIFullName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PI"));
	}
	
	private String getPIGivenName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PI")).substring(0, getPIFullName(rec).indexOf(' '));
	}
	
	private String getPIFamilyName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PI")).substring(getPIFullName(rec).indexOf(' ') + 1);
	}
	
	private String getPIMBox(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PIEmail"));
	}
	
	private String getCPI1FullName(Record rec) {
		if ((rec.getValueByColumnName(mapCol.get("CPI1FName")) == null || 
		     rec.getValueByColumnName(mapCol.get("CPI1FName")).isEmpty()) &&
			(rec.getValueByColumnName(mapCol.get("CPI1LName")) == null || 
		     rec.getValueByColumnName(mapCol.get("CPI1LName")).isEmpty())) {
			return null;
		}		
		return rec.getValueByColumnName(mapCol.get("CPI1FName")) + " " + 
			   rec.getValueByColumnName(mapCol.get("CPI1LName"));
	}
		
	private String getCPI1GivenName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI1FName"));
	}
	
	private String getCPI1FamilyName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI1LName"));
	}
	
	private String getCPI1MBox(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI1Email"));
	}
    
	private String getCPI1InstitutionName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI1institution"));
	}
	
	private String getCPI2FullName(Record rec) {
		if ((rec.getValueByColumnName(mapCol.get("CPI2FName")) == null || 
		     rec.getValueByColumnName(mapCol.get("CPI2FName")).isEmpty()) &&
			(rec.getValueByColumnName(mapCol.get("CPI2LName")) == null || 
		     rec.getValueByColumnName(mapCol.get("CPI2LName")).isEmpty())) {
			return null;
		}		
		return rec.getValueByColumnName(mapCol.get("CPI2FName")) + " " + 
			   rec.getValueByColumnName(mapCol.get("CPI2LName"));
	}
	
	private String getCPI2GivenName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI2FName"));
	}
	
	private String getCPI2FamilyName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI2LName"));
	}
	
	private String getCPI2MBox(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI2Email"));
	}
	
	private String getContactFullName(Record rec) {
		if ((rec.getValueByColumnName(mapCol.get("contactFName")) == null || 
		     rec.getValueByColumnName(mapCol.get("contactFName")).isEmpty()) &&
			(rec.getValueByColumnName(mapCol.get("contactLName")) == null || 
		     rec.getValueByColumnName(mapCol.get("contactLName")).isEmpty())) {
			return null;
		}		
		return rec.getValueByColumnName(mapCol.get("contactFName")) + " " + 
			   rec.getValueByColumnName(mapCol.get("contactLName"));
	}
	
	private String getContactGivenName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("contactFName"));
	}
	
	private String getContactFamilyName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("contactLName"));
	}
	
	private String getContactMBox(Record rec) {
		return rec.getValueByColumnName(mapCol.get("contactEmail"));
	}
	
	private String getCPI2InstitutionName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("CPI2institution"));
	}
	
    public Map<String, Object> createPIRow(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getPIFullName(rec) != null && !getPIFullName(rec).isEmpty()) {
			row.put("hasURI", createPIUri());
			row.put("a", FOAF.PERSON);
			row.put("hasco:hascoType", FOAF.PERSON);
			row.put("rdfs:label", getPIFullName(rec));
			row.put("foaf:name", getPIFullName(rec));
			row.put("rdfs:comment", "PI from " + getInstitutionName(rec));
			row.put("foaf:familyName", getPIFamilyName(rec));
			row.put("foaf:givenName", getPIGivenName(rec));
			row.put("foaf:mbox", getPIMBox(rec));
			if (getInstitutionName(rec) != null && !getInstitutionName(rec).isEmpty()) {
				row.put("foaf:member", createPIInstitutionUri());
			}
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	return row;
    }
    
    public Map<String, Object> createCPI1Row(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getCPI1FullName(rec) != null && !getCPI1FullName(rec).isEmpty()) {
			row.put("hasURI", createCPI1Uri());
			row.put("a", FOAF.PERSON);
			row.put("hasco:hascoType", FOAF.PERSON);
			row.put("rdfs:label", getCPI1FullName(rec));
			row.put("foaf:name", getCPI1FullName(rec));
			row.put("rdfs:comment", "Co-PI from " + getInstitutionName(rec));
			row.put("foaf:familyName", getCPI1FamilyName(rec));
			row.put("foaf:givenName", getCPI1GivenName(rec));
			row.put("foaf:mbox", getCPI1MBox(rec));
			if (getCPI1InstitutionName(rec) != null && !getCPI1InstitutionName(rec).isEmpty()) {
				row.put("foaf:member", createCPI1InstitutionUri());
			}
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	
    	return row;
    }

    public Map<String, Object> createCPI2Row(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getCPI2FullName(rec) != null && !getCPI2FullName(rec).isEmpty()) {
			row.put("hasURI", createCPI2Uri());
			row.put("a", FOAF.PERSON);
			row.put("hasco:hascoType", FOAF.PERSON);
			row.put("rdfs:label", getCPI2FullName(rec));
			row.put("foaf:name", getCPI2FullName(rec));
			row.put("rdfs:comment", "Co-PI from " + getInstitutionName(rec));
			row.put("foaf:familyName", getCPI2FamilyName(rec));
			row.put("foaf:givenName", getCPI2GivenName(rec));
			row.put("foaf:mbox", getCPI2MBox(rec));
			if (getCPI2InstitutionName(rec) != null && !getCPI2InstitutionName(rec).isEmpty()) {
				row.put("foaf:member", createCPI2InstitutionUri());
			}
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	
    	return row;
    }

    public Map<String, Object> createContactRow(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getContactFullName(rec) != null && !getContactFullName(rec).isEmpty()) {
			row.put("hasURI", createContactUri());
			row.put("a", FOAF.PERSON);
			row.put("hasco:hascoType", FOAF.PERSON);
			row.put("rdfs:label", getContactFullName(rec));
			row.put("foaf:name", getContactFullName(rec));
			row.put("rdfs:comment", "Co-PI from " + getInstitutionName(rec));
			row.put("foaf:familyName", getContactFamilyName(rec));
			row.put("foaf:givenName", getContactGivenName(rec));
			row.put("foaf:mbox", getContactMBox(rec));
			if (getInstitutionName(rec) != null && !getInstitutionName(rec).isEmpty()) {
				row.put("foaf:member", createPIInstitutionUri());
			}
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	
    	return row;
    }

    public Map<String, Object> createInstitutionRow(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getInstitutionName(rec) != null && !getInstitutionName(rec).isEmpty()) {
			row.put("hasURI", createPIInstitutionUri());
			row.put("a", FOAF.ORGANIZATION);
			row.put("hasco:hascoType", FOAF.ORGANIZATION);
			row.put("rdfs:label", getInstitutionName(rec));
			row.put("foaf:name", getInstitutionName(rec));
			row.put("rdfs:comment", getInstitutionName(rec) + " Institution");
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	
    	return row;
    }
    
    public Map<String, Object> createCPI1InstitutionRow(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getCPI1InstitutionName(rec) != null && !getCPI1InstitutionName(rec).isEmpty()) {
			row.put("hasURI", createCPI1InstitutionUri());
			row.put("a", FOAF.ORGANIZATION);
			row.put("hasco:hascoType", FOAF.ORGANIZATION);
			row.put("rdfs:label", getCPI1InstitutionName(rec));
			row.put("foaf:name", getCPI1InstitutionName(rec));
			row.put("rdfs:comment", getCPI1InstitutionName(rec) + " Institution");
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	
    	return row;
    }
    
    public Map<String, Object> createCPI2InstitutionRow(Record rec) {
    	Map<String, Object> row = new HashMap<String, Object>();
		if (getCPI2InstitutionName(rec) != null && !getCPI2InstitutionName(rec).isEmpty()) {
			row.put("hasURI", createCPI2InstitutionUri());
			row.put("a", FOAF.ORGANIZATION);
			row.put("hasco:hascoType", FOAF.ORGANIZATION);
			row.put("rdfs:label", getCPI2InstitutionName(rec));
			row.put("foaf:name", getCPI2InstitutionName(rec));
			row.put("rdfs:comment", getCPI2InstitutionName(rec) + " Institution");
			row.put("vstoi:hasSIRManagerEmail", this.getDataFile().getHasSIRManagerEmail());
			counter++;
		}
    	
    	return row;
    }
    
	@Override    
    public void createRows() throws Exception {
		System.out.println("Begining createRows in AgentGenerator");
		boolean duplicate=false;
    	rows.clear();
    	// Currently using an inefficient way to check if row already exists in the list of rows; This should be addressed in the future
		try {
			if (records != null) {
				for (Record record : records) {
					if(getPIFullName(record) != null && !getPIFullName(record).isEmpty()) {
						System.out.println("Creating PI Row:" + getPIFullName(record) + ":");
						duplicate=false;
						for (Map<String, Object> row : rows) {
							if(row.get("foaf:name").equals(getPIFullName(record))) {
								System.out.println("Found Duplicate: " + getPIFullName(record));
								duplicate=true;
								break;
							}
						}
						if(!duplicate){
							System.out.println("Didn't Find Duplicate, adding PI " + getPIFullName(record) + " row to list of rows.");
							rows.add(createPIRow(record));
						}
					}
					if(getCPI1FullName(record) != null && !getCPI1FullName(record).isEmpty()) {
						System.out.println("Creating CPI1 Row:" + getCPI1FullName(record) + ":");
						duplicate=false;
						for (Map<String, Object> row : rows){
							if(row.get("foaf:name").equals(getCPI1FullName(record))){
								System.out.println("Found Duplicate: " + getCPI1FullName(record));
								duplicate=true;
								break;
							}
						}
						if(!duplicate){
							System.out.println("Didn't Find Duplicate, adding CPI1 " + getCPI1FullName(record) + " row to list of rows.");
							rows.add(createCPI1Row(record));
						}
					}
					if(getCPI2FullName(record) != null && !getCPI2FullName(record).isEmpty()) {
						System.out.println("Creating CPI2 Row:" + getCPI2FullName(record) + ":");
						duplicate=false;
						for (Map<String, Object> row : rows){
							if(row.get("foaf:name").equals(getCPI2FullName(record))){
								System.out.println("Found Duplicate: " + getCPI2FullName(record));
								duplicate=true;
								break;
							}
						}
						if(!duplicate){
							System.out.println("Didn't Find Duplicate, adding CPI2 " + getCPI2FullName(record) + " row to list of rows.");
							rows.add(createCPI2Row(record));
						}
					}
					if(getContactFullName(record) != null && !getContactFullName(record).isEmpty()) {
						System.out.println("Creating Contact Row:" + getContactFullName(record) + ":");
						duplicate=false;
						for (Map<String, Object> row : rows){
							if(row.get("foaf:name").equals(getContactFullName(record))){
								System.out.println("Found Duplicate: " + getContactFullName(record));
								duplicate=true;
								break;
							}
						}
						if(!duplicate){
							System.out.println("Didn't Find Duplicate, adding Contact " + getContactFullName(record) + " row to list of rows.");
							rows.add(createContactRow(record));
						}
					}
					if(getInstitutionName(record) != null && !getInstitutionName(record).isEmpty()) {
						System.out.println("Creating Institution Row:" + getInstitutionName(record) + ":");
						duplicate=false;
						for (Map<String, Object> row : rows){
							if(row.get("foaf:name").equals(getInstitutionName(record))){
								System.out.println("Found Duplicate: " + getInstitutionName(record));
								duplicate=true;
								break;
							}
						}
						if(!duplicate){
							System.out.println("Didn't Find Duplicate, adding Contact " + getInstitutionName(record) + " row to list of rows.");
							rows.add(createInstitutionRow(record));
						}
					}
					if(getCPI1InstitutionName(record) != null && !getCPI1InstitutionName(record).isEmpty()) {
						System.out.println("Creating CPI1 Institution Row:" + getCPI1InstitutionName(record) + ":");
						duplicate=false;
						for (Map<String, Object> row : rows){
							if(row.get("foaf:name").equals(getCPI1InstitutionName(record))){
								System.out.println("Found Duplicate: " + getCPI1InstitutionName(record));
								duplicate=true;
								break;
							}
						}
						if(!duplicate){
							System.out.println("Didn't Find Duplicate, adding Contact " + getCPI1InstitutionName(record) + " row to list of rows.");
							rows.add(createCPI1InstitutionRow(record));
						}
					}
				}  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("AgentGenerator.createRows() has created " + counter + " rows.");		
		System.out.println("Ending createRows in AgentGenerator");		
    }

	@Override
    public void postprocess() throws Exception {
		//System.out.println("Values at AgentGenerator.postprocess():");
		//System.out.println("   - piUri = [" + piUri + "]");
		//System.out.println("   - piInstitutionUri = [" + piInstitutionUri + "]");
		//System.out.println("   - cpi1Uri = [" + cpi1Uri + "]");
		//System.out.println("   - cpi1InstitutionUri = [" + cpi1InstitutionUri + "]");
		//System.out.println("   - cpi2Uri = [" + cpi2Uri + "]");
		//System.out.println("   - cpi2InstitutionUri = [" + cpi2InstitutionUri + "]");
		//System.out.println("   - contactUri = [" + contactUri + "]");
	}
	
	@Override
    public Map<String,String> postprocessuris() throws Exception {
		Map<String,String> uris = new HashMap<String,String>();
		uris.put("piUri",piUri);
		uris.put("piInstitutionUri",piInstitutionUri);
		uris.put("cpi1Uri",cpi1Uri);
		uris.put("cpi2Uri",cpi2Uri);
		uris.put("contactUri",contactUri);
		return uris;
	}
	
	@Override
	public String getTableName() {
		return "Agent";
	}
	
	@Override
    public String getErrorMsg(Exception e) {
        return "Error in AgentGenerator: " + e.getMessage();
    }

}