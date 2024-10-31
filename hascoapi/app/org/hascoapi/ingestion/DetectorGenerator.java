package org.hascoapi.ingestion;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.vocabularies.VSTOI;

public class DetectorGenerator extends BaseGenerator {

	public DetectorGenerator(DataFile dataFile) {
		super(dataFile);
	}

	@Override
    public void createRows() throws Exception {    		

		if (records == null) {
			System.out.println("[ERROR] DetectorGenerator: no records to process.");
            return;
        }

		System.out.println("inside of DetectorGenerator's createRows");
		System.out.println("inside of DetectorGenerator's: total of records=" + records.size());

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
					tempRow.put("rdf:subClassOf", VSTOI.DETECTOR);
					tempRow.put("hasco:hascoType", VSTOI.DETECTOR);
					tempRow.put("rdfs:label", "Detector" );
					tempRow.put("rdfs:comment", "Detector");
					rows.add(tempRow);
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

	@Override
	public String getTableName() {
		return "Detector";
	}

	@Override
	public String getErrorMsg(Exception e) {
		e.printStackTrace();
		return "Error in DetectorGenerator: " + e.getMessage();
	}
}
