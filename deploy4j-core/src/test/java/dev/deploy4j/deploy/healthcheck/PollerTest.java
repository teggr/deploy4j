package dev.deploy4j.deploy.healthcheck;

import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.HealthCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Poller")
class PollerTest {

    @Mock
    private DeployContext deployContext;

    @Mock
    private Configuration configuration;

    @Mock
    private HealthCheck healthCheck;

    private Poller poller;

    @BeforeEach
    void setUp() {
        when(deployContext.config()).thenReturn(configuration);
        when(configuration.healthcheck()).thenReturn(healthCheck);
        lenient().when(configuration.readinessDelay()).thenReturn(1000);
        poller = new Poller(deployContext);
    }

    @Test
    @DisplayName("should wait for healthy status without pause")
    void shouldWaitForHealthyStatusWithoutPause() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(5);
        Supplier<String> statusSupplier = () -> "healthy";

        // Act - should complete without throwing
        poller.waitForHealthy(false, statusSupplier);
    }

    @Test
    @DisplayName("should wait for healthy status after retries")
    void shouldWaitForHealthyAfterRetries() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(5);
        AtomicInteger attempts = new AtomicInteger(0);
        Supplier<String> statusSupplier = () -> {
            if (attempts.incrementAndGet() < 3) {
                return "starting";
            }
            return "healthy";
        };

        // Act - should complete without throwing
        poller.waitForHealthy(false, statusSupplier);
    }

    @Test
    @DisplayName("should wait for running status without pause")
    void shouldWaitForRunningStatusWithoutPause() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(5);
        Supplier<String> statusSupplier = () -> "running";

        // Act - should complete without throwing
        poller.waitForHealthy(false, statusSupplier);
    }

    @Test
    @DisplayName("should handle max attempts being reached")
    void shouldHandleMaxAttemptsBeingReached() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(2);
        Supplier<String> statusSupplier = () -> "starting";

        // Act - the exception is caught and swallowed, so it completes normally
        poller.waitForHealthy(false, statusSupplier);
    }

    @Test
    @DisplayName("should wait for unhealthy status without pause")
    void shouldWaitForUnhealthyStatusWithoutPause() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(5);
        Supplier<String> statusSupplier = () -> "unhealthy";

        // Act - should complete without throwing
        poller.waitForUnhealthy(false, statusSupplier);
    }

    @Test
    @DisplayName("should wait for unhealthy status after retries")
    void shouldWaitForUnhealthyAfterRetries() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(5);
        AtomicInteger attempts = new AtomicInteger(0);
        Supplier<String> statusSupplier = () -> {
            if (attempts.incrementAndGet() < 3) {
                return "healthy";
            }
            return "unhealthy";
        };

        // Act - should complete without throwing
        poller.waitForUnhealthy(false, statusSupplier);
    }

    @Test
    @DisplayName("should handle container not becoming unhealthy")
    void shouldHandleContainerNotBecomingUnhealthy() {
        // Arrange
        when(healthCheck.maxAttempts()).thenReturn(2);
        Supplier<String> statusSupplier = () -> "healthy";

        // Act - the exception is caught and swallowed, so it completes normally
        poller.waitForUnhealthy(false, statusSupplier);
    }
}
