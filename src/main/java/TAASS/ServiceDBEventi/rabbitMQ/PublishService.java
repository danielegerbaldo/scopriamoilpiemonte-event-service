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


    public void requestUser(long userId) {
        rabbitTemplate.convertAndSend("requestUserEvent",
                new UserMessage(userId,null));
    }
}