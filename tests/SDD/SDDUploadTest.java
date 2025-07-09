package tests.SDD;

import org.junit.jupiter.api.*;
import tests.base.BaseUpload;

import java.io.File;

public class SDDUploadTest extends BaseUpload {

    private final String sddType = System.getProperty("sddType", "DPQ"); // default to DPQ

    @Test
    @DisplayName("Upload a valid SDD file with type DPQ or DEMO")
    void shouldUploadSDDFileSuccessfully() throws InterruptedException {
        navigateToUploadPage("sdd");

        fillInputByLabel("Name", "testeSDD" + sddType);
        fillInputByLabel("Version", "1");

        File file = new File("tests/testfiles/SDD-NHANES-2017-2018-" + sddType + ".xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();
    }
}
