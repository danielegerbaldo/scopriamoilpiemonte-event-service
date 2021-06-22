package TAASS.ServiceDBEventi.rabbitMQ;


import TAASS.ServiceDBEventi.rabbitMQ.DTO.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ListenerService {

    /*public static final Logger logger = LoggerFactory.getLogger(ListenerService.class);
    public static HashMap<Long,Utente> utente = new HashMap<Long,Utente>();

    @RabbitListener(queues = "publishUserEvent")
    public void getPojo(UserMessage message) {

        logger.info("From Queue : {}", message);

        utente.put(message.getUserId(),message.getBody());
    }*/
}