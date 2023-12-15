package guru.springframework.msscbeerorderservice.sm.actions;

import guru.springframework.brewery.model.events.FailedAllocationRequest;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author cevher
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationFailedAction implements Action<BeerOrderStatus, BeerOrderEvent> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
        String orderId = (String) context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER);
        Optional.ofNullable(orderId).ifPresent(
                (oid) -> jmsTemplate.convertAndSend(JmsConfig.FAILED_ALLOCATION_QUEUE, new FailedAllocationRequest(oid))
        );
        log.info("Sending compensating message for failed allocation with order id: {}", orderId);
    }
}
