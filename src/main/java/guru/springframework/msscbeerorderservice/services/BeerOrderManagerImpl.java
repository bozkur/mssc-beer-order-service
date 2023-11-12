package guru.springframework.msscbeerorderservice.services;

import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.sm.BeerOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * @author cevher
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID";

    private final StateMachineFactory<BeerOrderStatus, BeerOrderEvent> smFactory;
    private final BeerOrderRepository beerOrderRepository;

    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatus.NEW);
        BeerOrder saved = beerOrderRepository.save(beerOrder);
        sendBeerOrderEvent(saved, BeerOrderEvent.VALIDATE_ORDER);
        return saved;
    }

    @Override
    public void processValidationResult(UUID orderId, boolean isValid) {
        Optional<BeerOrder> optionalOrder = beerOrderRepository.findById(orderId);
        optionalOrder.ifPresent(order -> {
            if (isValid) {
                sendBeerOrderEvent(order, BeerOrderEvent.VALIDATION_PASSED);

                Optional<BeerOrder> uptoDateOrder = beerOrderRepository.findById(orderId);
                uptoDateOrder.ifPresent(ord -> sendBeerOrderEvent(ord, BeerOrderEvent.ALLOCATE_ORDER));
            } else {
                sendBeerOrderEvent(order, BeerOrderEvent.VALIDATION_FAILED);
            }
        });
    }

    @Override
    public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> found = beerOrderRepository.findById(beerOrderDto.getId());
        found.ifPresent(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_SUCCESS);
            updateAllocatedQuantity(beerOrderDto);
        });
    }

    private void updateAllocatedQuantity(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> optionalBeerOrder = beerOrderRepository.findById(beerOrderDto.getId());
        optionalBeerOrder.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    if (beerOrderLine.getId().equals(beerOrderLineDto.getId())) {
                        beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                    }
                });
            });
            beerOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: {}", beerOrderDto.getId()));
    }

    @Override
    public void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> found = beerOrderRepository.findById(beerOrderDto.getId());
        found.ifPresent(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_NO_INVENTORY);
            updateAllocatedQuantity(beerOrderDto);
        });
    }

    @Override
    public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> found = beerOrderRepository.findById(beerOrderDto.getId());
        found.ifPresent(beerOrder -> {
            sendBeerOrderEvent(beerOrder, BeerOrderEvent.ALLOCATION_FAILED);
        });
    }

    private StateMachine<BeerOrderStatus, BeerOrderEvent> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatus, BeerOrderEvent> sm = smFactory.getStateMachine(beerOrder.getId());
        sm.stop();

        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(new BeerOrderStateChangeInterceptor(beerOrderRepository));
            sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
        });

        sm.start();
        return sm;
    }

    private void sendBeerOrderEvent(BeerOrder order, BeerOrderEvent event) {
        StateMachine<BeerOrderStatus, BeerOrderEvent> sm = build(order);
        Message<BeerOrderEvent> message = MessageBuilder.withPayload(event).
        setHeader(ORDER_ID_HEADER, order.getId().toString()).build();
        sm.sendEvent(message);
    }
}
