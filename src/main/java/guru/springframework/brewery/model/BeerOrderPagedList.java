package guru.springframework.brewery.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class BeerOrderPagedList extends PageImpl<BeerOrderDto> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BeerOrderPagedList(@JsonProperty("content") List<BeerOrderDto> content,
                              @JsonProperty("number") int page,
                              @JsonProperty("size") int size,
                              @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page,size), total);
    }

    public BeerOrderPagedList(List<BeerOrderDto> content) {
        super(content);
    }
}
