package br.insper.pf.fila;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

// Quem envia. Uso: produtor.enviar("texto")
@Component
public class Produtor {

    private final RabbitTemplate rabbitTemplate;

    public Produtor(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviar(String mensagem) {
        rabbitTemplate.convertAndSend(FilaConfig.FILA, mensagem);
    }
}
