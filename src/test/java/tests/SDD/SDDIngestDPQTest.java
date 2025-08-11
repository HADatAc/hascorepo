package tests.SDD;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.base.BaseIngest;

public class SDDIngestDPQTest extends BaseIngest {

    @Test
    @DisplayName("Ingest INS file: testeINSHIERARCHY")
    void shouldingestsdd() throws InterruptedException {
        ingestSpecificSDD("testeSDDdpq");
    }
}
