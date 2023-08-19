package guru.springframework.msscbeerorderservice.services.beer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author cevher
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerDto {
    private String beerName;
    private String upc;
    private UUID id;
    private String beerStyle;
    private BigDecimal beerPrice;
}
