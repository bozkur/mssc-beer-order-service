package guru.springframework.msscbeerorderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * @author cevher
 */
@EnableJms
@Configuration
public class JmsConfig {

    /**Name of the message queue which holds validate order messages. */
    public static final String VALIDATE_ORDER_QUEUE = "validate-order";
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order";
    public static final String ALLOCATE_ORDER_RESPONSE_QUEUE = "allocate-order-response";
    public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";
    public static final String FAILED_ALLOCATION_QUEUE = "failed-allocation-queue";

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("_type");
        messageConverter.setTargetType(MessageType.TEXT);
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }
}
