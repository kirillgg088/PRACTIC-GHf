package com.wildberries;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class RunTests {
    public static void main(String[] args) {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                    selectClass(com.wildberries.webtests.ConfigBasedTest.class),
                    selectClass(com.wildberries.webtests.ParameterizedWebTest.class)
                )
                .build();
        
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        
        TestExecutionSummary summary = listener.getSummary();
        
        System.out.println("\n========== ОТЧЕТ О ТЕСТИРОВАНИИ ==========");
        System.out.println("Всего тестов: " + summary.getTestsFoundCount());
        System.out.println("Успешно: " + summary.getTestsSucceededCount());
        System.out.println("Провалено: " + summary.getTestsFailedCount());
        System.out.println("Пропущено: " + summary.getTestsSkippedCount());
        System.out.println("Общее время: " + summary.getTimeFinished() + " мс");
        System.out.println("========================================\n");
        
        if (summary.getTestsFailedCount() > 0) {
            System.out.println("Детали ошибок:");
            summary.getFailures().forEach(failure -> 
                System.out.println(" - " + failure.getTestIdentifier().getDisplayName() + 
                                 ": " + failure.getException().getMessage()));
        }
        
        System.exit(summary.getTestsFailedCount() > 0 ? 1 : 0);
    }
}
