package org.hascoapi.console.controllers.restapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import org.hascoapi.Constants;
import org.hascoapi.entity.pojo.AnnotationStem;
import org.hascoapi.entity.pojo.Codebook;
import org.hascoapi.entity.pojo.DA;
import org.hascoapi.entity.pojo.DSG;
import org.hascoapi.entity.pojo.DataFile;
import org.hascoapi.entity.pojo.Detector;
import org.hascoapi.entity.pojo.DetectorStem;
import org.hascoapi.entity.pojo.GenericFind;
import org.hascoapi.entity.pojo.KGR;
import org.hascoapi.entity.pojo.Organization;
import org.hascoapi.entity.pojo.Person;
import org.hascoapi.entity.pojo.Place;
import org.hascoapi.entity.pojo.PostalAddress;
import org.hascoapi.entity.pojo.ResponseOption;
import org.hascoapi.entity.pojo.SDD;
import org.hascoapi.entity.pojo.SemanticVariable;
import org.hascoapi.entity.pojo.Study;
import org.hascoapi.entity.pojo.StudyObject;
import org.hascoapi.entity.pojo.StudyObjectCollection;
import org.hascoapi.entity.pojo.StudyRole;
import org.hascoapi.entity.pojo.VirtualColumn;
import org.hascoapi.utils.ApiUtil;
import org.hascoapi.utils.HAScOMapper;
import org.hascoapi.vocabularies.HASCO;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class StudyAPI extends Controller {

    public static Result getStudies(List<Study> results){
        if (results == null) {
            return ok(ApiUtil.createResponse("No Study has been found", false));
        } else {
            ObjectMapper mapper = HAScOMapper.getFiltered(HAScOMapper.FULL,HASCO.STUDY);
            JsonNode jsonObject = mapper.convertValue(results, JsonNode.class);
            return ok(ApiUtil.createResponse(jsonObject, true));
        }
    }

    /**
     *   GET ELEMENTS BY MANAGER EMAIL AND STUDY WITH PAGE
     */

    public Result getElementsByManagerEmailByStudy(String studyuri, String elementtype, String manageremail, int pagesize, int offset) {
        if (manageremail == null || manageremail.isEmpty()) {
            return ok(ApiUtil.createResponse("No Manager Email has been provided", false));
        }
        if (elementtype.equals("studyobjectcollection")) {
            GenericFind<StudyObjectCollection> query = new GenericFind<StudyObjectCollection>();
            List<StudyObjectCollection> results = query.findByManagerEmailWithPagesByStudy(StudyObjectCollection.class, studyuri, manageremail, pagesize, offset);
            return StudyObjectCollectionAPI.getStudyObjectCollections(results);
        }  else if (elementtype.equals("studyobject")) {
            GenericFind<StudyObject> query = new GenericFind<StudyObject>();
            List<StudyObject> results = query.findByManagerEmailWithPagesByStudy(StudyObject.class, studyuri, manageremail, pagesize, offset);
            return StudyObjectAPI.getStudyObjects(results);
        }  else if (elementtype.equals("studyrole")) {
            GenericFind<StudyRole> query = new GenericFind<StudyRole>();
            List<StudyRole> results = query.findByManagerEmailWithPagesByStudy(StudyRole.class, studyuri, manageremail, pagesize, offset);
            return StudyRoleAPI.getStudyRoles(results);
        } else if (elementtype.equals("da")) {
            GenericFind<DA> query = new GenericFind<DA>();
            List<DA> results = query.findByManagerEmailWithPagesByStudy(DA.class, studyuri, manageremail, pagesize, offset);
            return DAAPI.getDAs(results);
        }  else if (elementtype.equals("virtualcolumn")) {
            GenericFind<VirtualColumn> query = new GenericFind<VirtualColumn>();
            List<VirtualColumn> results = query.findByManagerEmailWithPagesByStudy(VirtualColumn.class, studyuri, manageremail, pagesize, offset);
            return VirtualColumnAPI.getVirtualColumns(results);
        }
        /* 
        else if (elementType.equals("person")) {
            GenericFind<Person> query = new GenericFind<Person>();
            List<Person> results = query.findByManagerEmailWithPagesByStudy(Person.class, studyuri, manageremail, pagesize, offset);
            return PersonAPI.getPeople(results);
        }  else if (elementType.equals("organization")) {
            GenericFind<Organization> query = new GenericFind<Organization>();
            List<Organization> results = query.findByManagerEmailWithPagesByStudy(Organization.class, studyuri, manageremail, pagesize, offset);
            return OrganizationAPI.getOrganizations(results);
        }  else if (elementType.equals("place")) {
            GenericFind<Place> query = new GenericFind<Place>();
            List<Place> results = query.findByManagerEmailWithPagesByStudy(Place.class, studyuri, manageremail, pagesize, offset);
            return PlaceAPI.getPlaces(results);
        }  else if (elementType.equals("postaladdress")) {
            GenericFind<PostalAddress> query = new GenericFind<PostalAddress>();
            List<PostalAddress> results = query.findByManagerEmailWithPagesByStudy(PostalAddress.class, studyuri, manageremail, pagesize, offset);
            return PostalAddressAPI.getPostalAddresses(results);
        }  else if (elementtype.equals("semanticvariable")) {
            GenericFind<SemanticVariable> query = new GenericFind<SemanticVariable>();
            List<SemanticVariable> results = query.findByManagerEmailWithPagesByStudy(SemanticVariable.class, studyuri, manageremail, pagesize, offset);
            return SemanticVariableAPI.getSemanticVariables(results);
        }  else if (elementtype.equals("sdd")) {
            GenericFind<SDD> query = new GenericFind<SDD>();
            List<SDD> results = query.findByManagerEmailWithPagesByStudy(SDD.class, studyuri, manageremail, pagesize, offset);
            return SDDAPI.getSDDs(results);
        }  else if (elementType.equals("datafile")) {
            GenericFind<DataFile> query = new GenericFind<DataFile>();
            List<DataFile> results = query.findByManagerEmailWithPagesByStudy(DataFile.class, studyuri, manageremail, pagesize, offset);
            return DataFileAPI.getDataFiles(results);
        }  
        */
        return ok("[getElementsByManagerEmailByStudy] No valid element type.");

    }

    public Result getTotalElementsByManagerEmailByStudy(String studyuri, String elementtype, String manageremail){
        //System.out.println("SIRElementAPI: getTotalElementsByManagerEmailByStudy");
        if (elementtype == null || elementtype.isEmpty()) {
            return ok(ApiUtil.createResponse("No elementtype has been provided", false));
        }
        Class clazz = GenericFind.getElementClass(elementtype);
        if (clazz == null) {        
            return ok(ApiUtil.createResponse("[" + elementtype + "] is not a valid elementtype", false));
        }
        
        int totalElements = totalElements = GenericFind.findTotalByManagerEmailByStudy(clazz, studyuri, manageremail);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }
        return ok(ApiUtil.createResponse("query method getTotalElementsByManagerEmailByStudy() failed to retrieve total number of element", false));

    }

    public Result findTotalDAs(String uri) {
        int totalElements = Study.findTotalStudyDAs(uri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalStudyDAs() failed to retrieve total number of element", false));   
    }

    public Result findTotalRoles(String uri) {
        int totalElements = Study.findTotalStudyRoles(uri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalStudyRoles() failed to retrieve total number of element", false));   
    }

    public Result findSOCs(String uri, int pagesize, int offset) {
        List<StudyObjectCollection> results = Study.findStudyObjectCollections(uri, pagesize, offset);
        return StudyObjectCollectionAPI.getStudyObjectCollections(results);       
    }

    public Result findTotalSOCs(String uri) {
        int totalElements = Study.findTotalStudyObjectCollections(uri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalStudyObjectCollections() failed to retrieve total number of element", false));   
    }

    public Result findVCs(String studyuri) {
        List<VirtualColumn> results = VirtualColumn.findVCsByStudy(studyuri);
        return VirtualColumnAPI.getVirtualColumns(results);
       
    }

    public Result findTotalVCs(String studyuri) {
        int totalElements = VirtualColumn.findTotalVCsByStudy(studyuri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalVCss() failed to retrieve total number of element", false));   
    }

    public Result findTotalSOs(String studyuri) {
        int totalElements = StudyObject.getNumberStudyObjectsByStudy(studyuri);
        if (totalElements >= 0) {
            String totalElementsJSON = "{\"total\":" + totalElements + "}";
            return ok(ApiUtil.createResponse(totalElementsJSON, true));
        }     
        return ok(ApiUtil.createResponse("Query method findTotalSOs() failed to retrieve total number of element", false));   
    }
    
    
}
