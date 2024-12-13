package io.github.steingen.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Lewis
 * <p>
 * Date: 11/4/2024
 * Time: 9:19 PM
 * <p>
 */
class AbstractStateMachineTest {

    public enum TestState {
        INITIAL,
        PROCESSING,
        COMPLETED
    }

    public enum TestEvent {
        START_PROCESS,
        COMPLETE_PROCESS
    }

    public static class TestStateMachine extends AbstractStateMachine<TestState, TestEvent> {
        public TestStateMachine() {
            // Define event rules for state transitions
            this.eventRules.putAll(
                    Map.of(
                            TestEvent.START_PROCESS, List.of(
                                    newTransition(TestState.INITIAL, TestState.PROCESSING),
                                    newTransition(TestState.PROCESSING, TestState.COMPLETED)
                            ),
                            TestEvent.COMPLETE_PROCESS, List.of(newTransition(TestState.PROCESSING, TestState.COMPLETED))
                    )
            );
            populateAllowedTransitions();
        }
    }

    private final TestStateMachine stateMachine = new TestStateMachine();

    @BeforeEach
    public void setUp() {
        stateMachine.setCurrentState(TestState.INITIAL);
    }

    @Test
    void testSendEventStartProcess() {
        stateMachine.sendEvent(TestEvent.START_PROCESS);
        assertEquals(TestState.PROCESSING, stateMachine.getCurrentState());
    }

    @Test
    void testSendEventCompleteProcess() {
        stateMachine.sendEvent(TestEvent.START_PROCESS); // Move to PROCESSING
        stateMachine.sendEvent(TestEvent.COMPLETE_PROCESS); // Now complete
        assertEquals(TestState.COMPLETED, stateMachine.getCurrentState());
    }

    @Test
    void testSendEventWithNoTransition() {
        stateMachine.currentState = TestState.COMPLETED; // Already completed
        stateMachine.sendEvent(TestEvent.START_PROCESS); // This should not change the state
        assertEquals(TestState.COMPLETED, stateMachine.getCurrentState());
    }

    @Test
    void testIsValidTransition() {
        assertTrue(stateMachine.isValidTransition(TestState.PROCESSING));
    }

    @Test
    void testCreateTransition() {
        AbstractStateMachine<TestState, TestEvent> abstractStateMachine = new AbstractStateMachine<>();

        Transition<TestState> transition = abstractStateMachine.newTransition(TestState.INITIAL, TestState.PROCESSING);

        abstractStateMachine.setCurrentState(TestState.INITIAL);

        assertTrue(transition.canTransition());
    }

    @Test
    void testCreateTransitionWithGuard() {
        AbstractStateMachine<TestState, TestEvent> abstractStateMachine = new AbstractStateMachine<>();

        Transition<TestState> transition = abstractStateMachine.newTransition(TestState.INITIAL, TestState.PROCESSING,
                (from, to) -> true);

        assertTrue(transition.canTransition());
    }
}
