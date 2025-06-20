package tests;

import org.junit.jupiter.api.*;

import tests.DSG.DSGUploadTest;
import tests.DA.DAUploadTest;
import tests.SDD.SDDUploadTest;
import tests.DP2.DP2UploadTest;
import tests.STR.STRUploadTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullUploadTestDPQ {

    @Test
    @Order(1)
    void uploadDSG() {
        new DSGUploadTest();
    }

    @Test
    @Order(2)
    void uploadDA() {
        System.setProperty("daType", "DPQ");
        new DAUploadTest();
    }

    @Test
    @Order(3)
    void uploadSDD() {
        System.setProperty("sddType", "DPQ");
        new SDDUploadTest();
    }

    @Test
    @Order(4)
    void uploadDP2() {
        new DP2UploadTest();
    }

    @Test
    @Order(5)
    void uploadSTR() {
        new STRUploadTest();
    }
}
