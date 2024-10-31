package org.hascoapi.ingestion;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.vocabularies.VSTOI;


public class INSGenerator extends BaseGenerator {
    
	protected String instrumentUri = "";

	protected String firstSlotUri = "";

	public String getInstrumentUri() {
		return this.instrumentUri;
	}
    
	public void setInstrumentUri(String instrumentUri) {
		this.instrumentUri = instrumentUri;
	}

	public String getFirstSlotUri() {
		return this.firstSlotUri;
	}
    
	public void setFirstSlotUri(String firstSlotUri) {
		this.firstSlotUri = firstSlotUri;
	}

	public INSGenerator(String elementType, DataFile dataFile) {
		super(dataFile);
		this.setElementType(elementType);
	}

	@Override
	public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {
		Map<String, Object> row = new HashMap<String, Object>();
		
		for (String header : file.getHeaders()) {
		    if (!header.trim().isEmpty()) {
		        String value = rec.getValueByColumnName(header);
		        if (value != null && !value.isEmpty()) {
					System.out.println("Header: [" + header + "] Value: [" + value + "]");
		            row.put(header, value);
		        }
		    }
		}
		if (this.getElementType().equals("instrument")) {
			//row.put("rdfs:subClassOf", VSTOI.INSTRUMENT);
			row.put("hasco:hascoType", VSTOI.INSTRUMENT);
			row.put("vstoi:hasFirst", this.getFirstSlotUri());
			row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());
		}
		if (this.getElementType().equals("detectorstem")) {
			//row.put("rdfs:subClassOf", VSTOI.DETECTOR_STEM);
			row.put("hasco:hascoType", VSTOI.DETECTOR_STEM);
			row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());
		}
		if (this.getElementType().equals("codebook")) {
			//row.put("rdfs:subClassOf", VSTOI.CODEBOOK);
			row.put("hasco:hascoType", VSTOI.CODEBOOK);
			row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());
		}
		if (this.getElementType().equals("responseoption")) {
			//row.put("rdfs:subClassOf", VSTOI.RESPONSE_OPTION);
			row.put("hasco:hascoType", VSTOI.RESPONSE_OPTION);
			row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());
		}

		if (row.containsKey("hasURI") && !row.get("hasURI").toString().trim().isEmpty()) {
		    return row;
		}
		
		return null;
	}

	@Override
	public String getTableName() {
		return "INS";
	}

	@Override
	public String getErrorMsg(Exception e) {
		return "Error in INSGenerator: " + e.getMessage();
	}

    @Override
    public void preprocessuris(Map<String,String> uris) throws Exception {
		if (this.getElementType().equals("instrument")) {
			this.setFirstSlotUri(uris.get("firstSlotUri"));
			this.setInstrumentUri(uris.get("instrumentUri"));
		}
	}
	
}
