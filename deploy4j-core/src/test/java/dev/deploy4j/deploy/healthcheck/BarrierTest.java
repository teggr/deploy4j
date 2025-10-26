package dev.deploy4j.deploy.healthcheck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Barrier")
class BarrierTest {

    private Barrier barrier;

    @BeforeEach
    void setUp() {
        barrier = new Barrier();
    }

    @Test
    @DisplayName("should be closed by default")
    void shouldBeClosedByDefault() {
        assertThat(barrier.opened()).isFalse();
    }

    @Test
    @DisplayName("should open barrier")
    void shouldOpenBarrier() {
        // Act
        boolean result = barrier.open();

        // Assert
        assertThat(result).isTrue();
        assertThat(barrier.opened()).isTrue();
    }

    @Test
    @DisplayName("should close barrier")
    void shouldCloseBarrier() {
        // Arrange
        barrier.open();

        // Act
        boolean result = barrier.close();

        // Assert
        assertThat(result).isTrue();
        assertThat(barrier.opened()).isFalse();
    }

    @Test
    @DisplayName("should wait for opened barrier without exception")
    void shouldWaitForOpenedBarrier() {
        // Arrange
        barrier.open();

        // Act & Assert - should not throw
        barrier.waitFor();
    }

    @Test
    @DisplayName("should throw exception when waiting for closed barrier")
    void shouldThrowExceptionWhenWaitingForClosedBarrier() {
        // Assert
        assertThatThrownBy(() -> barrier.waitFor())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Halted at barrier");
    }

    @Test
    @DisplayName("should allow multiple open calls")
    void shouldAllowMultipleOpenCalls() {
        // Act
        barrier.open();
        barrier.open();

        // Assert
        assertThat(barrier.opened()).isTrue();
    }

    @Test
    @DisplayName("should allow multiple close calls")
    void shouldAllowMultipleCloseCalls() {
        // Act
        barrier.close();
        barrier.close();

        // Assert
        assertThat(barrier.opened()).isFalse();
    }

    @Test
    @DisplayName("should toggle between open and closed states")
    void shouldToggleBetweenStates() {
        // Act & Assert
        barrier.open();
        assertThat(barrier.opened()).isTrue();

        barrier.close();
        assertThat(barrier.opened()).isFalse();

        barrier.open();
        assertThat(barrier.opened()).isTrue();
    }
}
