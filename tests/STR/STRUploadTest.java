package tests.STR;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class STRUploadTest extends BaseUpload {

    @Test
    @DisplayName("Upload a valid STR file with basic data")
    void shouldUploadSTRFileSuccessfully() {
        navigateToUploadPage("str");

        fillInputByLabel("Name", "testeSTR");
        fillInputByLabel("Version", "1");

        File file = new File("/EXAMPLE/PATH/TO/STR.xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();
    }
}
