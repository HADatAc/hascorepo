package tests.SDD;

import org.junit.jupiter.api.*;
import tests.base.BaseUpload;

import java.io.File;

public class SDDUploadTest extends BaseUpload {

    private final String sddType = System.getProperty("sddType", "DPQ"); // default to DPQ



    @Test
    @DisplayName("Upload a valid SDD file with type DPQ or DEMO")
      void shouldUploadSDDFileSuccessfully() throws InterruptedException {
        switch(sddType){
            case "DPQ", "DEMO" :
        navigateToUploadPage("sdd");
        fillInputByLabel("Name", "testeSDD" + sddType);
        fillInputByLabel("Version", "1");
        File filenhanes = new File("tests/testfiles/SDD-NHANES-2017-2018-" + sddType + ".xlsx");
        uploadFile(filenhanes);
        submitFormAndVerifySuccess();
        break;
        case "WS" :
            navigateToUploadPage("sdd");

            fillInputByLabel("Name", "testeSDD");
            fillInputByLabel("Version", "1");

            File filews = new File("tests/testfiles/SDD-WS.xlsx");;
            uploadFile(filews);

            submitFormAndVerifySuccess();
            break;
        default:
                throw new IllegalArgumentException("Invalid SDD type: " + sddType);
        }
    }

}
