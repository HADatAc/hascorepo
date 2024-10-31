package org.hascoapi.entity.pojo;

import java.util.HashMap;
import java.util.Map;

import org.hascoapi.ingestion.Record;
import org.hascoapi.ingestion.RecordFile;

public class DOI {

	private DataFile doifile = null;
	private Map<String, String> mapCatalog = new HashMap<String, String>();
	
	public DOI(DataFile dataFile) {
		this.doifile = dataFile;
		readCatalog(dataFile.getRecordFile());
	}
	
	public String getFileName() {
	    return doifile.getFilename();
    }
	
	public Map<String, String> getCatalog() {
		return mapCatalog;
	}
	
    public String getVersion() {
        String sddVersion = mapCatalog.get("Version");
        if (sddVersion == null) {
            return "";
        }
        return sddVersion;
    }

    public String getStudyId() {
        String sddVersion = mapCatalog.get("Study_ID");
        if (sddVersion == null) {
            return "";
        }
        return sddVersion;
    }

    private void readCatalog(RecordFile file) {
	    if (!file.isValid()) {
            return;
        }
	    
	    for (Record record : file.getRecords()) {
	        mapCatalog.put(record.getValueByColumnIndex(0), record.getValueByColumnIndex(1));
	    }
	}	
}
