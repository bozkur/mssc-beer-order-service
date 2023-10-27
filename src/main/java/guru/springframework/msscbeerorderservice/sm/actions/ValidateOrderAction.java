package guru.springframework.msscbeerorderservice.sm.actions;

import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.brewery.model.events.ValidateBeerOrderRequest;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import guru.springframework.msscbeerorderservice.web.mapper.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * @author cevher
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateOrderAction implements Action<BeerOrderStatus, BeerOrderEvent> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper mapper;

    @Override
    public void execute(StateContext<BeerOrderStatus, BeerOrderEvent> context) {
        String orderId = (String) context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER);
        Optional.ofNullable(orderId).ifPresent(oid -> {
            Optional<BeerOrder> foundOrder = beerOrderRepository.findById(UUID.fromString(oid));
            foundOrder.ifPresent(bo -> {
                BeerOrderDto beerOrderDto = mapper.beerOrderToDto(bo);
                jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, new ValidateBeerOrderRequest(beerOrderDto));
                log.debug("Validation request is sent for order with id : {}", oid);
            });
        });
    }
}
