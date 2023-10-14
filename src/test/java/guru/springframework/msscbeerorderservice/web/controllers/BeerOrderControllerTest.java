package guru.springframework.msscbeerorderservice.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbeerorderservice.services.BeerOrderService;
import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.brewery.model.BeerOrderLineDto;
import guru.springframework.brewery.model.BeerOrderPagedList;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    private static final String BEER_ORDER_API_URL = "/api/v1/customers";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeerOrderService orderService;

    @Test
    @DisplayName("List orders")
    void listOrders() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(orderService.listOrders(ArgumentMatchers.eq(customerId), ArgumentMatchers.any())).thenReturn(createPagedList(customerId));

        mockMvc.perform(MockMvcRequestBuilders.get(BEER_ORDER_API_URL + "/{customerId}/orders", customerId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].beerOrderLines.size()", Matchers.equalTo(2)));
    }

    private BeerOrderPagedList createPagedList(UUID customerId) {
        BeerOrderDto order1 = createBeerOrder(customerId);
        BeerOrderDto order2 = createBeerOrder(customerId);
        return new BeerOrderPagedList(Arrays.asList(order1, order2));
    }

    private BeerOrderDto createBeerOrder(UUID customerId) {
        BeerOrderLineDto ol1 = getOrderLine(UUID.randomUUID(), 5, "Efes");
        BeerOrderLineDto ol2 = getOrderLine(UUID.randomUUID(), 3, "Edelmeister");
        return BeerOrderDto.builder()
                .beerOrderLines(Arrays.asList(ol1, ol2))
                .customerId(customerId)
                .build();
    }

    private static BeerOrderLineDto getOrderLine(UUID beerId, int orderQuantity, String efes) {
        return BeerOrderLineDto.builder()
                .beerId(beerId)
                .orderQuantity(orderQuantity)
                .beerName(efes)
                .build();
    }

    @Test
    @DisplayName("Place new order")
    void placeOrder() throws Exception {
        UUID customerId = UUID.randomUUID();
        BeerOrderDto beerOrder = createBeerOrder(customerId);

        mockMvc.perform(MockMvcRequestBuilders.post(BEER_ORDER_API_URL + "/{customerId}/orders", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrder)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void getOrder() throws Exception {
        UUID customerId = UUID.randomUUID();
        BeerOrderDto beerOrder = createBeerOrder(customerId);
        beerOrder.setId(UUID.randomUUID());
        when(orderService.getOrderById(customerId, beerOrder.getId())).thenReturn(beerOrder);
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_ORDER_API_URL + "/{customerId}/orders/{orderId}", customerId, beerOrder.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId", Matchers.equalTo(customerId.toString())));

    }

    @Test
    void pickupOrder() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.put(BEER_ORDER_API_URL + "/{customerId}/orders/{orderId}", customerId, orderId))
                        .andExpect(MockMvcResultMatchers.status().isNoContent());
        verify(orderService).pickOrder(customerId, orderId);
    }
}