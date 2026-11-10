package br.insper.pf.avaliacao;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repository;

    public AvaliacaoService(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    public Avaliacao cadastrar(Avaliacao avaliacao, String emailAvaliador) {
        avaliacao.setId(null);
        avaliacao.setEmailAvaliador(emailAvaliador);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        return repository.save(avaliacao);
    }

    public List<Avaliacao> listar() {
        return repository.findAll();
    }
}
