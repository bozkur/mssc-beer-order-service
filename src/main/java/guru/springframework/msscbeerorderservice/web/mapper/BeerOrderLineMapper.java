package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.msscbeerorderservice.domain.BeerOrderLine;
import guru.springframework.brewery.model.BeerOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(BeerOrderMapperDecorator.class)
public interface BeerOrderLineMapper {

    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine beerOrderLine);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}
