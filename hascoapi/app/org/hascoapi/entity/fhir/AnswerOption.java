package org.hascoapi.entity.fhir;

import org.hl7.fhir.r4.model.Coding;
import org.hascoapi.entity.pojo.ResponseOption;

public class AnswerOption {
  
    private ResponseOption responseOption;

    public AnswerOption(ResponseOption responseOption) {
        this.responseOption = responseOption;
    }

    public Coding getFHIRObject() {
        Coding coding = new Coding();
        coding.setCode(responseOption.getUri());
        coding.setDisplay(responseOption.getHasContent());
        return coding;
    }
}
