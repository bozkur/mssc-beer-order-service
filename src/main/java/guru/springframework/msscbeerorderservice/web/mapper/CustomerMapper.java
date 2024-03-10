package guru.springframework.msscbeerorderservice.web.mapper;

import guru.springframework.brewery.model.CustomerDto;
import guru.springframework.msscbeerorderservice.domain.Customer;
import org.mapstruct.Mapper;

/**
 * @author cevher
 */
@Mapper (uses = {BeerOrderMapper.class, DateMapper.class, BeerOrderLineMapper.class})
public interface CustomerMapper {

    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(CustomerDto dto);
}
