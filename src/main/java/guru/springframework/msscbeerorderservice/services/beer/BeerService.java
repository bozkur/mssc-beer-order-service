package guru.springframework.msscbeerorderservice.services.beer;

import guru.springframework.msscbeerorderservice.services.beer.model.BeerDto;

import java.util.Optional;
import java.util.UUID;

/**
 * @author cevher
 */
public interface BeerService {

    Optional<BeerDto> getBeerByUpc(String upc);

    Optional<BeerDto> getBeerById(UUID beerId);
}
