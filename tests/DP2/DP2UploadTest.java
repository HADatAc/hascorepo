package tests.DP2;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class DP2UploadTest extends BaseUpload {

    @Test
    @DisplayName("Upload a valid DP2 file with basic data")
    void shouldUploadDP2FileSuccessfully() {
        navigateToUploadPage("dp2");

        fillInputByLabel("Name", "testeDP2");
        fillInputByLabel("Version", "1");

        File file = new File("tests/testfiles/DP2-NHANES-2017-2018.xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();
    }
}
