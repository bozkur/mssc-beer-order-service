package guru.springframework.msscbeerorderservice.sm.actions;

import guru.springframework.brewery.model.events.AllocateBeerOrderRequest;
import guru.springframework.msscbeerorderservice.config.JmsConfig;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderEvent;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.repositories.BeerOrderRepository;
import guru.springframework.msscbeerorderservice.services.BeerOrderManagerImpl;
import guru.springframework.msscbeerorderservice.web.mapper.BeerOrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * @author cevher
 */
@ExtendWith(MockitoExtension.class)
class AllocateOrderActionTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private BeerOrderRepository beerOrderRepository;

    @Mock
    private StateContext<BeerOrderStatus, BeerOrderEvent> stateContext;

    @Spy
    private BeerOrderMapper mapper;

    @InjectMocks
    private AllocateOrderAction orderAction;

    @Test
    void shouldSendJmsMessageWhenContextContainsAValidOrderId() {
        String orderId = UUID.randomUUID().toString();
        when(stateContext.getMessageHeader(ArgumentMatchers.eq(BeerOrderManagerImpl.ORDER_ID_HEADER))).thenReturn(orderId);
        BeerOrder beerOrder = new BeerOrder();
        when(beerOrderRepository.findById(UUID.fromString(orderId))).thenReturn(Optional.of(beerOrder));
        ArgumentCaptor<AllocateBeerOrderRequest> captor = ArgumentCaptor.forClass(AllocateBeerOrderRequest.class);
        doNothing().when(jmsTemplate).convertAndSend(ArgumentMatchers.eq(JmsConfig.ALLOCATE_ORDER_QUEUE), captor.capture());

        orderAction.execute(stateContext);

        AllocateBeerOrderRequest request = captor.getValue();
        assertEquals(mapper.beerOrderToDto(beerOrder), request.getBeerOrderDto());
    }

}