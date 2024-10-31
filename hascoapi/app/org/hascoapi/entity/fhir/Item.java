package org.hascoapi.entity.fhir;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemAnswerOptionComponent;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType;
import org.hascoapi.entity.pojo.*;

public class Item {

    private Detector detector;
    private List<AnswerOption> answerOptions;

    public Item(Detector detector) {
        this.detector = detector;
        if (this.detector != null) {
            answerOptions = new ArrayList<AnswerOption>();
            Codebook codebook = detector.getCodebook();
            if (codebook != null) {
                List<CodebookSlot> slots = codebook.getCodebookSlots();
                if (slots != null) {
                    for (CodebookSlot slot : slots) {
                        ResponseOption responseOption = slot.getResponseOption();
                        AnswerOption answerOption = new AnswerOption(responseOption);
                        answerOptions.add(answerOption);
                    }
                }
            }
        }
    }

    public QuestionnaireItemComponent getFHIRObject() {
        QuestionnaireItemComponent item = new QuestionnaireItemComponent();

        item.setDefinition(detector.getUri());
        item.setText(detector.getHasContent());
        item.setType(QuestionnaireItemType.CHOICE);

        for (AnswerOption answerOption : answerOptions) {
            QuestionnaireItemAnswerOptionComponent questionnaireItemAnswerOptionComponent = new QuestionnaireItemAnswerOptionComponent();
            Coding coding = answerOption.getFHIRObject();
            questionnaireItemAnswerOptionComponent.setValue(coding);
            item.addAnswerOption(questionnaireItemAnswerOptionComponent);
        }

        return item;
    }
}
