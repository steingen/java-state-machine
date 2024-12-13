package io.github.steingen.statemachine;

import org.junit.jupiter.api.Test;

import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Lewis
 * <p>
 * Date: 11/2/2024
 * Time: 11:57 PM
 * <p>
 */
class TransitionTest {
    private enum TestEnum {
        SUBMITTED,
        PROCESSING,
        DISPATCHED,
        DELIVERED,
        COMPLETED,
        CANCELLED,
        EXPIRED,
        OPS_CANCELLED,
        PARTIALLY_DELIVERED
    }
    
    @Test
    void testTransitionAllowed() {
        // Arrange: Define a guard that always allows the transition
        BiPredicate<TestEnum, TestEnum> guard = (fromState, toState) -> true;
        Transition<TestEnum> transition = Transition.of(TestEnum.SUBMITTED, TestEnum.PROCESSING, guard);

        // Act & Assert
        assertTrue(transition.canTransition(), "Transition should be allowed by guard");
        assertEquals(TestEnum.PROCESSING, transition.apply(null),
            "Transition should move to new state");
    }

    @Test
    void testTransitionNotAllowed() {
        // Arrange: Define a guard that prevents the transition
        BiPredicate<TestEnum, TestEnum> guard = (fromState, toState) -> false;
        Transition<TestEnum> transition = Transition.of(TestEnum.SUBMITTED, TestEnum.COMPLETED, guard);

        // Act & Assert
        assertFalse(transition.canTransition(), "Transition should be prevented by guard");
        assertEquals(TestEnum.SUBMITTED, transition.apply(TestEnum.SUBMITTED),
            "Transition should remain in initial state");
    }

    @Test
    void testTransitionToSameState() {
        // Arrange: Define a guard that prevents the transition to the same state
        BiPredicate<TestEnum, TestEnum> guard = (fromState, toState) -> false;
        Transition<TestEnum> transition = Transition.of(TestEnum.COMPLETED, TestEnum.COMPLETED, guard);

        // Act & Assert
        assertFalse(transition.canTransition(), "Transition to the same state should not be allowed");
        assertEquals(TestEnum.COMPLETED, transition.apply(TestEnum.COMPLETED),
            "Transition should remain in the same state");
    }

    @Test
    void testGuardWithConditionalLogic() {
        // Arrange: Define a guard that only allows transition to COMPLETED if current state is PROCESSING
        BiPredicate<TestEnum, TestEnum> guard = (fromState, toState) -> toState == TestEnum.COMPLETED;
        Transition<TestEnum> transition = Transition.of(TestEnum.PROCESSING, TestEnum.COMPLETED, guard);

        // Act & Assert
        assertTrue(transition.canTransition(), "Transition should be allowed from PROCESSING to COMPLETED");
        assertEquals(TestEnum.COMPLETED, transition.apply(null),
            "Transition should move to COMPLETED state");
    }
}
