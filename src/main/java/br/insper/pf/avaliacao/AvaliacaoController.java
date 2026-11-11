package br.insper.pf.avaliacao;

import br.insper.pf.SecurityConfig;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Avaliacao cadastrar(@Valid @RequestBody Avaliacao avaliacao, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString(SecurityConfig.CLAIM_EMAIL);
        return service.cadastrar(avaliacao, email, avaliacao.getEmail(), avaliacao.getTitulo(), avaliacao.getDescricao(), avaliacao.getPrioridade());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Avaliacao> listar() {
        return service.listar();
    }

    @DeleteMapping
    @PreAuthorize ("hasRole('ADMIN')")
    public Avaliacao deletar(@RequestBody Avaliacao avaliacao) {
        return service.deletar(avaliacao.getId());
    }
}
