package test.delete;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import play.mvc.Result;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hascoapi.console.controllers.restapi.CodebookAPI;
import static play.test.Helpers.contentAsString;

import static test.Responses.*;

@Order(value = 005)
public class ResetCodebook {

    // Unit test deleteCodebookForTesting
    @Test
    public void test005ResetCodebook() {
		System.out.println("Testing 005 reset Codebook");
        final CodebookAPI controller = new CodebookAPI();
        Result result = controller.deleteCodebookForTesting();
        assertEquals(RESPONSE_NOK_RESET_CODEBOOK,contentAsString(result));
    }

}
