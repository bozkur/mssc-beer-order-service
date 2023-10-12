package guru.springframework.msscbeerorderservice.sm;

import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("New -> Validate Order -> New")
    void shouldDoTransitionFromNewToNewWhenValidateOrderOccurs() {
        sm.sendEvent(BeerOrderEvent.VALIDATE_ORDER);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.NEW));
    }

    @Test
    @DisplayName("New -> Validation passed -> Validated")
    void shouldDoTransitionFrpmNewToValidatedWhenValidationPassed() {
        sm.sendEvent(BeerOrderEvent.VALIDATION_PASSED);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.VALIDATED));
    }

    @Test
    @DisplayName("New -> Validation exception -> Validation failed")
    void shouldDoTransitionFromNewToValidationExceptionWhenValidationFailed() {
        sm.sendEvent(BeerOrderEvent.VALIDATION_FAILED);
        assertThat(sm.getState().getId(), Matchers.equalTo(BeerOrderStatus.VALIDATION_EXCEPTION));
    }

    @AfterEach
    void tearDown() {
        sm.stop();
    }
}