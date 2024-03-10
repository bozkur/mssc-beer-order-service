package guru.springframework.msscbeerorderservice.web.controllers;

import guru.springframework.brewery.model.CustomerDto;
import guru.springframework.brewery.model.CustomerPagedList;
import guru.springframework.msscbeerorderservice.services.CustomerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;

/**
 * @author cevher
 */
@ExtendWith({SpringExtension.class})
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    private static final String CUSTOMER_API_URL = "/api/v1/customers";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("List customers")
    void listCustomers() throws Exception {
        List<CustomerDto> customerDtoList = createCustomerList();

        when(customerService.listCustomers(ArgumentMatchers.any())).thenReturn(new CustomerPagedList(customerDtoList));
        mockMvc.perform(MockMvcRequestBuilders.get(CUSTOMER_API_URL + "/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].customerName", Matchers.equalTo(customerDtoList.get(0).getCustomerName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].customerName", Matchers.equalTo(customerDtoList.get(1).getCustomerName())));

    }

    private List<CustomerDto> createCustomerList() {
        CustomerDto customer1 = createCustomer("Bob");
        CustomerDto customer2 = createCustomer("Alice");
        return List.of(customer1, customer2);
    }

    private CustomerDto createCustomer(String name) {
        return CustomerDto.builder().customerName(name).build();
    }
}
