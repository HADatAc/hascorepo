package tests.DA;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.base.BaseDelete;

public class DADeleteTest extends BaseDelete {

    @Test
    @DisplayName("Delete DA file by name")
    void shouldDeleteDAByName() throws InterruptedException {
        deleteFile("da", "testeDA");
    }
}
