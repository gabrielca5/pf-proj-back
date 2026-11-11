package br.insper.pf.fila;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Declara as filas. Para uma fila nova: uma constante e um @Bean como o de baixo.
@Configuration
public class FilaConfig {

    public static final String FILA = "avaliacoes";

    @Bean
    public Queue fila() {
        return new Queue(FILA, true);
    }
}
