package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.bootstrap.BeerOrderBootStrap;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TastingRoomServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BeerOrderService beerOrderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeTastingRoomOrder() {
        TastingRoomService roomService = new TastingRoomService(customerRepository, beerOrderService);
        List<Customer> customerList = createCustomer();
        when(customerRepository.findAllByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM)).thenReturn(customerList);

        roomService.placeTastingRoomOrder();

        verify(beerOrderService).placeOrders(ArgumentMatchers.eq(customerList.get(0).getId()), ArgumentMatchers.any());
    }

    private List<Customer> createCustomer() {
        return Arrays.asList(Customer.builder()
                .customerName("Cevher")
                .id(UUID.randomUUID()).build());
    }
}