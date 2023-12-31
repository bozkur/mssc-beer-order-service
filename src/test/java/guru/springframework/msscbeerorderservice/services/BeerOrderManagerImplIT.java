package guru.springframework.msscbeerorderservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import guru.springframework.brewery.model.BeerDto;
import guru.springframework.brewery.model.events.DeallocateBeerOrderRequest;
import guru.springframework.brewery.model.events.FailedAllocationRequest;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author cevher
 */
@SpringBootTest
@EnableWireMock({
        @ConfigureWireMock(name = "user-service", port = 9000)
})
//@ContextConfiguration(classes=BeanTestConfig.class, loader= AnnotationConfigContextLoader.class)
public class BeerOrderManagerImplIT {


    @Autowired
    private BeerOrderManager beerOrderManager;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    private UUID beerId = UUID.randomUUID();

    @InjectWireMock("user-service")
    private WireMockServer wiremock;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JmsTemplate jmsTemplate;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder()
                        .customerName("Test Customer").build());
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException {
        newToAllocatedTest();
    }

    private BeerOrder newToAllocatedTest() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wiremock.stubFor(get("/api/v1/beerUpc/").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        assertNotNull(savedBeerOrder);
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.ALLOCATED, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
        return savedBeerOrder;
    }

    private BeerOrder createBeerOrder() {
        BeerOrderLine ol1 = BeerOrderLine.builder().beerId(beerId).orderQuantity(3).build();
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .beerOrderLines(List.of(ol1)).build();
        ol1.setBeerOrder(beerOrder);
        return beerOrder;

    }

    @Test
    void testPickupOrder() throws Exception {
        BeerOrder savedBeerOrder = newToAllocatedTest();

        beerOrderManager.pickupOrder(savedBeerOrder.getId());
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.PICKED_UP, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
    }

    @Test
    void testInvalidOrder() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wiremock.stubFor(get("/api/v1/beerUpc/").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("invalid-customer");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        assertNotNull(savedBeerOrder);
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.VALIDATION_EXCEPTION, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
    }

    @Test
    void testAllocationFailed() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wiremock.stubFor(get("/api/v1/beerUpc/").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("failed-allocation");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        assertNotNull(savedBeerOrder);
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.ALLOCATION_EXCEPTION, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
        FailedAllocationRequest request = (FailedAllocationRequest) jmsTemplate.receiveAndConvert(JmsConfig.FAILED_ALLOCATION_QUEUE);
        assertNotNull(request);
        assertEquals(beerOrder.getId().toString(), request.getOrderId());
    }

    @Test
    void testPartialAllocation() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wiremock.stubFor(get("/api/v1/beerUpc/").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("partial-allocation");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        assertNotNull(savedBeerOrder);
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.PENDING_INVENTORY, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
    }

    @Test
    void testMakeTransitionFromValidationPendingToCancelled() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wiremock.stubFor(get("/api/v1/beerUpc/").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("will-be-cancelled-validation");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.VALIDATION_PENDING, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );

        beerOrderManager.cancelOrder(savedBeerOrder.getId());
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.CANCELLED, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
    }

    @Test
    void testMakeTransitionFromAllocationPendingToCancelled() throws JsonProcessingException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();
        wiremock.stubFor(get("/api/v1/beerUpc/").willReturn(okJson(objectMapper.writeValueAsString(beerDto))));
        BeerOrder beerOrder = createBeerOrder();
        beerOrder.setCustomerRef("will-be-cancelled-allocation");

        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.ALLOCATION_PENDING, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );

        beerOrderManager.cancelOrder(savedBeerOrder.getId());
        await().untilAsserted(() ->
                assertEquals(BeerOrderStatus.CANCELLED, beerOrderRepository.findById(savedBeerOrder.getId()).get().getOrderStatus())
        );
    }

    @Test
    void testSendACancelOrderMesssageWhenAllocatedOrderIsCancelled() throws JsonProcessingException {
        BeerOrder beerOrder = newToAllocatedTest();
        beerOrderManager.cancelOrder(beerOrder.getId());
        DeallocateBeerOrderRequest request = (DeallocateBeerOrderRequest) jmsTemplate.receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);
        assertNotNull(request);
        assertEquals(beerOrder.getId(), request.getBeerOrderDto().getId());
    }

}
