package br.insper.pf.generico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    // Filtro pelo nome do metodo: findBy + nome do campo. Exemplos:
    // findByAtivo(Boolean ativo), findByNomeContainingIgnoreCase(String nome)
    List<Item> findByEmailCriador(String emailCriador);
}
