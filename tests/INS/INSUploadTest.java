package tests.INS;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class INSUploadTest extends BaseUpload {

    @Test
    @DisplayName("Upload a valid INS file with basic data")
    void shouldUploadINSFileSuccessfully() throws InterruptedException {
        navigateToUploadPage("ins");

        fillInputByLabel("Name", "testeINS");
        fillInputByLabel("Version", "1");

        File file = new File("tests/testfiles/INS-NHANES-2017-2018.xlsx");
        uploadFile(file);

        submitFormAndVerifySuccess();
        Thread.sleep(5000);
        navigateToUploadPage("ins");

        fillInputByLabel("Name", "testeINSHIERARCHY");
        fillInputByLabel("Version", "1");

        File filehi = new File("tests/testfiles/INS-NHANES-2017-2018-HIERARCHY.xlsx");
        uploadFile(filehi);

        submitFormAndVerifySuccess();

    }
}