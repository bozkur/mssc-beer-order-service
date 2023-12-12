package guru.springframework.msscbeerorderservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import guru.springframework.brewery.model.BeerDto;
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

}
