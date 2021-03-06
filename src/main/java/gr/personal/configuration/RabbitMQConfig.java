package gr.personal.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Nick Kanakis on 6/9/2017.
 *
 * There are 2 queues (comments, posts) that are bounded to a direct exchange.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.comment.queue}")
    private String commentQueueName;
    @Value("${rabbitmq.post.queue}")
    private String postQueueName;
    @Value("${rabbitmq.comment.routingKey}")
    private String commentRoutingKey;
    @Value("${rabbitmq.post.routingKey}")
    private String postRoutingKey;
    @Value("${rabbitmq.exchange.direct.name}")
    private String exchangeName;

    @Bean
    public DirectExchange direct(){
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue commentQueue(){
        return new Queue(commentQueueName);
    }

    @Bean
    public Queue postQueue(){
        return new Queue(postQueueName);
    }

    @Bean
    public Binding commentBinding() {
        return BindingBuilder.bind(commentQueue()).to(direct()).with(commentRoutingKey);
    }

    @Bean
    public Binding postBinding() {
        return BindingBuilder.bind(postQueue()).to(direct()).with(postRoutingKey);
    }
}
