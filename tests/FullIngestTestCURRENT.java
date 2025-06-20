package tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import tests.DA.DAIngestTest;
import tests.DP2.DP2IngestTest;
import tests.DSG.DSGIngestTest;
import tests.SDD.SDDIngestTest;
import tests.STR.STRIngestTest;
import tests.base.BaseIngest;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullIngestTestCURRENT {

    @BeforeAll
    static void setMode() {
        BaseIngest.ingestMode = "current";
    }
    @Test
    @Order(1)
    void ingestDSG() throws InterruptedException {
        new DSGIngestTest();
    }

    @Test
    @Order(2)
    void ingestDA() throws InterruptedException {
        new DAIngestTest();
    }

    @Test
    @Order(3)
    void ingestSDD() throws InterruptedException {
        new SDDIngestTest();
    }

    @Test
    @Order(4)
    void ingestDP2() throws InterruptedException {
        new DP2IngestTest();
    }

    @Test
    @Order(5)
    void ingestSTR() throws InterruptedException {
        new STRIngestTest();
    }
}
