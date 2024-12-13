package io.github.steingen.statemachine;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.util.function.BiPredicate;

/**
 * @author Lewis
 * <p>
 * Date: 11/2/2024
 * Time: 9:21 AM
 * <p>
 */
public class Transition<E extends Enum<E>> {
    @Getter
    private E fromState;
    @Getter
    private E toState;
    private BiPredicate<E, E> guard;


    public static <E extends Enum<E>> Transition<E> of(E fromState, E toState, BiPredicate<E, E> guard) {
        Transition<E> transition = new Transition<>();

        transition.fromState = fromState;
        transition.toState = toState;
        transition.guard = guard;

        return transition;
    }

    public boolean canTransition() {
        return guard.test(fromState, toState);
    }

    /**
     * Applies the defined transition. If the transition can be done (according to the rules defined in the guard),
     * then the returned value is {@code toState}, otherwise the {@code fallbackState} is returned
     * <p>
     * @param fallbackState - The value to return in case the transition cannot be done according to the rules<p>
     *                      set in the guard
     * <p>
     * @return The new state
     * */
    public E apply(@Nullable E fallbackState) {
        return canTransition() ? this.toState : fallbackState;
    }
}
