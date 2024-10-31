package org.hascoapi.ingestion;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.PostalAddress;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.NameSpace;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.utils.URIUtils;
import org.hascoapi.vocabularies.FOAF;
import org.hascoapi.vocabularies.VSTOI;
import org.hascoapi.vocabularies.SCHEMA;
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

public class PostalAddressGenerator extends BaseGenerator {

	private static final Logger log = LoggerFactory.getLogger(PostalAddressGenerator.class);
    private long timestamp;
	private String managerEmail;
	private List<String> addressKeyList;

	final String kbPrefix = ConfigProp.getKbPrefix();
	String startTime = "";
    protected IngestionLogger logger = null;

	public PostalAddressGenerator(DataFile dataFile, String templateFile, String managerEmail) {
		super(dataFile, null, templateFile);
		this.logger = dataFile.getLogger();
		this.managerEmail = managerEmail;
	}

	@Override
	public void initMapping() {

		System.out.println("Inside PostalAddressGenerator");

		// Get the current timestamp (in milliseconds)
        timestamp = System.currentTimeMillis();
		this.addressKeyList = new ArrayList<String>();

		// Create mapping
		mapCol.clear();
		mapCol.put("PostalAddressType", templates.getPostalAddressType());
		mapCol.put("PostalAddressStreet", templates.getPostalAddressStreet());
		mapCol.put("PostalAddressPostalCode", templates.getPostalAddressPostalCode());
		mapCol.put("PostalAddressLocality", templates.getPostalAddressLocality());
		mapCol.put("PostalAddressRegion", templates.getPostalAddressRegion());
		mapCol.put("PostalAddressCountry", templates.getPostalAddressCountry());
	}

    private String getPostalAddressType(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PostalAddressType"));
	}

    private String getPostalAddressStreet(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PostalAddressStreet"));
	}

	private String getPostalAddressPostalCode(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PostalAddressPostalCode"));
	}

	private String getPostalAddressLocality(Record rec) {
		String locality = rec.getValueByColumnName(mapCol.get("PostalAddressLocality"));
		Place place = Place.findSubclassByName(URIUtils.replacePrefixEx(SCHEMA.CITY), locality);
		if (place != null && place.getUri() != null) {
			return place.getUri();
		} else {
			System.out.println("[WARNING] PostalAddressGenerator: could not find following CITY [" + locality + "]");
		}
		return "";
	}

	private String getPostalAddressRegion(Record rec) {
		String state = rec.getValueByColumnName(mapCol.get("PostalAddressRegion"));
		Place place = Place.findSubclassByName(URIUtils.replacePrefixEx(SCHEMA.STATE), state);
		if (place != null && place.getUri() != null) {
			return place.getUri();
		} else {
			System.out.println("[WARNING] PostalAddressGenerator: could not find following STATE [" + state + "]");
		}
		return "";
	}

	private String getPostalAddressCountry(Record rec) {
		String country = rec.getValueByColumnName(mapCol.get("PostalAddressCountry"));
		Place place = Place.findSubclassByName(URIUtils.replacePrefixEx(SCHEMA.COUNTRY), country);
		if (place != null && place.getUri() != null) {
			return place.getUri();
		} else {
			System.out.println("[WARNING] PostalAddressGenerator: could not find following COUNTRY [" + country + "]");
		}
		return "";
	}

	public String createPostalAddressUri() throws Exception {

        // Generate a random integer between 10000 and 99999
        Random random = new Random();
        int randomNumber = random.nextInt(99999 - 10000 + 1) + 10000;

		return kbPrefix + "/" + Constants.PREFIX_POSTAL_ADDRESS + timestamp + randomNumber;
	}

	@Override
	public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {	

		String street = getPostalAddressStreet(rec).trim();
		String postalCode = getPostalAddressPostalCode(rec).trim();
		String addressKey = street + "|" + postalCode;

		if (street == null || street.isEmpty() || postalCode == null || postalCode.isEmpty()) {
			System.out.println("[WARNING] PostalAddressGenerator: PostalAddress for rowNumber=[" + rowNumber + "] has either street or postalcode null or empty.");
			return null;
		}

		PostalAddress postalAddress = PostalAddress.findByAddress(street, postalCode);
		if (postalAddress != null) {
			System.out.println("[WARNING] PostalAddressGenerator: PostalAddress with street=[" + street + "] and postalCode=[" + postalCode + "] already exist in triplestore.");
			return null;
		}

		if (addressKeyList.contains(addressKey)) {
			System.out.println("[WARNING] PostalAddressGenerator: PostalAddress with street=[" + street + "] and postalCode=[" + postalCode + "] already exist in source file.");
			return null;
		} else {
			addressKeyList.add(addressKey);
		}

		Map<String, Object> row = new HashMap<String, Object>();
		row.put("hasURI", createPostalAddressUri());
		row.put("hasco:hascoType", SCHEMA.POSTAL_ADDRESS);
		row.put("a", getPostalAddressType(rec));		
		row.put("rdfs:label", street + " " + postalCode);
		row.put("schema:streetAddress", street);
		row.put("schema:postalCode", postalCode);
		row.put("schema:addressLocality", getPostalAddressLocality(rec));
		row.put("schema:addressRegion", getPostalAddressRegion(rec));
		row.put("schema:addressCountry", getPostalAddressCountry(rec));
		row.put("vstoi:hasSIRManagerEmail", managerEmail);
		return row;
	}

	@Override
	public String getTableName() {
		return "PostalAddress";
	}

	@Override
	public String getErrorMsg(Exception e) {
		return "Error in PostalAddressGenerator: " + e.getMessage();
	}
 	 
}
