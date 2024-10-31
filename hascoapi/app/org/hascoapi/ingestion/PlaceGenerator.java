package org.hascoapi.ingestion;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.annotations.PropertyField;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.NameSpace;
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

public class PlaceGenerator extends BaseGenerator {

	private static final Logger log = LoggerFactory.getLogger(PlaceGenerator.class);
    private long timestamp;
	private String managerEmail;
	final String kbPrefix = ConfigProp.getKbPrefix();
	String startTime = "";
    protected IngestionLogger logger = null;

	public PlaceGenerator(DataFile dataFile, String templateFile, String managerEmail) {
		super(dataFile, null, templateFile);
		this.logger = dataFile.getLogger();
		this.managerEmail = managerEmail;
	}

	@Override
	public void initMapping() {

		System.out.println("Inside PlaceGenerator");

		// Get the current timestamp (in milliseconds)
        timestamp = System.currentTimeMillis();

		// Create mapping
		mapCol.clear();
		mapCol.put("OriginalID", templates.getPlaceOriginalID());
		mapCol.put("Type", templates.getPlaceType());
		mapCol.put("PlaceName", templates.getPlaceName());
		mapCol.put("PlaceImage", templates.getPlaceImage());
		mapCol.put("PlaceContainedIn", templates.getPlaceContainedIn());
		mapCol.put("PlaceIdentifier", templates.getPlaceIdentifier());
		mapCol.put("PlaceGeo", templates.getPlaceGeo());
		mapCol.put("PlaceLatitude", templates.getPlaceLatitude());
		mapCol.put("PlaceLongitude", templates.getPlaceLongitude());
		mapCol.put("PlaceUrl", templates.getPlaceUrl());
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

	private String getPlaceName(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceName"));
	}

	private String getPlaceImage(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceImage"));
	}

	private String getPlaceContainedIn(Record rec) {
		String containedInPlaceName = rec.getValueByColumnName(mapCol.get("PlaceContainedIn"));
		Place place = Place.findByName(containedInPlaceName);
		if (place != null && place.getUri() != null) {
			return place.getUri();
		}
		return "";
	}

	private String getPlaceIdentifier(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceIdentifier"));
	}

	private String getPlaceGeo(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceGeo"));
	}

	private String getPlaceLatitude(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceLatitude"));
	}

	private String getPlaceLongitude(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceLongitude"));
	}

	private String getPlaceUrl(Record rec) {
		return rec.getValueByColumnName(mapCol.get("PlaceUrl"));
	}

	public String createPlaceUri() throws Exception {

        // Generate a random integer between 10000 and 99999
        Random random = new Random();
        int randomNumber = random.nextInt(99999 - 10000 + 1) + 10000;

		return kbPrefix + "/" + Constants.PREFIX_PLACE + timestamp + randomNumber;
	}

	@Override
	public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {	
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("hasURI", createPlaceUri());
		row.put("hasco:hasOriginalID", URIUtils.replaceNameSpaceEx(getOriginalID(rec)));
		row.put("hasco:hascoType", SCHEMA.PLACE);
		row.put("a", URIUtils.replaceNameSpaceEx(getType(rec)));		
		row.put("rdfs:label", getPlaceName(rec));
		row.put("foaf:name", getPlaceName(rec));
		row.put("hasco:hasImage", getPlaceImage(rec));
		row.put("schema:containedInPlace", getPlaceContainedIn(rec));
		row.put("schema:identifier", getPlaceIdentifier(rec));
		row.put("schema:latitude", getPlaceLatitude(rec));
		row.put("schema:longitude", getPlaceLongitude(rec));
		row.put("schema:url", getPlaceUrl(rec));
		row.put("vstoi:hasSIRManagerEmail", managerEmail);
		return row;
	}

	@Override
	public String getTableName() {
		return "Place";
	}

	@Override
	public String getErrorMsg(Exception e) {
		return "Error in PlaceGenerator: " + e.getMessage();
	}
 	 
}
