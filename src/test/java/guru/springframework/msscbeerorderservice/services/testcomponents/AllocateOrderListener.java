package guru.springframework.msscbeerorderservice.services.testcomponents;

import guru.springframework.brewery.model.events.AllocateBeerOrderRequest;
import guru.springframework.brewery.model.events.AllocateBeerOrderResult;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/**
 * @author cevher
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AllocateOrderListener {

    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message message) {
        AllocateBeerOrderRequest request = (AllocateBeerOrderRequest) message.getPayload();
        String customerRef = request.getBeerOrderDto().getCustomerRef();
        if ("will-be-cancelled-allocation".equals(customerRef)) {
            return;
        }

        AllocateBeerOrderResult.AllocateBeerOrderResultBuilder builder = AllocateBeerOrderResult.builder();

        if ("failed-allocation".equals(customerRef)) {
            builder.allocationError(true);
        } else if ("partial-allocation".equals(customerRef)) {
            builder.pendingInventory(true);
            request.getBeerOrderDto().getBeerOrderLines().forEach(bol ->
                    bol.setQuantityAllocated(bol.getOrderQuantity() - 1));
        } else {
            request.getBeerOrderDto().getBeerOrderLines().forEach(bol ->
                    bol.setQuantityAllocated(bol.getOrderQuantity()));
        }
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                builder.beerOrder(request.getBeerOrderDto()).build());
    }
}
