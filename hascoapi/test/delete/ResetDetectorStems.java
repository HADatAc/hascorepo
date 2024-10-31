package test.delete;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import play.mvc.Result;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hascoapi.console.controllers.restapi.DetectorStemAPI;
import static play.test.Helpers.contentAsString;

import static test.Responses.*;

@Order(value = 003)
public class ResetDetectorStems {

    // Unit test deleteDetectorStemsForTesting
    @Test
    public void test003ResetDetestorStems() {
		System.out.println("Testing 003 reset DetectorStems");
        final DetectorStemAPI controller = new DetectorStemAPI();
        Result result = controller.deleteDetectorStemsForTesting();
        assertEquals(RESPONSE_NOK_RESET_DETECTOR_STEMS,contentAsString(result));
    }

}
