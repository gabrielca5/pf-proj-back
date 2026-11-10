package br.insper.pf.generico;

import br.insper.pf.SecurityConfig;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// CRUD completo. Apague as rotas que o enunciado nao pedir e ajuste os @PreAuthorize:
//   so ADMIN:      hasRole('ADMIN')
//   ADMIN e USER:  hasAnyRole('ADMIN', 'USER')
//   qualquer logado: apague a linha do @PreAuthorize
@RestController
@RequestMapping("/itens")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Item> listar() {
        return service.listar();
    }

    // So os itens de quem esta logado
    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Item> listarMeus(@AuthenticationPrincipal Jwt jwt) {
        return service.listarDoUsuario(jwt.getClaimAsString(SecurityConfig.CLAIM_EMAIL));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Item buscar(@PathVariable Integer id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Item cadastrar(@Valid @RequestBody Item item, @AuthenticationPrincipal Jwt jwt) {
        return service.cadastrar(item, jwt.getClaimAsString(SecurityConfig.CLAIM_EMAIL));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Item editar(@PathVariable Integer id, @Valid @RequestBody Item item) {
        return service.editar(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(@PathVariable Integer id) {
        service.excluir(id);
    }
}
