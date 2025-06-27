package tests.DA;

import org.junit.jupiter.api.*;
import tests.base.BaseUpload;

import java.io.File;

public class DAUploadTest extends BaseUpload {

    private final String sddType = System.getProperty("sddType", "DPQ"); // default to DPQ

    @Test
    @DisplayName("Upload a valid DA file with type DPQ or DEMO")
    void shouldUploadSDDFileSuccessfully() {
        navigateToUploadPage("da");

        fillInputByLabel("Name", "testeDA");
        fillInputByLabel("Version", "1");

        File file = new File("tests/testfiles/DA-NHANES-2017-2018-" + sddType + "_J.csv");
        uploadFile(file);

        submitFormAndVerifySuccess();
    }
}
