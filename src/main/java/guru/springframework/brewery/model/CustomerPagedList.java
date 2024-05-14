package guru.springframework.brewery.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.Serial;
import java.util.List;

/**
 * @author cevher
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class CustomerPagedList extends PageImpl<CustomerDto> {

    @Serial
    private static final long serialVersionUID = 2127221190756502812L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomerPagedList(@JsonProperty("content") List<CustomerDto> content,
                             @JsonProperty("number") int page,
                             @JsonProperty("size") int size,
                             @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page, size), total);
    }

    public CustomerPagedList(List<CustomerDto> content) {
        super(content);
    }
}
