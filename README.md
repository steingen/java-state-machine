# Java State Machine
This is a simple state machine implementation in Java.

A state machine is a computational model in which the entity that it represents can be in any one of its finite states/stable conditions at a given time.  
It comprises a finite set of states and reacts to inputs so that it can transition the entity to its new state. For the purposes of this library, we shall define the following terms:  
1. `State` - The current condition of the entity
2. `Transition` - A change from the current state to a new state
3. `Event` - The input that causes the entity to transition to a new state
4. `Guard` - A condition that must be met in order for the entity to transition to a new state

The state machine enables you to define a finite set of states and events, and then define the rules that determine how the entity transitions from one state to another.  
It also enables you to query the current state of the state machine at any given time, and to send an event to the state machine, which causes the entity to transition to a new state.

## Getting Started with the Java State Machine
For basic usage of this state machine, you are to proceed as follows:  
1. Define an enum class containing the states of your state machine
2. Define an enum class containing the events that will trigger the transitions from one state to another
3. Extend the `AbstractStateMachine` class to define the allowed transitions from the defined states and events

### 1. Define an enum class for states
This is a simple Java enum, or protocol buffer enum class.
Example:  
```java
public enum SampleState {
    INITIAL,
    PROCESSING,
    PAUSED,
    COMPLETED
}
```

### 2. Define an enum class for events
This is a simple Java enum, or protocol buffer enum class.
Example:
```java
public enum SampleEvent {
    START_PROCESS,
    PAUSE_PROCESS,
    COMPLETE_PROCESS
}
```

### 3. Define the allowed transitions (Implement a class that extends the `AbstractStateMachine` class)
```java
public class SampleStateMachine extends AbstractStateMachine<SampleState, SampleEvent> {
    
    public SampleStateMachine() {
        // Define event rules for state transitions
        this.eventRules.putAll(
                Map.of(
                        SampleEvent.START_PROCESS, List.of(
                                newTransition(SampleState.INITIAL, SampleState.PROCESSING), 
                                newTransition(SampleState.PAUSED, SampleState.PROCESSING)
                        ),
                        SampleEvent.PAUSE_PROCESS, List.of(
                                newTransition(SampleState.PROCESSING, SampleState.PAUSED)
                        ),
                        SampleEvent.COMPLETE_PROCESS, List.of(
                                newTransition(SampleState.PROCESSING, SampleState.COMPLETED)
                        )
                )
        );
        // Populate allowed transitions
        populateAllowedTransitions();
    }
    
}
```

The above example shows a state machine with four states and three events. The constructor of the `SampleStateMachine` class defines the transitions that are allowed given the incoming event.  
For example, the event `START_PROCESS` transitions the state machine from INITIAL to PROCESSING, and from PAUSED to PROCESSING.

**NOTE**  
The `AbstractStateMachine` defines a default guard that always allows the transition if the state machine's current state is same as the initial state of the defined transition.  
For example:  
Given the following transition definition: `newTransition(SampleState.INITIAL, SampleState.PROCESSING)`, the default guard will allow the transition if the current state of the state machine is `SampleState.INITIAL`.  
The default guard is implemented as follows:   
```java
    protected boolean defaultGuard(S from, S to) {
        return Objects.equals(from, this.currentState);
    }
```
You can also override the default guard to implement your own logic.

**NOTE**
The `populateAllowedTransitions()` method is called in the constructor to derive the "allowed transitions" from the defined event rules. This enables you to call methods such as `isValidTransition(...)` and know if a transition is valid.

### 4. Using the State Machine
Once the event rules and allowed transitions have been defined, you can now send events to the state machine and query its current state.

```java
SampleStateMachine stateMachine = new SampleStateMachine();
// Send an event to the state machine
stateMachine.sendEvent(SampleEvent.START_PROCESS);
// Query the current state of the state machine
SampleState currentState = stateMachine.getCurrentState();
```

