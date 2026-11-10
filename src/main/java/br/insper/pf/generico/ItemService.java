package br.insper.pf.generico;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> listar() {
        return repository.findAll();
    }

    public List<Item> listarDoUsuario(String email) {
        return repository.findByEmailCriador(email);
    }

    // 404 se nao existir
    public Item buscar(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item nao encontrado"));
    }

    public Item cadastrar(Item item, String emailCriador) {
        item.setId(null);
        item.setEmailCriador(emailCriador);
        item.setDataCriacao(LocalDateTime.now());
        return repository.save(item);
    }

    // Copia so os campos que o usuario pode alterar
    public Item editar(Integer id, Item dados) {
        Item item = buscar(id);
        item.setNome(dados.getNome());
        item.setDescricao(dados.getDescricao());
        item.setValor(dados.getValor());
        item.setAtivo(dados.getAtivo());
        return repository.save(item);
    }

    public void excluir(Integer id) {
        repository.delete(buscar(id));
    }
}
