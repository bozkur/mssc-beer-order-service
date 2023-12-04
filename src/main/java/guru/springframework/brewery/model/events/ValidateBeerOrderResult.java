package guru.springframework.brewery.model.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author cevher
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateBeerOrderResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 3203599521339753454L;

    private UUID orderId;
    private boolean isValid;
}
