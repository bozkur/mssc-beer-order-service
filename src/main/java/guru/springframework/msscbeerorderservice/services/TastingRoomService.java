package guru.springframework.msscbeerorderservice.services;

import guru.springframework.brewery.model.BeerOrderDto;
import guru.springframework.brewery.model.BeerOrderLineDto;
import guru.springframework.msscbeerorderservice.domain.Customer;
import guru.springframework.msscbeerorderservice.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

import static guru.springframework.msscbeerorderservice.bootstrap.BeerOrderBootStrap.TASTING_ROOM;

@Slf4j
@Service
public class TastingRoomService {

    public static final String BEER1_UPC = "0631234200036";
    public static final String BEER2_UPC = "0631234300019";
    public static final String BEER3_UPC = "0083783375213";

    private final CustomerRepository customerRepository;
    private final BeerOrderService beerOrderService;
    private final List<String> beerUpcs = new ArrayList<>(3);

    public TastingRoomService(CustomerRepository customerRepository, BeerOrderService beerOrderService) {
        this.customerRepository = customerRepository;
        this.beerOrderService = beerOrderService;
        beerUpcs.addAll(Arrays.asList(BEER1_UPC, BEER2_UPC, BEER3_UPC));
    }

    @Transactional
    @Scheduled(fixedRate = 2000)
    public void placeTastingRoomOrder() {
        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(TASTING_ROOM);
        if (customerList.size() == 1) {
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String beerUpc = getRandomUpc();
        BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
                .upc(beerUpc)
                .orderQuantity(new Random().nextInt(6))
                .build();
        List<BeerOrderLineDto> beerOrderLineList = new ArrayList<>();
        beerOrderLineList.add(beerOrderLine);

        BeerOrderDto beerOrder = BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .beerOrderLines(beerOrderLineList)
                .build();
        beerOrderService.placeOrders(customer.getId(), beerOrder);

    }

    private String getRandomUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size()));
    }
}
