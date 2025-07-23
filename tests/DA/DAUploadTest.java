package tests.DA;

import org.junit.jupiter.api.*;
import tests.base.BaseUpload;

import java.io.File;

public class DAUploadTest extends BaseUpload {

    private final String daType = System.getProperty("daType", "DPQ"); // default to DPQ

    @Test
    @DisplayName("Upload a valid DA file with type DPQ or DEMO")
    void shouldUploadSDDFileSuccessfully() {


        navigateToUploadPage("da");

        fillInputByLabel("Name", "testeDA");
        fillInputByLabel("Version", "1");
        switch (daType) {
            case "DPQ", "DEMO":
                File filedemo = new File("tests/testfiles/DA-NHANES-2017-2018-" + daType + "_J.csv");
                uploadFile(filedemo);
                submitFormAndVerifySuccess();
                break;
            case "WS":
                File filews = new File("tests/testfiles/DA-NHANES-2017-2018-" + daType + "_J.csv");
                uploadFile(filews);
                submitFormAndVerifySuccess();

        }
    }
}
