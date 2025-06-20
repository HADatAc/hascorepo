package tests.INS;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.base.BaseIngest;

public class INSIngestTest extends BaseIngest {

    @Test
    @DisplayName("Ingest uploaded INS file")
    void shouldIngestINSSuccessfully() throws InterruptedException {
        ingestFile("ins");
    }
}
