package org.hascoapi.ingestion;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.vocabularies.VSTOI;

public class CodeBookSlotGenerator extends BaseGenerator {

	public CodeBookSlotGenerator(DataFile dataFile) {
		super(dataFile);
	}

	@Override
    public void createRows() throws Exception {    		

		if (records == null) {
			System.out.println("[ERROR] CodeBookSlotGenerator: no records to process.");
            return;
        }

		System.out.println("inside of CodeBookSlotGenerator's createRows");
		System.out.println("inside of CodeBookSlotGenerator's: total of records=" + records.size());

		int priority = 1;
		
        int rowNumber = 0;
        int skippedRows = 0;
        Record lastRecord = null;
        for (Record record : records) {
        	if (lastRecord != null && record.equals(lastRecord)) {
        		skippedRows++;
        	} else {
        		Map<String, Object> tempRow = createRow(record, ++rowNumber);
				//for (Map.Entry<String, Object> entry : tempRow.entrySet()) {
				//	System.out.println(entry.getKey() + ": " + entry.getValue());
				//}
				if (tempRow != null) {
					String belongsTo = "";
					if (tempRow.get("vstoi:belongsTo") != null) {
						belongsTo = (String)tempRow.get("vstoi:belongsTo");
					}
					if (belongsTo.isEmpty()) {
						System.out.println("[ERROR] CodeBookSlotGenerator: could not find a value for vstoi:belongsTo");
					} else {
						String priorityStr = String.valueOf(priority++);
						tempRow.put("vstoi:hasPriority",priorityStr);
						tempRow.put("hasURI",computeCodeBookSlotUri(priorityStr, belongsTo));
						tempRow.put("rdf:type", VSTOI.CODEBOOK_SLOT);
						tempRow.put("hasco:hascoType", VSTOI.CODEBOOK_SLOT);
						tempRow.put("rdfs:label", "CodebookSlot " + priorityStr);
						tempRow.put("rdfs:comment", "CodeBookSlot " + priorityStr + " of codebook with URI " + belongsTo);
						rows.add(tempRow);
					}
        		}
        	}
        }

        if (skippedRows > 0) {
        	System.out.println("Skipped rows: " + skippedRows);
        }

    }
	
	@Override
	public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception {
		Map<String, Object> row = new HashMap<String, Object>();
		
		for (String header : file.getHeaders()) {
		    if (!header.trim().isEmpty()) {
		        String value = rec.getValueByColumnName(header);
		        if (value != null && !value.isEmpty()) {
		            row.put(header, value);
		        }
		    }
		}
		row.put("vstoi:hasSIRManagerEmail", this.dataFile.getHasSIRManagerEmail());
		return row;
	}

	private String computeCodeBookSlotUri(String priority, String belongsTo) {

		return belongsTo + "/CBS/" + priority;
	}


	@Override
	public String getTableName() {
		return "CodeBookSlot";
	}

	@Override
	public String getErrorMsg(Exception e) {
		e.printStackTrace();
		return "Error in CodeBookSlotGenerator: " + e.getMessage();
	}
}
