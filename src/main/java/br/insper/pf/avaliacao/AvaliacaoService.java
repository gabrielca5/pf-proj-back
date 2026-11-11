package br.insper.pf.avaliacao;

import br.insper.pf.fila.Produtor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository repository;
    private final Produtor produtor;

    public AvaliacaoService(AvaliacaoRepository repository, Produtor produtor) {
        this.repository = repository;
        this.produtor = produtor;

    }

    public Avaliacao cadastrar(Avaliacao avaliacao, String emailAvaliador) {
        avaliacao.setComentarios(avaliacao.getComentarios());
        avaliacao.setId(null);
        avaliacao.setTitulo(avaliacao.getTitulo());
        avaliacao.setPrioridade(avaliacao.getPrioridade());
        avaliacao.setNota(avaliacao.getNota());
        avaliacao.setEmail(emailAvaliador);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        Avaliacao salva = repository.save(avaliacao);
        produtor.enviar("Nova avaliacao: " + salva.getEmail() + " nota " + salva.getNota());
        return salva;
    }

    public List<Avaliacao> listar() {
        return repository.findAll();
    }
}
