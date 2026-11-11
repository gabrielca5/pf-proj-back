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
// [ERROR: column "email" of relation "avaliacao" contains null values]
    public Avaliacao cadastrar(Avaliacao avaliacao,
         String emailAvaliador, String emailAvaliado,
          String titulo, String descricao,
           String prioridade
            ) {
        avaliacao.setEmail(emailAvaliado);
        avaliacao.setDescricao(descricao);
        avaliacao.setId(null);
        avaliacao.setTitulo(titulo);
        avaliacao.setPrioridade(prioridade);
        avaliacao.setEmail(emailAvaliador);
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        avaliacao.setDeletado(false);
        Avaliacao salva = repository.save(avaliacao);
        // produtor.enviar("Nova avaliacao: " + salva.getEmail() + " nota " + salva.getNota());
        return salva;
    }

    public Avaliacao deletar(Integer id) {
        Avaliacao avaliacao = repository.findById(id).orElseThrow();
        avaliacao.setDeletado(true);
        return repository.save(avaliacao);
    }
    public List<Avaliacao> listar() {
        return repository.findAll();
    }
}
