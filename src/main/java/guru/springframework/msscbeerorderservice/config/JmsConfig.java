package guru.springframework.msscbeerorderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * @author cevher
 */
@Configuration
public class JmsConfig {

    /**Name of the message queue which holds validate order messages. */
    public static final String VALIDATE_ORDER_QUEUE = "validate-order";
    public static final String ALLOCATE_ORDER_QUEUE = "allocate-order";
    public static final String ALLOCATE_ORDER_RESPONSE_QUEUE = "allocate-order-response";
    public static final String VALIDATE_ORDER_RESULT_QUEUE = "validate-order-result";

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("_type");
        messageConverter.setTargetType(MessageType.TEXT);
        return messageConverter;
    }
}
