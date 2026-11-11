package br.insper.pf.avaliacao;

import br.insper.pf.fila.Produtor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository repository;

    @Mock
    private Produtor produtor;

    @InjectMocks
    private AvaliacaoService service;

    @Test
    void cadastrarPreencheAvaliadorEData() {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setEmail("avaliado@empresa.com");
        avaliacao.setNota(8);
        when(repository.save(avaliacao)).thenReturn(avaliacao);

        Avaliacao salva = service.cadastrar(avaliacao, "admin@empresa.com");

        assertEquals("admin@empresa.com", salva.getEmail());
        assertNotNull(salva.getDataAvaliacao());
        verify(produtor).enviar(anyString());
    }

    @Test
    void listarDevolveTudoDoRepositorio() {
        when(repository.findAll()).thenReturn(List.of(new Avaliacao(), new Avaliacao()));

        assertEquals(2, service.listar().size());
    }
}
