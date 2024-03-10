package guru.springframework.brewery.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serial;
import java.util.List;

/**
 * @author cevher
 */
public class CustomerPagedList extends PageImpl<CustomerDto> {

    @Serial
    private static final long serialVersionUID = 2127221190756502812L;

    public CustomerPagedList(List<CustomerDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CustomerPagedList(List<CustomerDto> content) {
        super(content);
    }
}
