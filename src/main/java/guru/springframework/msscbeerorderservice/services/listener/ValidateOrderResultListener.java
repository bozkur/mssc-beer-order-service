package guru.springframework.msscbeerorderservice.services.listener;

import guru.springframework.brewery.model.events.ValidateBeerOrderResult;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.services.BeerOrderManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author cevher
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateOrderResultListener {

    private final BeerOrderManager beerOrderManager;

    @Transactional
    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESULT_QUEUE)
    public void listen(ValidateBeerOrderResult validateBeerOrderResult) {
        UUID orderId = validateBeerOrderResult.getOrderId();
        log.debug("Validation result for order id: {} is {}", orderId, validateBeerOrderResult.isValid());
        beerOrderManager.processValidationResult(orderId, validateBeerOrderResult.isValid());

    }
}
