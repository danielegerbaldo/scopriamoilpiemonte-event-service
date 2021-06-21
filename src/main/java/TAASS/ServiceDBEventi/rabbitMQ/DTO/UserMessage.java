package TAASS.ServiceDBEventi.rabbitMQ.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserMessage implements Serializable {
    long userId;
    Utente body;
}