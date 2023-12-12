package guru.springframework.msscbeerorderservice.sm.actions;

import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * @author cevher
 */
@Component
@Slf4j
public class InvalidOrderAction implements Action<BeerOrderStatus, BeerOrderEvent> {
    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
        String orderId = (String) context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER);
        // Only log invalid order situaiton for now...
        log.info("Compensation for invalid order with order id: {}", orderId);
    }
}
