package tests;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.*;

public class InstrumentFullWorkflowTest {

    private final Launcher launcher = LauncherFactory.create();

    @Test
    void runAllInstrumentTests() throws InterruptedException {
        runTestClass(InstrumentInsUploadTest.class);
        Thread.sleep(2000);

        runTestClass(InstrumentInsIngestTest.class);
        Thread.sleep(2000);

        runTestClass(InstrumentRegressionTest.class);
        Thread.sleep(3000);

        runTestClass(InstrumentInsDeleteTest.class);
    }

    private void runTestClass(Class<?> testClass) {
        System.out.println("===> Running: " + testClass.getSimpleName());

        launcher.execute(
                LauncherDiscoveryRequestBuilder.request()
                        .selectors(DiscoverySelectors.selectClass(testClass))
                        .build()
        );
    }
}
