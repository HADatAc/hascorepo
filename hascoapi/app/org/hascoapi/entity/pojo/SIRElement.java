package org.hascoapi.entity.pojo;

public interface SIRElement {

    /*
     *  Possible Status values: "Draft", "UnderReview", "Published", "Deprecated"
     */

    public String getHasStatus();

    public String getHasVersion();

    public String getHasLanguage();

    public String getHasSIRManagerEmail();

}
