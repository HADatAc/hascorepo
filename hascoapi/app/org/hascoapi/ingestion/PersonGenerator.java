package org.hascoapi.ingestion;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Organization;
import org.hascoapi.entity.pojo.PostalAddress;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.NameSpace;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.utils.ConfigProp;
import org.hascoapi.utils.IngestionLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.String;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Date;
import java.util.Collections;
import java.util.Base64;
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  

public class PersonGenerator extends BaseGenerator {

	private static final Logger log = LoggerFactory.getLogger(PVGenerator.class);
    private long timestamp;
	private String managerEmail;
	final String kbPrefix = ConfigProp.getKbPrefix();
	String startTime = "";
    protected IngestionLogger logger = null;

	public PersonGenerator(DataFile dataFile, String templateFile, String managerEmail) {
		super(dataFile, null, templateFile);
		this.logger = dataFile.getLogger();
		this.managerEmail = managerEmail;
	}

	@Override
	public void initMapping() {

		System.out.println("Inside PersonGenerator");

		// Get the current timestamp (in milliseconds)
        timestamp = System.currentTimeMillis();

		// Create mapping
		mapCol.clear();
		mapCol.put("OriginalID", templates.getAgentOriginalID());
		mapCol.put("Type", templates.getAgentType());
		mapCol.put("GivenName", templates.getAgentGivenName());
		mapCol.put("FamilyName", templates.getAgentFamilyName());
		mapCol.put("Email", templates.getAgentEmail());
		mapCol.put("Telephone", templates.getAgentTelephone());
		mapCol.put("Url", templates.getAgentUrl());
		mapCol.put("JobTitle", templates.getAgentJobTitle());
		mapCol.put("Affiliation", templates.getAgentHasAffiliationUri());
		mapCol.put("Address", templates.getAgentAddress());
	}

    private String getOriginalID(Record rec) {
		return rec.getValueByColumnName(mapCol.get("OriginalID"));
	}

	private String getType(Record rec) {
		String cls = rec.getValueByColumnName(mapCol.get("Type"));
		if (cls.length() > 0) {
			if (URIUtils.isValidURI(cls)) {
				return cls;
			} else {
				System.out.println("[WARNING] The following URI is considered invalid: " + cls);
			}
		} 
		return "";
	}

	private String getGivenName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("GivenName"));
	}

	private String getFamilyName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("FamilyName"));
	}

	private String getEmail(Record rec) {
		return rec.getValueByColumnName(mapCol.get("Email"));
	}

	private String getTelephone(Record rec) {
		return rec.getValueByColumnName(mapCol.get("Telephone"));
	}

	private String getUrl(Record rec) {
		return rec.getValueByColumnName(mapCol.get("Url"));
	}

	private String getJobTitle(Record rec) {
		return rec.getValueByColumnName(mapCol.get("JobTitle"));
	}

	private String getHasAffiliationUri(Record rec) {
		String orgName = rec.getValueByColumnName(mapCol.get("Affiliation"));
		Organization organization = Organization.findByName(orgName);
		if (organization != null && organization.getUri() != null) {
			return organization.getUri();
		}
		return "";
	}

	private String getAddress(Record rec) {
		String addressKey = rec.getValueByColumnName(mapCol.get("Address"));
		if (addressKey != null && !addressKey.isEmpty()) {
			String[] parts = addressKey.split("\\|", 2);
			PostalAddress postalAddress = PostalAddress.findByAddress(parts[0], parts[1]);
			if (postalAddress != null && postalAddress.getUri() != null) {
				return postalAddress.getUri();
			}
		}
		return "";
	}

	public String createPersonUri() throws Exception {

        // Generate a random integer between 10000 and 99999
        Random random = new Random();
        int randomNumber = random.nextInt(99999 - 10000 + 1) + 10000;

		return kbPrefix + "/" + Constants.PREFIX_PERSON + timestamp + randomNumber;
	}

	@Override
	public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {	
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("hasURI", createPersonUri());
		row.put("hasco:hasOriginalID", URIUtils.replaceNameSpaceEx(getOriginalID(rec)));
		row.put("hasco:hascoType", FOAF.PERSON);
		row.put("a", URIUtils.replaceNameSpaceEx(getType(rec)));		
		row.put("rdfs:label", getGivenName(rec) + " " + getFamilyName(rec));
		row.put("foaf:name", getGivenName(rec) + " " + getFamilyName(rec));
		row.put("foaf:givenName", getGivenName(rec));
		row.put("foaf:familyName", getFamilyName(rec));
		row.put("foaf:mbox", getEmail(rec));
		row.put("schema:telephone", getTelephone(rec));
		row.put("schema:url", getUrl(rec));
		row.put("schema:jobTitle", getJobTitle(rec));
		row.put("foaf:member", getHasAffiliationUri(rec));
		row.put("schema:address", getAddress(rec));
		row.put("vstoi:hasSIRManagerEmail", managerEmail);
		return row;
	}

	@Override
	public String getTableName() {
		return "Person";
	}

	@Override
	public String getErrorMsg(Exception e) {
		return "Error in PersonGenerator: " + e.getMessage();
	}
 	 
}
