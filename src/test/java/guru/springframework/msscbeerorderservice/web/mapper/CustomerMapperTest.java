package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.brewery.model.BeerDto;
import guru.springframework.brewery.model.CustomerDto;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.services.beer.BeerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author cevher
 */
@SpringBootTest
class CustomerMapperTest {

    private final UUID beerId = UUID.randomUUID();
    private final String upc = "12345";

    @MockBean
    private BeerService beerService;

    @Autowired
    private CustomerMapper customerMapper;
    @Test
    void customerToDto() {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();
        when(beerService.getBeerByUpc(beerDto.getUpc())).thenReturn(Optional.of(beerDto));
        Customer customer = Customer.builder().customerName("name")
                .apiKey(UUID.randomUUID())
                .beerOrders(Set.of(createBeerOrder())).build();

        CustomerDto customerDto = customerMapper.customerToDto(customer);
        assertThat(customerDto.getCustomerName(), Matchers.equalTo(customer.getCustomerName()));
        assertThat(customerDto.getApiKey(), Matchers.equalTo(customer.getApiKey()));
        assertThat(customerDto.getBeerOrders().size(), Matchers.equalTo(customer.getBeerOrders().size()));
    }

    private BeerOrder createBeerOrder() {
        return BeerOrder.builder()
                .beerOrderLines(createBeerOrderLines())
                .build();
    }

    private List<BeerOrderLine> createBeerOrderLines() {
        BeerOrderLine line1 = createBeerOrderLine();
        BeerOrderLine line2 = createBeerOrderLine();
        return Arrays.asList(line1, line2);
    }

    private BeerOrderLine createBeerOrderLine() {
        return BeerOrderLine.builder()
                .upc(upc)
                .beerId(beerId).build();
    }
}