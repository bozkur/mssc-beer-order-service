package guru.springframework.msscbeerorderservice.sm;

import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author cevher
 */
@SpringBootTest
class BeerOrderStateConfigTest {

    @Autowired
    private StateMachineFactory<BeerOrderStatus, BeerOrderEvent> smFactory;
    private StateMachine<BeerOrderStatus, BeerOrderEvent> sm;

    @BeforeEach
    void setUp() {
         sm = smFactory.getStateMachine();
         sm.start();
    }
    @Test
    @DisplayName("New -> Validate Order -> Validation pending")
    void shouldDoTransitionFromNewToNewWhenValidateOrderOccurs() {
        sm.sendEvent(BeerOrderEvent.VALIDATE_ORDER);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.VALIDATION_PENDING));
    }

    @Test
    @DisplayName("Validation pending -> Validation passed -> Validated")
    void shouldDoTransitionFrpmNewToValidatedWhenValidationPassed() {
        sm.sendEvent(BeerOrderEvent.VALIDATE_ORDER);
        sm.sendEvent(BeerOrderEvent.VALIDATION_PASSED);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.VALIDATED));
    }

    @Test
    @DisplayName("Validation pending -> Validation exception -> Validation failed")
    void shouldDoTransitionFromNewToValidationExceptionWhenValidationFailed() {
        sm.sendEvent(BeerOrderEvent.VALIDATE_ORDER);
        sm.sendEvent(BeerOrderEvent.VALIDATION_FAILED);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.VALIDATION_EXCEPTION));
    }

    @Test
    @DisplayName("Validated -> Allocate -> Allocation Pending")
    void shouldDoTransitionFromValidateToAllocationPending() {
        sm.sendEvent(BeerOrderEvent.VALIDATE_ORDER);
        sm.sendEvent(BeerOrderEvent.VALIDATION_PASSED);
        sm.sendEvent(BeerOrderEvent.ALLOCATE_ORDER);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.ALLOCATION_PENDING));
    }

    @AfterEach
    void tearDown() {
        sm.stop();
    }
}