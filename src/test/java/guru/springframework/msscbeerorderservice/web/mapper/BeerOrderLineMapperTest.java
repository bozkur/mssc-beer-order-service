package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderLineDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = { BeerOrderLineMapperImpl.class, DateMapper.class })
class BeerOrderLineMapperTest {

    @Autowired
    private BeerOrderLineMapper mapper;

    @Autowired
    private DateMapper dateMapper;

    @Test
    @DisplayName("Convert dto to domain object")
    void shouldConvertDtoToDomainObject() {
        BeerOrderLineDto dto = BeerOrderLineDto.builder()
                .upc(UUID.randomUUID().toString())
                .beerId(UUID.randomUUID())
                .beerName("Efes Pilsen")
                .orderQuantity(10)
                .version(1)
                .lastModifiedDate(OffsetDateTime.now())
                .createdDate(OffsetDateTime.now())
                .build();

        BeerOrderLine domain = mapper.dtoToBeerOrderLine(dto);

        assertThat(domain.getBeerId(), Matchers.equalTo(dto.getBeerId()));
        assertThat(domain.getOrderQuantity(), Matchers.equalTo(dto.getOrderQuantity()));
        assertThat(domain.getVersion().intValue(), Matchers.equalTo(dto.getVersion()));
        assertThat(domain.getLastModifiedDate(), Matchers.equalTo(dateMapper.offsetDateTime2TimeStamp(dto.getLastModifiedDate())));
        assertThat(domain.getCreatedDate(), Matchers.equalTo(dateMapper.offsetDateTime2TimeStamp(dto.getCreatedDate())));
    }

    @Test
    @DisplayName("Convert domain object to dto")
    void shouldConvertDomainToDto() {
        BeerOrderLine domain = new BeerOrderLine();
        domain.setId(UUID.randomUUID());
        domain.setBeerId(UUID.randomUUID());
        domain.setVersion(1L);
        domain.setCreatedDate(Timestamp.from(Instant.now()));
        domain.setOrderQuantity(10);
        domain.setLastModifiedDate(Timestamp.from(Instant.now()));

        BeerOrderLineDto dto = mapper.beerOrderLineToDto(domain);

        assertThat(dto.getId(), Matchers.equalTo(domain.getId()));
        assertThat(dto.getBeerId(), Matchers.equalTo(domain.getBeerId()));
        assertThat(dto.getVersion(), Matchers.equalTo(domain.getVersion().intValue()));
        assertThat(dto.getCreatedDate(), Matchers.equalTo(dateMapper.timeStamp2OffsetDateTime(domain.getCreatedDate())));
        assertThat(dto.getLastModifiedDate(), Matchers.equalTo(dateMapper.timeStamp2OffsetDateTime(domain.getLastModifiedDate())));
        assertThat(dto.getOrderQuantity(), Matchers.equalTo(domain.getOrderQuantity()));
    }

}