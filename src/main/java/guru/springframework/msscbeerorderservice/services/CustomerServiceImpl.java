package guru.springframework.msscbeerorderservice.services;

import guru.springframework.brewery.model.CustomerDto;
import guru.springframework.brewery.model.CustomerPagedList;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import guru.springframework.msscbeerorderservice.web.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cevher
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        List<CustomerDto> dtoList = customerPage.stream().map(customerMapper::customerToDto).collect(Collectors.toList());
        PageRequest pageRequest = PageRequest.of(customerPage.getPageable().getPageNumber(), customerPage.getPageable().getPageSize());
        return new CustomerPagedList(dtoList, pageRequest, customerPage.getTotalElements());
    }
}
