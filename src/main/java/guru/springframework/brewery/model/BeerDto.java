package guru.springframework.brewery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class BeerDto {
    private String beerName;
    private String upc;
    private UUID id;
    private String beerStyle;
    private BigDecimal beerPrice;
}
