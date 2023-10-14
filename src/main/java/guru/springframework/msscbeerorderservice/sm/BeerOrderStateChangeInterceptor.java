package guru.springframework.msscbeerorderservice.sm;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * @author cevher
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatus, BeerOrderEvent> {
    
    private final BeerOrderRepository beerOrderRepository;
    @Override
    public void preStateChange(State<BeerOrderStatus, BeerOrderEvent> state, Message<BeerOrderEvent> message, Transition<BeerOrderStatus, BeerOrderEvent> transition, StateMachine<BeerOrderStatus, BeerOrderEvent> stateMachine, StateMachine<BeerOrderStatus, BeerOrderEvent> rootStateMachine) {
        log.debug("Pre state change");
        Optional.ofNullable(message).ifPresent(
                msg -> Optional.ofNullable(msg.getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER))
                        .ifPresent(oi -> {
                            log.debug("Saving state for order with id: {} Status: {}", oi, state.getId());
                            String orderId = (String) oi;
                            Optional<BeerOrder> foundOrder = beerOrderRepository.findById(UUID.fromString(orderId));
                            foundOrder.ifPresent( ord -> {
                                ord.setOrderStatus(state.getId());
                                beerOrderRepository.saveAndFlush(ord);
                            });
                        })
        );
    }
}
