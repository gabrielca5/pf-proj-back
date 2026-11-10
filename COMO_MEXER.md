# Como mexer (back)

Fluxo da prova: ler o enunciado, mexer, `bash run.sh deploy "msg"`, `gh run watch`, `bash run.sh check`.
O deploy leva uns 2 a 4 minutos. O teste de cada mudança roda no deploy: se um teste quebrar, nada vai para a AWS.

## Mapa

| Arquivo | Mexer quando |
|---|---|
| `avaliacao/Avaliacao.java` | campos novos ou validações (`@NotBlank`, `@Min`, `@Email`) |
| `avaliacao/AvaliacaoController.java` | rota nova, ou mudar quem pode acessar (`@PreAuthorize`) |
| `avaliacao/AvaliacaoService.java` | regra de negócio (o que o back preenche ou recusa) |
| `avaliacao/AvaliacaoRepository.java` | consulta nova no banco |
| `SecurityConfig.java` | quase nunca (só se mudar o nome dos claims) |
| `application.properties` | quase nunca (banco e Auth0 já estão certos) |
| `src/test/.../AvaliacaoServiceTest.java` | sempre que mudar o service ou os campos |
| `generico/` | modelo pronto com CRUD completo (`/itens`) para copiar |

## Receitas

### Trocar a entidade (Avaliacao vira Tarefa, rota /tarefas)
1. Renomeie a pasta `avaliacao` e as 4 classes (IntelliJ: Shift+F6; VS Code: F2 no nome da classe).
2. Em `Tarefa.java`, troque os campos.
3. No controller, troque `@RequestMapping("/tarefas")`.
4. No service, ajuste o que o back preenche sozinho.
5. Ajuste o teste (senão o deploy falha no build).
6. No front, veja o `COMO_MEXER.md` do `pf-proj-front`.
7. Se renomeou campo ou tabela e o banco reclamar de coluna: `bash run.sh reset-db ../projsoft26b.pem` e depois deploy.

### Campo novo
1. Entidade: `private String categoria;` (com `@NotBlank` se for obrigatório).
2. Teste: preencha o campo nos testes se ele for obrigatório.
3. Front: campo no form e na tabela.
O `ddl-auto=update` cria a coluna sozinho.

### Mudar quem pode acessar uma rota
No `@PreAuthorize` da rota:
```java
@PreAuthorize("hasRole('ADMIN')")                  // so ADMIN
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")       // os dois
// sem a linha: qualquer usuario logado
```

### Rota nova: listar so as do usuario logado
Repository:
```java
List<Avaliacao> findByEmailAvaliador(String emailAvaliador);
```
Service:
```java
public List<Avaliacao> listarDoAvaliador(String email) {
    return repository.findByEmailAvaliador(email);
}
```
Controller:
```java
@GetMapping("/meus")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public List<Avaliacao> meus(@AuthenticationPrincipal Jwt jwt) {
    return service.listarDoAvaliador(jwt.getClaimAsString(SecurityConfig.CLAIM_EMAIL));
}
```
Outros filtros: `findByNota(Integer nota)`, `findByNotaGreaterThan(Integer nota)`, `findByEmailAvaliadoContainingIgnoreCase(String parte)`.

### Regra de negocio (recusar com erro)
No service, antes do `save`:
```java
if (avaliacao.getEmailAvaliado().equals(emailAvaliador)) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao pode avaliar a si mesmo");
}
```
Imports: `org.springframework.http.HttpStatus` e `org.springframework.web.server.ResponseStatusException`.
Use `HttpStatus.NOT_FOUND` (404), `FORBIDDEN` (403) ou `CONFLICT` (409) conforme o caso.

### Editar e excluir
Copie `editar`, `excluir` e `buscar` do `generico/` (controller e service).

### Role nova (ex: GERENTE)
1. Auth0: User Management, Roles, crie `GERENTE` e atribua a um usuario.
2. Back: `hasRole('GERENTE')` no `@PreAuthorize`.
Nada mais, a Action ja manda qualquer role no token.

### Teste novo
Copie um teste do `AvaliacaoServiceTest`. Padrao: `when(repository.metodo(...)).thenReturn(...)`, chama o service, `assertEquals(esperado, real)`.
Para esperar erro: `assertThrows(ResponseStatusException.class, () -> service.cadastrar(...))`.

## Comandos (rodar na pasta do back)

| Comando | Para que |
|---|---|
| `bash run.sh deploy "msg"` | commit + push, dispara o deploy |
| `gh run watch` | acompanhar o deploy |
| `bash run.sh check` | ver se a AWS responde (esperado: 401) |
| `bash run.sh logs ../projsoft26b.pem` | logs do back na AWS |
| `bash run.sh reset-db ../projsoft26b.pem` | apagar o banco (se mudou campos) |
| `bash run.sh prune ../projsoft26b.pem` | liberar disco da AWS |
| `bash run.sh disk ../projsoft26b.pem` | ver o disco da AWS |

## Erros comuns

| Sintoma | Causa |
|---|---|
| 401 | sem token, ou issuer/audience errados em `application.properties` |
| 403 | token sem a role certa: confira o `@PreAuthorize` e a role do usuario no Auth0 |
| 400 | validacao da entidade (a resposta diz qual campo) |
| 404 | rota errada, ou `findById` sem resultado |
| 500 | olhe `bash run.sh logs` |
| Deploy vermelho no passo "Build e testes" | teste quebrado ou erro de compilacao, o log diz a linha |
| Deploy vermelho em "Deploy na AWS" com `no space left` | `bash run.sh prune ...` e `gh run rerun --failed` |
| Coluna antiga reclamando no banco | `bash run.sh reset-db ...` |

Para debugar 401 e 403: descomente `logging.level.org.springframework.security=DEBUG` no `application.properties`.
