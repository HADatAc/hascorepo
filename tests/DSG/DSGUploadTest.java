package tests.DSG;

import org.junit.jupiter.api.*;

import java.io.File;
import tests.base.BaseUpload;
public class DSGUploadTest extends BaseUpload {

    private final String dsgType = System.getProperty("dsgType", "WS");
    @Test
    @DisplayName("Upload a valid DSG file with basic data")
    void shouldUploadDSGFileSuccessfully() {
        navigateToUploadPage("dsg");

        fillInputByLabel("Name", "testeDSG");
        fillInputByLabel("Version", "1");

        switch(dsgType) {
            case "nhanes":
                File file = new File("tests/testfiles/DSG-NHANES-2017-2018.xlsx");
                uploadFile(file);
                submitFormAndVerifySuccess();
                break;
            case "WS":
                File fileWS = new File("tests/testfiles/DSG-LTE-PIAGET-WEATHER-STATION.xlsx");
                uploadFile(fileWS);
                submitFormAndVerifySuccess();
                break;
            default:
                throw new IllegalArgumentException("Invalid DSG type: " + dsgType);
        }
    }
}
