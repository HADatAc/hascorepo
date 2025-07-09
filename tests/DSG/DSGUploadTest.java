package tests.DSG;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class DSGUploadTest extends BaseUpload {

    @Test
    @DisplayName("Upload a valid DSG file with basic data")
    void shouldUploadDSGFileSuccessfully() {
        navigateToUploadPage("dsg");

        fillInputByLabel("Name", "testeDSG");
        fillInputByLabel("Version", "1");

        File file = new File("tests/testfiles/DSG-NHANES-2017-2018.xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();
    }
}
