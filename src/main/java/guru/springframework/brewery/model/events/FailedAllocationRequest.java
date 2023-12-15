package guru.springframework.brewery.model.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author cevher
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedAllocationRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 8736413399725921380L;
    private String orderId;
}
