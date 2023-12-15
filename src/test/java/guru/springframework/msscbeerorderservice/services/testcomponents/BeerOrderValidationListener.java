package guru.springframework.msscbeerorderservice.services.testcomponents;

import guru.springframework.brewery.model.events.ValidateBeerOrderRequest;
import guru.springframework.brewery.model.events.ValidateBeerOrderResult;
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
public class BeerOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message message) {
        ValidateBeerOrderRequest request = (ValidateBeerOrderRequest) message.getPayload();

        String customerRef = request.getBeerOrderDto().getCustomerRef();
        if ("will-be-cancelled-validation".equals(customerRef)) {
            return;
        }

        boolean isValid = ! "invalid-customer".equals(customerRef);

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESULT_QUEUE,
                ValidateBeerOrderResult.builder().orderId(request.getBeerOrderDto().getId())
                        .isValid(isValid).build());
    }
}
