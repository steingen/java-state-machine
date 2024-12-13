package io.github.steingen.statemachine;

/**
 * @author Lewis
 * <p>
 * Date: 11/4/2024
 * Time: 11:18 AM
 * <p>
 */
public interface StateMachine<S extends Enum<S>, E extends Enum<E>> {
    S getCurrentState();

    boolean isValidTransition(S newState);

    void sendEvent(E event);
}
