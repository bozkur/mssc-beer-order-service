package guru.springframework.msscbeerorderservice.services.beer;

import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

/**
 * @author cevher
 */
@Slf4j
@Component
public class BeerInventoryServiceRestTemplate implements BeerService {

    private static final String BEER_UPC_SERVICE_PATH_V1 = "/api/v1/beerUpc/{upc}";
    private static final String BEER_SERVICE_PATH_V1 = "/api/v1/beer/{beerId}";
    private final RestTemplate restTemplate;


    @Value("${sfg.brewery.beer-service-host}")
    private String beerServiceHost;

    public BeerInventoryServiceRestTemplate(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc) {
        String url = beerServiceHost + BEER_UPC_SERVICE_PATH_V1;
        return Optional.ofNullable(getBeerDtoResource(url, upc));
    }

    private BeerDto getBeerDtoResource(String url, Object queryItem) {
        ResponseEntity<BeerDto> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                }, queryItem);
        return response.getBody();
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID beerId) {
        String url = beerServiceHost + BEER_SERVICE_PATH_V1;
        return Optional.ofNullable(getBeerDtoResource(url, beerId));
    }
}
