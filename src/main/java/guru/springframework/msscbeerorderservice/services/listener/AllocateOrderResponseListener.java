package guru.springframework.msscbeerorderservice.services.listener;

import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.brewery.model.events.AllocateBeerOrderResult;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.services.BeerOrderManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

/**
 * @author cevher
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AllocateOrderResponseListener {

    private final BeerOrderManager beerOrderManager;

    @Transactional
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateBeerOrderResult result) {
        BeerOrderDto beerOrderDto = result.getBeerOrder();
        if (!result.isAllocationError() && !result.isPendingInventory()) {
            beerOrderManager.beerOrderAllocationPassed(beerOrderDto);
        } else if (!result.isAllocationError()) {
            beerOrderManager.beerOrderAllocationPendingInventory(beerOrderDto);
        } else {
            beerOrderManager.beerOrderAllocationFailed(beerOrderDto);
        }
    }
}
