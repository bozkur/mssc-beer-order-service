package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.msscbeerorderservice.services.beer.BeerService;
import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;
import guru.springframework.msscbeerorderservice.web.model.BeerOrderLineDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author cevher
 */
public abstract class BeerOrderMapperDecorator implements BeerOrderLineMapper {

    @Autowired
    private BeerService beerService;

    @Autowired
    private BeerOrderLineMapper orderLineMapper;

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine beerOrderLine) {
        BeerOrderLineDto dto = orderLineMapper.beerOrderLineToDto(beerOrderLine);
        Optional<BeerDto> beerDtoOptional = beerService.getBeerByUpc(dto.getUpc());
        beerDtoOptional.ifPresent(beerDto -> {
            dto.setBeerId(beerDto.getId());
            dto.setBeerName(beerDto.getBeerName());
            dto.setBeerId(beerDto.getId());
            dto.setBeerStyle(beerDto.getBeerStyle());
            dto.setBeerPrice(beerDto.getBeerPrice());
        });
        return dto;
    }
}
