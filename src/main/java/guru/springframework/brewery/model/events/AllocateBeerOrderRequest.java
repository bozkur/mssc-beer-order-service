package guru.springframework.brewery.model.events;

import guru.springframework.brewery.model.BeerOrderDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author cevher
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllocateBeerOrderRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7513966075503896945L;
    private BeerOrderDto beerOrderDto;
}
