package tests.SDD;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.base.BaseIngest;

public class SDDIngestTest extends BaseIngest {

    @Test
    @DisplayName("Ingest uploaded SDD file")
    void shouldIngestSDDSuccessfully() throws InterruptedException {
        ingestFile("sdd");
    }
}
