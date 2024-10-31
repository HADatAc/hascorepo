package test.delete;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import play.mvc.Result;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.hascoapi.console.controllers.restapi.InstrumentAPI;
import static play.test.Helpers.contentAsString;

import static test.Responses.*;

@Order(value = 001)
public class ResetInstrument {

    // Unit test deleteInstrumentForTesting
    @Test
    public void test001ResetInstrument() {
		System.out.println("Testing 001 reset Instrument");
        final InstrumentAPI controller = new InstrumentAPI();
        Result result = controller.deleteInstrumentForTesting();
        assertEquals(RESPONSE_NOK_RESET_INSTRUMENT,contentAsString(result));
    }

}
