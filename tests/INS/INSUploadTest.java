package tests.INS;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class INSUploadTest extends BaseUpload {

    private final String insType = System.getProperty("insType", "nhanes");

    @Test
    @DisplayName("Upload a valid INS file with basic data")
    void shouldUploadINSFileSuccessfully() throws InterruptedException {
        navigateToUploadPage("ins");
        switch (insType) {
            case "nhanes":
                fillInputByLabel("Name", "testeINS");
                fillInputByLabel("Version", "1");

                File file = new File("tests/testfiles/INS-NHANES-2017-2018 DEMO.xlsx");
                uploadFile(file);

                submitFormAndVerifySuccess();
                Thread.sleep(5000);
                /*
                navigateToUploadPage("ins");

                fillInputByLabel("Name", "testeINSHIERARCHY");
                fillInputByLabel("Version", "1");

                File filehi = new File("tests/testfiles/INS-NHANES-2017-2018-HIERARCHY.xlsx");
                uploadFile(filehi);
                submitFormAndVerifySuccess();

                 */
            case "WS":
                fillInputByLabel("Name", "testeINS");
                fillInputByLabel("Version", "1");

                File fileWS = new File("tests/testfiles/INS-LTE-PIAGET-WEATHER-STATION.xlsx");
                uploadFile(fileWS);

                submitFormAndVerifySuccess();
                break;
    }

    }
}
