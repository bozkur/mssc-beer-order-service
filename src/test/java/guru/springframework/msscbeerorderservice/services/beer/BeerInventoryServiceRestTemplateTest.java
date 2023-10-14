package guru.springframework.msscbeerorderservice.services.beer;

import guru.springframework.brewery.model.BeerDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author cevher
 */
@SpringBootTest
class BeerInventoryServiceRestTemplateTest {

    @Value("${sfg.brewery.beer-service-host}")
    private String beerServiceHost;


    @Autowired
    private BeerService beerService;

    @BeforeEach
    void setUp() {
        Assumptions.assumeTrue(new BeerServiceController().beerServiceListening(beerServiceHost));
    }

    @Test
    @DisplayName("Get beer information with UPC")
    void shouldGerBeerInformationFromBeerService() {
        String upc = "0083783375213";
        BeerDto obtainedBeer = beerService.getBeerByUpc(upc).get();

        assertThat(obtainedBeer.getBeerName(), Matchers.equalTo("Pinball Porter"));
        assertThat(obtainedBeer.getId().toString(), Matchers.equalTo("026cc3c8-3a0c-4083-a05b-e908048c1b08"));
        assertThat(obtainedBeer.getUpc(), Matchers.equalTo(upc));
    }

    @Test
    @DisplayName("Returned information is null when there is no such beer with the given UPC")
    void shouldReturnEmptyOptionalWhenGivenUpcDoesnotCorrespondToABeer() {
        assertTrue(beerService.getBeerByUpc("111").isEmpty());
    }

    @Test
    @DisplayName("Get beer information with id")
    void shouldGetBeerInformationFromBeerServiceWithBeerId() {
        UUID beerId = UUID.fromString("026cc3c8-3a0c-4083-a05b-e908048c1b08");
        BeerDto obtainedBeer = beerService.getBeerById(beerId).get();
        assertThat(obtainedBeer.getBeerName(), Matchers.equalTo("Pinball Porter"));
        assertThat(obtainedBeer.getId(), Matchers.equalTo(beerId));
        assertThat(obtainedBeer.getUpc(), Matchers.equalTo("0083783375213"));
    }


    //TODO: Bulunamayan bira icin NotFoundException atiliyor ve sunucu tarafindan hata geliyor.
    //O nedenle bu durumu daha sonra kotaracagiz...
    @Disabled
    @Test
    @DisplayName("Returned information is null when there is no such beer with the given Id")
    void shouldReturnEmptyOptionalWhenGivenBeerIdDoesnotCorrespondToABeer() {
        UUID beerId = UUID.randomUUID();
        assertTrue(beerService.getBeerById(beerId).isEmpty());
    }
}