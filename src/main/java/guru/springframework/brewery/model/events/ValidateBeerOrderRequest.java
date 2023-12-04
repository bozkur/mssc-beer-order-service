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
public class ValidateBeerOrderRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2077758097086543090L;
    private BeerOrderDto beerOrderDto;

}
