package guru.springframework.msscbeerorderservice.services;

import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author cevher
 */
@SpringBootTest
class BeerOrderManagerImplTest {

    @MockBean
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private BeerOrderManager orderManager;

    @BeforeAll
    static void setupBeforeClass() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(61616));
        } catch (IOException e) {
           org.junit.jupiter.api.Assumptions.assumeTrue(false);
        }
    }

    @Test
    void testNewBeerOder() {
        BeerOrder order = BeerOrder.builder().id(UUID.randomUUID()).build();
        when(beerOrderRepository.save(ArgumentMatchers.any())).thenReturn(order);
        when(beerOrderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        orderManager.newBeerOrder(BeerOrder.builder().build());

        ArgumentCaptor<BeerOrder> beerOrderArgumentCaptor = ArgumentCaptor.forClass(BeerOrder.class);
        Mockito.verify(beerOrderRepository).saveAndFlush(beerOrderArgumentCaptor.capture());
        BeerOrder capturedOrder = beerOrderArgumentCaptor.getValue();
        assertThat(capturedOrder.getOrderStatus(), Matchers.equalTo(BeerOrderStatus.VALIDATION_PENDING));
    }
}