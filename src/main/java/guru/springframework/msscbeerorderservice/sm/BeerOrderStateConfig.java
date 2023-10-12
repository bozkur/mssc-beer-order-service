package guru.springframework.msscbeerorderservice.sm;

import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * @author cevher
 */
@Configuration
@EnableStateMachineFactory
public class BeerOrderStateConfig extends StateMachineConfigurerAdapter<BeerOrderStatus, BeerOrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatus, BeerOrderEvent> states) throws Exception {
        states.withStates()
                .initial(BeerOrderStatus.NEW)
                .states(EnumSet.allOf(BeerOrderStatus.class))
                .end(BeerOrderStatus.DELIVERED)
                .end(BeerOrderStatus.DELIVERY_EXCEPTION)
                .end(BeerOrderStatus.CANCELLED)
                .end(BeerOrderStatus.VALIDATION_EXCEPTION)
                .end(BeerOrderStatus.ALLOCATION_EXCEPTION)
                .end(BeerOrderStatus.PICKED_UP);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatus, BeerOrderEvent> transitions) throws Exception {
        transitions.withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.NEW).event(BeerOrderEvent.VALIDATE_ORDER)
                .and()
                .withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATED).event(BeerOrderEvent.VALIDATION_PASSED)
                .and()
                .withExternal().source(BeerOrderStatus.NEW).target(BeerOrderStatus.VALIDATION_EXCEPTION).event(BeerOrderEvent.VALIDATION_FAILED);
    }
}
