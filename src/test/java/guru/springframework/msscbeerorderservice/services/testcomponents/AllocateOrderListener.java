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
        request.getBeerOrderDto().getBeerOrderLines().forEach(bol ->
                bol.setQuantityAllocated(bol.getOrderQuantity()));

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                AllocateBeerOrderResult.builder().beerOrder(request.getBeerOrderDto()).build());
    }
}
