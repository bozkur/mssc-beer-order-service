package guru.springframework.msscbeerorderservice.services;

import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;

import java.util.UUID;

/**
 * @author cevher
 */
public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);

    void processValidationResult(UUID orderId, boolean isValid);

    void beerOrderAllocationPassed(BeerOrderDto beerOrderDto);

    void beerOrderAllocationPendingInventory(BeerOrderDto beerOrderDto);

    void beerOrderAllocationFailed(BeerOrderDto beerOrderDto);
}
