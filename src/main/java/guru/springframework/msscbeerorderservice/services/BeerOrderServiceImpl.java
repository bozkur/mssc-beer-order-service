package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.domain.OrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import guru.springframework.msscbeerorderservice.web.mapper.BeerOrderMapper;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderDto;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService {
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper objectMapper;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository, BeerOrderMapper objectMapper) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            return null;
        }
        Page<BeerOrder> beerOrderPage = beerOrderRepository.findAllByCustomer(customer.get(), pageable);
        List<BeerOrderDto> listOfBeerOrders = beerOrderPage.stream().map(objectMapper::beerOrderToDto).collect(Collectors.toList());
        PageRequest pageRequest = PageRequest.of(beerOrderPage.getPageable().getPageNumber(), beerOrderPage.getPageable().getPageSize());
        return new BeerOrderPagedList(listOfBeerOrders, pageRequest, beerOrderPage.getTotalElements());
    }

    @Override
    public BeerOrderDto placeOrders(UUID customerId, BeerOrderDto beerOrderDto) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if(customer.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        BeerOrder beerOrder = objectMapper.dtoToBeerOrder(beerOrderDto);
        beerOrder.setId(null);
        beerOrder.setCustomer(customer.get());
        beerOrder.setOrderStatus(OrderStatus.NEW);

        beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
        log.debug("Saved beer order: {}", savedBeerOrder);
        //TODO: Publish event
        return objectMapper.beerOrderToDto(savedBeerOrder);
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return objectMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(OrderStatus.PICKED_UP);
        beerOrderRepository.save(beerOrder);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if(customer.isEmpty()) {
            throw new RuntimeException("Customer not found");
        }
        Optional<BeerOrder> optionalOrder = beerOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Beer Order Not Found");
        }
        if (optionalOrder.get().getCustomer().getId().equals(customerId)) {
            return optionalOrder.get();
        } else {
            throw new RuntimeException("Beer Order Not Found");
        }
    }
}
