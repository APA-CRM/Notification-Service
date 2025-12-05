package com.crm.notification.notification_service.config;

import com.crm.sharedlib.messaging.config.BaseRabbitMQConfig;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.crm.sharedlib.messaging.constants.RabbitMQConstants.*;

@Configuration
@EnableRabbit
public class RabbitMQConfig extends BaseRabbitMQConfig {

    @Bean
    public Queue sendPasswordQueue() {
        return QueueBuilder
                .durable(SEND_PASSWORD_QUEUE)
                .build();
    }

    @Bean
    public Queue sendInvitationOfOrganizationQueue() {
        return QueueBuilder
                .durable(SEND_INVITATION_OF_ORGANIZATION)
                .build();
    }

    @Bean
    public Queue sendMessageQueue() {
        return QueueBuilder
                .durable(SEND_MESSAGE_QUEUE)
                .build();
    }

}
