package br.insper.pf.avaliacao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository repository;

    @InjectMocks
    private AvaliacaoService service;

    @Test
    void cadastrarPreencheAvaliadorEData() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setEmailAvaliado("avaliado@empresa.com");
        avaliacao.setNota(8);
        when(repository.save(avaliacao)).thenReturn(avaliacao);

        Avaliacao salva = service.cadastrar(avaliacao, "admin@empresa.com");

        assertEquals("admin@empresa.com", salva.getEmailAvaliador());
        assertNotNull(salva.getDataAvaliacao());
    }

    @Test
    void listarDevolveTudoDoRepositorio() {
        when(repository.findAll()).thenReturn(List.of(new Avaliacao(), new Avaliacao()));

        assertEquals(2, service.listar().size());
    }
}
