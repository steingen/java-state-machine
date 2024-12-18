package io.github.steingen.statemachine;

import io.github.steingen.bitstore.BitStore;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author Lewis
 * <p>
 * Date: 11/4/2024
 * Time: 11:57 AM
 * <p>
 */
public class AbstractStateMachine<S extends Enum<S>, E extends Enum<E>> implements StateMachine<S, E> {
    @Setter
    @Accessors(chain = true) // Enables chaining by returning "this"
    protected S currentState;
    protected E event;
    protected final Map<E, List<Transition<S>>> eventRules = new HashMap<>();
    /**
     * Allowed transitions will be derived from the declared transitions.
     * No need to maintain transition definitions and then also maintain a map of allowed transitions.
     * The transition definitions should also effectively become the allowed transitions.
     * This derivation should be done in the constructor by calling {@code populateAllowedTransitions()},
     * after the transitions have been defined.
     * */
    protected final Map<S, BitStore<S>> allowedTransitions = new HashMap<>();

    /**
     * Derives the allowed transitions from the defined transitions that are in the eventRules
     * */
    protected void populateAllowedTransitions() {
        for (List<Transition<S>> transitions : eventRules.values()) {
            for (Transition<S> transition : transitions) {
                S fromState = transition.getFromState();
                S toState = transition.getToState();

                // Fetch or create a BitStore for the source status
                allowedTransitions.computeIfAbsent(fromState, k -> BitStore.empty())
                    .set(toState);
            }
        }
    }


    @Override
    public S getCurrentState() {
        return currentState;
    }

    @Override
    public void sendEvent(E event) {
        this.event = event;

        List<Transition<S>> eventTransitions = eventRules.getOrDefault(this.event, List.of());

        for (Transition<S> transition : eventTransitions) {
            S newStatus = transition.apply(null);

            if (null != newStatus) {
                this.currentState = newStatus;
                // The transition changed the state. Short-circuit the loop.
                // We are only interested in the first valid transition, and anyway we only expect there to be one
                // NB: This can also be known by checking if transition.canTransition() is true
                // since that is the condition that is checked when determining whether to transition to the next state
                break;
            }
        }
    }


    @Override
    public boolean isValidTransition(S toState) {
        return isValidTransition(this.currentState, toState);
    }

    public boolean isValidTransition(S fromState, S toState) {
        return allowedTransitions.getOrDefault(fromState, BitStore.empty()).get(toState);
    }

    protected Transition<S> newTransition(S from, S to, BiPredicate<S, S> guard) {
        return Transition.of(from, to, guard);
    }

    protected Transition<S> newTransition(S from, S to) {
        return newTransition(from, to, this::defaultGuard);
    }

    protected boolean defaultGuard(S from, S to) {
        return Objects.equals(from, this.currentState);
    }
}
