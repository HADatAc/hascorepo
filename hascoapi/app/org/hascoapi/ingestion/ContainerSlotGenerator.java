package org.hascoapi.ingestion;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.vocabularies.VSTOI;

public class ContainerSlotGenerator extends BaseGenerator {

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

	public ContainerSlotGenerator(DataFile dataFile) {
		super(dataFile);
	}

	@Override
    public void createRows() throws Exception {    		

		if (records == null) {
			System.out.println("[ERROR] SlotElementGenerator: no records to process.");
            return;
        }

		System.out.println("inside of SlotElementGenerator's createRows");
		System.out.println("inside of SlotElementGenerator's: total of records=" + records.size());

		Map<String, String> contexts = new HashMap<String, String>();
		int priority = 1;
		String context = "!!root";
		
        int rowNumber = 0;
        int skippedRows = 0;
        Record lastRecord = null;
		boolean isFirst = true;
		String previousUri = null;
        for (Record record : records) {
        	if (lastRecord != null && record.equals(lastRecord)) {
        		skippedRows++;
        	} else {
        		Map<String, Object> tempRow = createRow(record, ++rowNumber);
				//for (Map.Entry<String, Object> entry : tempRow.entrySet()) {
				//	System.out.println(entry.getKey() + ": " + entry.getValue());
				//}
				if (tempRow != null) {

					String id = "";
					if (tempRow.get("hasco:originalID") != null) {
						id = (String)tempRow.get("hasco:originalID");
					}
					String belongsTo = "";
					if (tempRow.get("hasco:originalID") != null) {
						belongsTo = (String)tempRow.get("vstoi:belongsTo");
					}
					
					if (id.startsWith("??") && id.length() > 2) {
						System.out.println("Included context " + id + ":" + belongsTo);
						contexts.put(id, belongsTo);
					}
				
					if (belongsTo.startsWith("??") && !belongsTo.equals(context) && contexts.containsKey(belongsTo)) { 
						System.out.println("Changed context " + belongsTo);
						context = belongsTo;
						priority = 1;
					} 
					String containerUri = this.computeContainerUri(id, belongsTo, contexts);
					String slotUri = this.computeSlotUri(id, belongsTo, contexts);
					System.out.println("Context: [" + context + "] Priority: [" + priority++ + "] Id: [" + id + "] BelongsTo: [" + belongsTo + "]");
					System.out.println("          ContainerURI: [" + containerUri + "]   SlotUri: [" + slotUri + "]");
					tempRow.put("hasURI",slotUri);
					tempRow.put("vstoi:belongsTo",containerUri);
					String priorityStr = String.valueOf(priority);
					tempRow.put("vstoi:hasPriority",priorityStr);
					tempRow.put("rdfs:label", cleanName(id));
					if (id.startsWith("??")) {
						tempRow.put("rdf:type", VSTOI.SUBCONTAINER);
						tempRow.put("hasco:hascoType", VSTOI.SUBCONTAINER);
						tempRow.put("rdfs:subClassOf", VSTOI.SUBCONTAINER);
						tempRow.put("rdfs:comment", "Subcontainer " + cleanName(id));
					} else {
						tempRow.put("rdf:type", VSTOI.CONTAINER_SLOT);
						tempRow.put("hasco:hascoType", VSTOI.CONTAINER_SLOT);
						tempRow.put("rdfs:subClassOf", VSTOI.CONTAINER_SLOT);
						tempRow.put("rdfs:comment", "ContainerSlot " + id);
					}	
					
					if (isFirst) {
						this.setInstrumentUri(containerUri);
						this.setFirstSlotUri(slotUri);
						isFirst = false;
					}

					if (previousUri != null) {
						tempRow.put("vstoi:hasPrevious", slotUri);
					}

					previousUri = slotUri;
					
				}

				if (tempRow != null) {
        			rows.add(tempRow);
        			lastRecord = record;
        		}
        	}
        }
        if (skippedRows > 0) {
        	System.out.println("Skipped rows: " + skippedRows);
        }

		Map<String, String> subcontainers = new HashMap<String, String>();

		for (int i=0; i < rows.size() - 1; i++) {
		//for (Map<String, Object> row : rows) 
			Map<String, Object> rowNow = rows.get(i);
			Map<String, Object> rowNext = rows.get(i + 1);
			if (rowNow.get("hasco:hascoType").equals(VSTOI.SUBCONTAINER)) {
				subcontainers.put((String)rowNow.get("hasURI"), String.valueOf(i));
			}
			System.out.println(i + "    " + rowNow.get("hasURI") + "     " + rowNow.get("vstoi:belongsTo"));
			if (rowNow.get("vstoi:belongsTo").equals(rowNext.get("vstoi:belongsTo"))) {
				rowNow.put("vstoi:hasNext", rowNext.get("hasURI"));				
				System.out.println("adding hasNext");
			} else {
				int subIndex = Integer.valueOf((String)subcontainers.get(rowNext.get("vstoi:belongsTo")));
				Map<String, Object> rowSubcontainer = rows.get(subIndex); 
				rowSubcontainer.put("vstoi:hasFirst", rowNext.get("hasURI"));				
				//System.out.println("adding hasFirst for " + rowNow.get("hasURI") + " pointing to " + rowNext.get("hasURI"));
			}			
		}
    }
	
	@Override
	public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {
		Map<String, Object> row = new HashMap<String, Object>();
		
		for (String header : file.getHeaders()) {
		    if (!header.trim().isEmpty()) {
		        String value = rec.getValueByColumnName(header);
		        if (value != null && !value.isEmpty()) {
					if (!header.equals("instrument")) {
		            	row.put(header, value);
					}
		        }
		    }
		}
		//row.put("hasco:hascoType", VSTOI.INSTRUMENT);
		row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());

		//if (row.containsKey("hasURI") && !row.get("hasURI").toString().trim().isEmpty()) {
		    return row;
		//}
		
		//return null;
	}

	@Override
    public Map<String,String> postprocessuris() throws Exception {
		Map<String,String> uris = new HashMap<String,String>();
		uris.put("instrumentUri",this.getInstrumentUri());
		uris.put("firstSlotUri",this.getFirstSlotUri());
		return uris;
	}

	private String computeContainerUri(String id, String belongsTo, Map<String, String> contexts) {

		// CONTAINER URI
		String containerUri = "";
		if (!belongsTo.startsWith("??")) {
			containerUri = belongsTo;
		} else {
			String search = belongsTo;
			String searchResult = contexts.get(search);
			containerUri = searchResult + "/" + cleanName(belongsTo);
			while (contexts.get(search).startsWith("??")) {
				search = contexts.get(search);
				searchResult = contexts.get(search);
				containerUri = searchResult + "/" + cleanName(containerUri);
			} 
		}

		return containerUri;
	}

	private String computeSlotUri(String id, String belongsTo, Map<String, String> contexts) {

		// URI WITH SLOT's ID
		String fullUri = computeContainerUri(id,belongsTo,contexts);
		if (!id.startsWith("??")) {
			fullUri = fullUri + "/CTS/" + id;
		} else {
			fullUri = fullUri + "/" + cleanName(id);
		}

		return fullUri;
	}

	private String cleanName(String raw) {
		if (raw == null || raw.isEmpty()) {
			return "";
		}
		if (raw.startsWith("??")) {
			return raw.substring(2);
		}
		return raw;
	}


	@Override
	public String getTableName() {
		return "SlotElement";
	}

	@Override
	public String getErrorMsg(Exception e) {
		e.printStackTrace();
		return "Error in SlotElementGenerator: " + e.getMessage();
	}
}
