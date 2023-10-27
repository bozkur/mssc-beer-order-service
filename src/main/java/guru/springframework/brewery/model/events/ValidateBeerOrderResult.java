package guru.springframework.brewery.model.events;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author cevher
 */
@Data
@RequiredArgsConstructor
public class ValidateBeerOrderResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 3203599521339753454L;

    private final UUID orderId;
    private final boolean isValid;
}
