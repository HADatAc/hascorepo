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

        File file = new File("/EXAMPLE/PATH/TO/Dsg.xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();
    }
}
