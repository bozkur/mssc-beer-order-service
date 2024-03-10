package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.brewery.model.BeerDto;
import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.brewery.model.BeerOrderLineDto;
import guru.springframework.msscbeerorderservice.domain.BeerOrder;
import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.domain.BeerOrderStatus;
import guru.springframework.msscbeerorderservice.services.beer.BeerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class BeerOrderMapperTest {

    private final String upc = "12345";

    @Autowired
    private BeerOrderMapper mapper;

    @Autowired
    private DateMapper dateMapper;

    @MockBean
    private BeerService beerService;

    @Test
    void shouldConvertDomainToDto() {
        BeerDto beerDto = BeerDto.builder().id(UUID.randomUUID()).upc(upc).build();
        when(beerService.getBeerByUpc(beerDto.getUpc())).thenReturn(Optional.of(beerDto));
        BeerOrderLine line1 = createDomainLine();
        BeerOrderLine line2 = createDomainLine();

        BeerOrder order = BeerOrder.builder()
                .beerOrderLines(Arrays.asList(line1, line2))
                .id(UUID.randomUUID())
                .version(1L)
                .lastModifiedDate(Timestamp.from(Instant.now()))
                .createdDate(Timestamp.from(Instant.now()))
                .orderStatus(BeerOrderStatus.NEW)
                .build();

        BeerOrderDto dto = mapper.beerOrderToDto(order);

        List<BeerOrderLineDto> orderLineDtoList = dto.getBeerOrderLines();
        assertThat(orderLineDtoList.size(), Matchers.equalTo(2));
        assertThat(orderLineDtoList.get(0).getId(), Matchers.equalTo(line1.getId()));
        assertThat(orderLineDtoList.get(1).getId(), Matchers.equalTo(line2.getId()));
        assertThat(dto.getCreatedDate(), Matchers.equalTo(dateMapper.timeStamp2OffsetDateTime(order.getCreatedDate())));
        assertThat(dto.getLastModifiedDate(), Matchers.equalTo(dateMapper.timeStamp2OffsetDateTime(order.getLastModifiedDate())));
        assertThat(dto.getOrderStatus(), Matchers.equalTo(order.getOrderStatus()));
        assertThat(dto.getId(), Matchers.equalTo(order.getId()));
        assertThat(dto.getVersion(), Matchers.equalTo(order.getVersion().intValue()));
    }

    private BeerOrderLine createDomainLine() {
        return BeerOrderLine.builder()
                .beerId(UUID.randomUUID())
                .upc(upc)
                .build();
    }

    @Test
    void shouldConvertDtoToDomain() {
        BeerOrderLineDto dtoline1 = createLineItem();
        BeerOrderLineDto dtoline2 = createLineItem();

        BeerOrderDto beerDto = BeerOrderDto.builder()
                .customerId(UUID.randomUUID())
                .beerOrderLines(Arrays.asList(dtoline1, dtoline2))
                .lastModifiedDate(OffsetDateTime.now())
                .createdDate(OffsetDateTime.now())
                .version(1)
                .orderStatus(BeerOrderStatus.NEW)
                .id(UUID.randomUUID())
                .build();

        BeerOrder domain = mapper.dtoToBeerOrder(beerDto);

        List<BeerOrderLine> domainOrderLines = domain.getBeerOrderLines();
        assertThat(domainOrderLines.size(), Matchers.equalTo(2));
        assertThat(domainOrderLines.get(0).getId(), Matchers.equalTo(dtoline1.getId()));
        assertThat(domainOrderLines.get(1).getId(), Matchers.equalTo(dtoline2.getId()));
        assertThat(domain.getOrderStatus(), Matchers.equalTo(beerDto.getOrderStatus()));
        assertThat(domain.getCreatedDate(), Matchers.equalTo(dateMapper.offsetDateTime2TimeStamp(beerDto.getCreatedDate())));
        assertThat(domain.getLastModifiedDate(), Matchers.equalTo(dateMapper.offsetDateTime2TimeStamp(beerDto.getLastModifiedDate())));
    }

    private BeerOrderLineDto createLineItem() {
        return BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .build();
    }
}