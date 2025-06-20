package tests.INS;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class INSUploadTest extends BaseUpload {

    @Test
    @DisplayName("Upload a valid INS file with basic data")
    void shouldUploadINSFileSuccessfully() {
        navigateToUploadPage("ins");

        fillInputByLabel("Name", "testeINS");
        fillInputByLabel("Version", "1");

        File file = new File("/Users/kael/Desktop/Projeto/hascorepo/tests/testfiles/INS-NHANES-2017-2018-NAMESPACES.xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();

    }
}