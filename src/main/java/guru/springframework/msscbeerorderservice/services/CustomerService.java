package guru.springframework.msscbeerorderservice.services;

import guru.springframework.brewery.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

/**
 * @author cevher
 */
public interface CustomerService {
    CustomerPagedList listCustomers(Pageable pageable);
}
