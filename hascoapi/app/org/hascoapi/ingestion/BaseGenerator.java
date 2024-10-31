package org.hascoapi.ingestion;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.hascoapi.utils.Templates;
import org.hascoapi.utils.IngestionLogger;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.HADatAcThing;
import org.hascoapi.utils.MetadataFactory;
import org.hascoapi.utils.CollectionUtil;

public abstract class BaseGenerator {

    protected List<Record> records = null;
    protected RecordFile file;
    protected DataFile dataFile;

    protected List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
    protected List<HADatAcThing> objects = new ArrayList<HADatAcThing>();
    
    protected Map<String, Cache> caches = new HashMap<String, Cache>();

    protected HashMap<String, String> mapCol = new HashMap<String, String>();
    protected String fileName = "";
    protected String relativePath = "";

    protected String studyUri = "";
    protected String namedGraphUri = "";
    protected Templates templates = null;
    protected String elementType = "";

    protected IngestionLogger logger = null;

    public BaseGenerator(DataFile dataFile) {
    	this(dataFile, null, null);
    }
    
    public BaseGenerator(DataFile dataFile, String studyUri) {
    	this(dataFile, studyUri, null);
    }

    public BaseGenerator(DataFile dataFile, String studyUri, String templateFile) {
    	if (studyUri != null && !studyUri.equals("")) {
    		this.studyUri = studyUri;
    	}
        if (templateFile != null) {
            templates = new Templates(templateFile);
        }

        //System.out.println("BaseGenerator: (Constructor) process dataFile");
    	if (dataFile != null) {
    		this.dataFile = dataFile;
            //System.out.println("BaseGenerator: (Constructor) process dataFile: file");
    		file = dataFile.getRecordFile();
            //System.out.println("BaseGenerator: (Constructor) process dataFile: records");
    		records = file.getRecords();
            //System.out.println("BaseGenerator: Number of records is [" + records.size() + "]");
    		fileName = dataFile.getFilename();
            //System.out.println("BaseGenerator: (Constructor) process dataFile: logger");
    		logger = dataFile.getLogger();
    	}
    	
        //System.out.println("BaseGenerator: (Constructor) process initMapping");
        initMapping();
    }

    public void initMapping() {}
    
    public void addCache(Cache cache) {
        if (!caches.containsKey(cache.getName())) {
            caches.put(cache.getName(), cache);
        }
    }
    
    public void clearCacheByName(String name) {
        if (caches.containsKey(name)) {
            caches.get(name).clear();
        }
    }
    
    public void clearAllCaches() {
        for (String name : caches.keySet()) {
            caches.get(name).clear();
        }
    }
    
    public IngestionLogger getLogger() {
        return logger;
    }

    public String getTableName() {
        return null;
    }

    public String getErrorMsg(Exception e) {
        e.printStackTrace();
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return "Errors in " + getClass().getSimpleName() + ": " + e.getMessage() + " " + errors.toString();
    }

    public DataFile getDataFile() {
        return dataFile;
    }
    
    public String getFileName() {
        return fileName;
    }

    public RecordFile getRecordFile() {
        return file;
    }

    public String getStudyUri() {
        return studyUri;
    }
    public void setStudyUri(String studyUri) {
        this.studyUri = studyUri;
    }

    public String getNamedGraphUri() {
        return namedGraphUri;
    }

    public void setNamedGraphUri(String namedGraphUri) {
        this.namedGraphUri = namedGraphUri;
    }

	public String getElementType() {
		return this.elementType;
	}
    
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}


    public Map<String, Object> createRow(Record rec, int rowNumber) throws Exception { return null; }

    public HADatAcThing createObject(Record rec, int rowNumber, String selector) throws Exception { return null; }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public List<HADatAcThing> getObjects() {
        return objects;
    }
    
    public void addRow(Map<String, Object> row) {
        rows.add(row);
    }
    
    public void addObject(HADatAcThing object) {
        objects.add(object);
    }

    public void preprocess() throws Exception {}
    public void preprocessuris(Map<String,String> uris) throws Exception {}

    public void postprocess() throws Exception {}
    public Map<String,String> postprocessuris() throws Exception { return new HashMap<String,String>(); }

    public void createRows() throws Exception {        
        if (records == null) {
            return;
        }
        System.out.println("In BaseGenerator.createRows(): number of record is " + records.size());

        int rowNumber = 0;
        int skippedRows = 0;
        Record lastRecord = null;
        for (Record record : records) {
        	if (lastRecord != null && record.equals(lastRecord)) {
        		skippedRows++;
        	} else {
        		Map<String, Object> tempRow = createRow(record, ++rowNumber);
        		if (tempRow != null) {
        			rows.add(tempRow);
        			lastRecord = record;
        		}
        	}
        }
        if (skippedRows > 0) {
        	System.out.println("Skipped rows: " + skippedRows);
        }
    }

    public void createObjects() throws Exception {
        if (records == null) {
            return;
        }

        int rowNumber = 0;
        for (Record record : records) {
            HADatAcThing obj = createObject(record, ++rowNumber, null);
            if (obj != null) {
                objects.add(obj);
            }
        }

        Map<String, Integer> mapStats = new HashMap<String, Integer>();
        for (HADatAcThing obj : objects) {
            String clsName = obj.getClass().getSimpleName();
            if (mapStats.containsKey(clsName)) {
                mapStats.put(clsName, mapStats.get(clsName) + 1);
            } else {
                mapStats.put(clsName, 1);
            }
        }
        String results = String.join(" and ", mapStats.entrySet().stream()
                .map(e -> e.getValue() + " " + e.getKey() + "(s)")
                .collect(Collectors.toList()));
        if (!results.isEmpty()) {
            logger.println(results + " have been created. ");
        }
    }

    @Override
    public String toString() {
        if (rows.isEmpty()) {
            return "";
        }
        String result = "";
        result = String.join(",", rows.get(0).keySet());
        for (Map<String, Object> row : rows) {
            List<String> values = new ArrayList<String>();
            for (String colName : rows.get(0).keySet()) {
                if (row.containsKey(colName)) {
                    values.add((String) row.get(colName));
                } else {
                    values.add("");
                }
            }
            result += "\n";
            result += String.join(",", values);
        }

        return result;
    }

    private void checkRows(List<Map<String, Object>> rows, String primaryKey) throws Exception {
        int i = 1;
        Set<String> values = new HashSet<>();
        for (Map<String, Object> row : rows) {
            String val = (String)row.get(primaryKey);
            if (null == val) {
                throw new Exception(String.format("Found Row %d without URI specified!", i));
            }
            if (values.contains(val)) {
                throw new Exception(String.format("Duplicate Concepts in Inputfile row %d :" + val + " would be duplicate URIs!", i));
            }
            else {
                values.add(val);
            }

            i++;
        }
    }

    public boolean commitRowsToTripleStore(List<Map<String, Object>> rows) {
        System.out.println("BaseGenerator: commitRowsToTripleStore(): received values");
        //for (Map<String, Object> row : rows) {
        //    for (Map.Entry<String, Object> entry : row.entrySet()) {
        //        System.out.println("Row: " + entry.getKey() + ": " + entry.getValue());
        //    }
        //}
        //System.out.println("BaseGenerator: commitRowsToTripleStore(): getNamedGraphUri() is " + getNamedGraphUri());
        Model model = MetadataFactory.createModel(rows, getNamedGraphUri());
        int numCommitted = MetadataFactory.commitModelToTripleStore(
                model, CollectionUtil.getCollectionPath(
                        CollectionUtil.Collection.SPARQL_GRAPH));

        if (numCommitted > 0) {
            logger.println(String.format("%d triple(s) have been committed to triple store", model.size()));
        }

        return true;
    }

    public boolean commitObjectsToTripleStore(List<HADatAcThing> objects) {
        int count = 0;
        for (HADatAcThing obj : objects) {
            obj.setNamedGraph(getNamedGraphUri());

            //System.out.println("BaseGenerator.commitObjectsToTriplestore() [1]");
            if (obj.saveToTripleStore()) {
                count++;
            }
        }
        
        for (String name : caches.keySet()) {
            if (caches.get(name).getNeedCommit()) {
                //System.out.println("cache " + name + " size: Initial " + caches.get(name).getInitialCache().values().size());
                //System.out.println("cache " + name + " size: New " + caches.get(name).getNewCache().values().size());
                //System.out.println("cache " + name + " size: Total " + caches.get(name).getMapCache().values().size());
                for (Object obj : caches.get(name).getNewCache().values()) {
                    if (obj instanceof HADatAcThing) {
                        //System.out.println("BaseGenerator.commitObjectsToTriplestore() [2]");
                        ((HADatAcThing) obj).saveToTripleStore();
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            logger.println(String.format("%d object(s) have been committed to triple store", count));
        }

        return true;
    }

    public void deleteRowsFromTripleStore(List<Map<String, Object>> rows) {
        Model model = MetadataFactory.createModel(rows, "");

        Repository repo = new SPARQLRepository(
                CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
        repo.init();

        RepositoryConnection con = repo.getConnection();
        con.remove(model);

        String namedGraph = !this.getNamedGraphUri().isEmpty() ? this.getNamedGraphUri() : this.getStudyUri();
        dropGraph(namedGraph);
    }

    public boolean deleteObjectsFromTripleStore(List<HADatAcThing> objects) {
        for (HADatAcThing obj : objects) {
            if ( obj.getNamedGraph() == null || obj.getNamedGraph().length() == 0 ) {
                obj.setNamedGraph(getNamedGraphUri());
                // System.out.println("setting the name graph: " + getNamedGraphUri());
            }
            if (obj.getDeletable()) {
                obj.deleteFromTripleStore();
            }
        }
        
        for (String name : caches.keySet()) {
            if (caches.get(name).getNeedCommit()) {
                for (Object obj : caches.get(name).getNewCache().values()) {
                    if (obj instanceof HADatAcThing) {
                        HADatAcThing object = (HADatAcThing)obj;
                        if (object.getDeletable()) {
                            object.deleteFromTripleStore();
                        }
                    }
                }
            }
        }
        String namedGraph = !this.getNamedGraphUri().isEmpty() ? this.getNamedGraphUri() : this.getStudyUri();
        dropGraph(namedGraph);

        return true;
    }

    private void dropGraph(String namedGraphUri)
    {
        String dropGraph="DROP GRAPH <"+namedGraphUri+ ">";
        UpdateRequest request = UpdateFactory.create(dropGraph);
        UpdateProcessor processor = UpdateExecutionFactory.createRemote(
                request, CollectionUtil.getCollectionPath(CollectionUtil.Collection.SPARQL_UPDATE));
        processor.execute();
    }
}
