package guru.springframework.brewery.model.events;

import guru.springframework.brewery.model.BeerOrderDto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author cevher
 */
@Data
@RequiredArgsConstructor
@Builder
public class AllocateBeerOrderResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 2970399542510991054L;

    private final BeerOrderDto beerOrder;
    private final boolean allocationError;
    private final boolean pendingInventory;
}
