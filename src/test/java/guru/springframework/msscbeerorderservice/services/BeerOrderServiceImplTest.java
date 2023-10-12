package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import guru.springframework.msscbeerorderservice.web.mapper.BeerOrderMapper;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderDto;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderLineDto;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderPagedList;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BeerOrderServiceImplTest {

    @Mock
    private BeerOrderRepository repository;

    @Mock
    private CustomerRepository customerRepository;

    @Autowired
    private BeerOrderMapper mapper;

    private BeerOrderService beerOrderService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        beerOrderService = new BeerOrderServiceImpl(repository, customerRepository, mapper);
        customer = Customer.builder()
                .customerName("Cevher")
                .id(UUID.randomUUID())
                .build();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
    }
    @Test
    @DisplayName("List orders")
    void listOrders() {
        Pageable pageable = Pageable.ofSize(5);
        BeerOrder order1 = createOrder(customer);
        BeerOrder order2 = createOrder(customer);
        Page<BeerOrder> beerOrderPage = new PageImpl<>(Arrays.asList(order1, order2), Pageable.ofSize(1), 1);
        when(repository.findAllByCustomer(customer, pageable)).thenReturn(beerOrderPage);

        BeerOrderPagedList pagedOrderList = beerOrderService.listOrders(customer.getId(), pageable);

        List<BeerOrderDto> orders = pagedOrderList.getContent();

        assertThat(orders.size(), Matchers.equalTo(2));
        assertThat(orders.get(0).getId(), Matchers.equalTo(order1.getId()));
        assertThat(orders.get(1).getId(), Matchers.equalTo(order2.getId()));
    }

    private BeerOrder createOrder(Customer customer) {
        return BeerOrder.builder().customer(customer).orderStatus(BeerOrderStatus.NEW).id(UUID.randomUUID()).build();
    }

    @Test
    @DisplayName("Place orders")
    void placeOrders() {
        BeerOrderDto beerOrderDto = createBeerOrder();

        beerOrderService.placeOrders(customer.getId(), beerOrderDto);

        verify(repository).saveAndFlush(ArgumentMatchers.any());
    }

    private BeerOrderDto createBeerOrder() {
        BeerOrderLineDto item1 = createOrderItemLine();
        BeerOrderLineDto item2 = createOrderItemLine();
        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .beerOrderLines(Arrays.asList(item1, item2))
                .build();
    }

    private BeerOrderLineDto createOrderItemLine() {
        return BeerOrderLineDto.builder()
                .beerId(UUID.randomUUID())
                .build();
    }

    @Test
    @DisplayName("Get order by given customer id and order id")
    void getOrderById() {
        BeerOrder beerOrder = createOrder(customer);
        when(repository.findById(beerOrder.getId())).thenReturn(Optional.of(beerOrder));

        BeerOrderDto dto = beerOrderService.getOrderById(customer.getId(), beerOrder.getId());

        assertThat(dto.getId(), Matchers.equalTo(beerOrder.getId()));
    }

    @Test
    @DisplayName("Pick a specific order")
    void pickOrder() {
        BeerOrder beerOrder = createOrder(customer);
        when(repository.findById(beerOrder.getId())).thenReturn(Optional.of(beerOrder));

        beerOrderService.pickOrder(customer.getId(), beerOrder.getId());

        ArgumentCaptor<BeerOrder> captor = ArgumentCaptor.forClass(BeerOrder.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getOrderStatus(), Matchers.equalTo(BeerOrderStatus.PICKED_UP));
    }
}