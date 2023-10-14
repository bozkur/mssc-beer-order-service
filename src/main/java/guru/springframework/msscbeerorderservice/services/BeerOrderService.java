package guru.springframework.msscbeerorderservice.services;

import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.brewery.model.BeerOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BeerOrderService {
    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);
    BeerOrderDto placeOrders(UUID customerId, BeerOrderDto beerOrderDto);
    BeerOrderDto getOrderById(UUID customerId, UUID orderId);
    void pickOrder(UUID customerId, UUID orderId);
}
