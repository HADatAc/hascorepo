package org.hascoapi.entity.pojo;

import java.util.HashMap;
import java.util.Map;

import org.hascoapi.annotations.PropertyField;
import org.hascoapi.utils.Templates;

public class MetadataTemplate extends HADatAcThing {

    private Map<String, String> mapCatalog = new HashMap<String, String>();
    private Templates templates = null;

    @PropertyField(uri = "vstoi:hasStatus")
    private String hasStatus;

    @PropertyField(uri = "vstoi:hasVersion")
    private String hasVersion;

    @PropertyField(uri = "hasco:hasDataFile")
    private String hasDataFileUri;

    @PropertyField(uri="vstoi:hasSIRManagerEmail")
    private String hasSIRManagerEmail;

    public Map<String, String> getCatalog() {
        return mapCatalog;
    }

    public void setTemplates(String templateFile) {
        this.templates = new Templates(templateFile);
    }

    public Templates getTemplates() {
        return this.templates;
    }

    public String getHasStatus() {
        return hasStatus;
    }
    public void setHasStatus(String hasStatus) {
        this.hasStatus = hasStatus;
    }

    public String getHasVersion() {
        return hasVersion;
    }
    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getHasDataFileUri() {
        return hasDataFileUri;
    }
    public void setHasDataFileUri(String hasDataFileUri) {
        this.hasDataFileUri = hasDataFileUri;
        this.setNamedGraph(hasDataFileUri);
    }
    public DataFile getHasDataFile() {
        if (this.hasDataFileUri == null) {
            return null;
        }
        return DataFile.find(this.hasDataFileUri);
    }

    public String getHasSIRManagerEmail() {
        return hasSIRManagerEmail;
    }
    public void setHasSIRManagerEmail(String hasSIRManagerEmail) {
        this.hasSIRManagerEmail = hasSIRManagerEmail;
    }

    @Override
    public void save() {
        saveToTripleStore();
    }

    @Override
    public void delete() {
        deleteFromTripleStore();
    }

}
