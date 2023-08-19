package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.services.beer.BeerService;
import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderLineDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author cevher
 */
@SpringBootTest
class BeerOrderMapperDecoratorTest {

    @MockBean
    private BeerService beerService;
    @Autowired
    private BeerOrderMapperDecorator mapper;

    @Test
    void shouldMapToDto() {
        String upc = "111222";
        BeerDto beerDto = new BeerDto();
        beerDto.setId(UUID.randomUUID());
        beerDto.setBeerName("Efes Pilsen");
        beerDto.setUpc(upc);
        Mockito.when(beerService.getBeerByUpc(upc)).thenReturn(Optional.of(beerDto));

        BeerOrderLine beerOrderLine = BeerOrderLine.builder().upc(upc).build();
        BeerOrderLineDto obtainedDto = mapper.beerOrderLineToDto(beerOrderLine);
        assertThat(obtainedDto.getBeerName(), Matchers.equalTo(beerDto.getBeerName()));
        assertThat(obtainedDto.getBeerId(), Matchers.equalTo(beerDto.getId()));
        assertThat(obtainedDto.getUpc(), Matchers.equalTo(beerDto.getUpc()));
    }


}