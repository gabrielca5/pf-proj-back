package br.insper.pf.fila;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Quem recebe. Roda sozinho toda vez que chega mensagem na fila.
// O que ele faz esta no corpo do metodo: aqui so imprime no log.
@Component
public class Consumidor {

    private static final Logger log = LoggerFactory.getLogger(Consumidor.class);

    @RabbitListener(queues = FilaConfig.FILA)
    public void receber(String mensagem) {
        log.info("Mensagem recebida: {}", mensagem);
    }
}
