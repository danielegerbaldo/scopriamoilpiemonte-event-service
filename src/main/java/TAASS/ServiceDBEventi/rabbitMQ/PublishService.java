package TAASS.ServiceDBEventi.rabbitMQ;

import TAASS.ServiceDBEventi.rabbitMQ.DTO.UserMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PublishService {

    private final RabbitTemplate rabbitTemplate;

    public PublishService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void publishSubscriptionUser(long userId, long eventId, boolean isSubscription) {

        System.out.println("RABBITMQ SENT: " + " UserId: " + userId + " EventId: " +eventId + " isSubscription: " + isSubscription);

        rabbitTemplate.convertAndSend("eventSubscriptionRequest",
                new UserMessage(userId,eventId,isSubscription));
    }
}