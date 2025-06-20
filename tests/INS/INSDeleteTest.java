package tests.INS;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.base.BaseDelete;

public class INSDeleteTest extends BaseDelete {

    @Test
    @DisplayName("Delete INS file by name")
    void shouldDeleteDP2ByName() throws InterruptedException {
        deleteFile("ins", "testeINS");
    }
}
